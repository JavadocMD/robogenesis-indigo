package game

import indigo._

object Selection:
  val scannerRecharge = Seconds(1.0)

  object Tasks:
    def mismatched: Task = Task.onComplete(
      duration = scannerRecharge,
      model => Outcome(model.copy(selected = Set.empty))
    )

    def matched(p: Part): Task = Task.onComplete(
      duration = scannerRecharge,
      model => Outcome(model.copy(parts = p :: model.parts, selected = Set.empty))
      // TODO: animate the junk piles getting lifted off the conveyor
      // which also removes them so they can't be collected again
    )
  end Tasks

  def partsList(selected: Set[JunkId], junk: List[Junk]): List[Option[Part]] =
    selected.toList.map(id => junk.find(_.id == id).get.contents)

  def getMatch(selected: Set[JunkId], junk: List[Junk]): Option[Part] =
    partsList(selected, junk) match
      case Some(p1) :: Some(p2) :: _ if p1 == p2 => Some(p1)
      case _                                     => None

  // Returns Some on successful selection.
  def trySelect(model: Model, x: Int, y: Int): Option[Outcome[Model]] =
    for
      // max number selected: 2
      m <- Some(model) if model.selected.size < 2
      // hit test for selection target (junk piles)
      c <- m.junk.find(_.bounds.isPointWithin(x, y))
      // updated selection
      s = m.selected + c.id
    yield
      val events = getMatch(s, m.junk) match
        case Some(p)             => List(GameEvent.AddTask(Tasks.matched(p)))
        case None if s.size == 2 => List(GameEvent.AddTask(Tasks.mismatched))
        case _                   => Nil
      Outcome(m.copy(selected = s)).addGlobalEvents(events)

  def select(model: Model, x: Int, y: Int): Outcome[Model] =
    trySelect(model, x, y).getOrElse(Outcome(model))

end Selection
