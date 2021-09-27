package game

import indigo._
import game.Junk._

enum Match:
  case Incomplete
  case Mismatch
  case Matched(j1: Junk, j2: Junk, p: Part)

enum Selection:
  case Empty
  case One(j1: Junk.Id)
  case Two(j1: Junk.Id, j2: Junk.Id)

object Selection:

  extension (s: Selection)
    /** Add ID to the selection if the new selection would be valid and novel. */
    def append(newId: Junk.Id): Option[Selection] = s match
      case Empty                  => Some(One(newId))
      case One(id) if id != newId => Some(Two(id, newId))
      case _                      => None

    /** If the current selection represents a match, returns it. */
    def getMatch(model: Model): Match = s match
      case Empty  => Match.Incomplete
      case One(_) => Match.Incomplete
      case Two(j1, j2) =>
        val m = for {
          j1 <- model.getJunk(j1)
          j2 <- model.getJunk(j2)
          p1 <- j1.contents
          p2 <- j2.contents
          if p1 == p2
        } yield Match.Matched(j1, j2, p1)
        m.getOrElse(Match.Mismatch)

    /** True if the given ID is in the selection. */
    def isSelected(id: Junk.Id): Boolean = s match
      case Empty       => false
      case One(j1)     => id == j1
      case Two(j1, j2) => id == j1 || id == j2

    /** Filters the selection by the given function. */
    def filter(f: Junk.Id => Boolean): Selection = s match
      case Empty            => s
      case One(id) if f(id) => s
      case One(_)           => Empty
      case Two(id1, id2) =>
        (f(id1), f(id2)) match
          case (true, true)   => s
          case (true, false)  => One(id1)
          case (false, true)  => One(id2)
          case (false, false) => Empty

    /** Filters the selection to IDs that are still present in the model. */
    def filterValid(model: Model): Selection =
      s.filter(model.getJunk(_).isDefined)

  // Returns Some on successful selection.
  def trySelect(model: Model, x: Int, y: Int): Option[Outcome[Model]] =
    for
      // hit test for selection target (junk piles)
      j <- model.junk.find(_.bounds.isPointWithin(x, y))
      // updated selection, first culling removed junk piles
      s <- model.selected.filterValid(model).append(j.id)
    yield
      val events = s.getMatch(model) match
        case Match.Matched(j1, j2, p) => GameEvent.AddTask(Tasks.matched(j1, j2, p)) :: Nil
        case Match.Mismatch           => GameEvent.AddTask(Tasks.mismatched) :: Nil
        case Match.Incomplete         => Nil
      Outcome(model.copy(selected = s))
        .addGlobalEvents(Assets.scanner.play :: events)

  def select(model: Model, x: Int, y: Int): Outcome[Model] =
    trySelect(model, x, y).getOrElse(Outcome(model))

  object Tasks:
    val scannerRecharge = Seconds(1.0)
    val liftTime        = Seconds(0.5)

    def mismatched: Task = Task.Sequence(
      Task.Delay(scannerRecharge),
      Task.Then(_.copy(selected = Selection.Empty))
    )

    def matched(j1: Junk, j2: Junk, p: Part): Task =
      Task.Sequence(
        Task.Delay(scannerRecharge),
        Task.Event(Assets.capture.play),
        Task.Interpolate(
          liftTime,
          Interpolate.powIn(3),
          (alpha, model) =>
            val dy = -800.0 * alpha
            model.liftJunk(j1.id, j1.y + dy).liftJunk(j2.id, j2.y + dy)
        ),
        Task.Then(_.collectJunk(j1.id, j2.id, p))
      )

  end Tasks

end Selection
