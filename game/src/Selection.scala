package game

import indigo._

object Selection:

  object Tasks:
    val scannerRecharge = Seconds(1.0)
    val liftTime        = Seconds(0.5)

    def mismatched: Task = Task.Sequence(
      Task.Delay(scannerRecharge),
      Task.Then(_.copy(selected = Set.empty))
    )

    def matched(p: Part, id1: JunkId, y1: Double, id2: JunkId, y2: Double): Task =
      Task.Sequence(
        Task.Delay(scannerRecharge),
        Task.ThenE((_, List(Assets.capture.play))),
        Task.Interpolate(
          liftTime,
          Interpolate.powIn(3),
          (alpha, model) =>
            val dy = -800.0 * alpha
            model.liftJunk(id1, y1 + dy).liftJunk(id2, y2 + dy)
        ),
        Task.Then(_.collectJunk(id1, id2, p))
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
      // filter for unique only
      if !m.selected.contains(c.id)
    yield
      // updated selection
      val s = m.selected + c.id
      val events = getMatch(s, m.junk) match
        case Some(p) =>
          val j1 = s.head
          val y1 = model.getY(j1)
          val j2 = s.tail.head
          val y2 = model.getY(j2)
          List(GameEvent.AddTask(Tasks.matched(p, j1, y1, j2, y2)))
        case None if s.size == 2 => List(GameEvent.AddTask(Tasks.mismatched))
        case _                   => Nil
      Outcome(m.copy(selected = s))
        .addGlobalEvents(Assets.scanner.play :: events)

  def select(model: Model, x: Int, y: Int): Outcome[Model] =
    trySelect(model, x, y).getOrElse(Outcome(model))

end Selection
