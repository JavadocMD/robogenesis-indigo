package game

import indigo._

object Selection {
  def partsList(selected: Set[JunkId], junk: List[Junk]): List[Option[Part]] =
    selected.toList.map(id => junk.find(_.id == id).get.contents)

  def checkMatch(selected: Set[JunkId], junk: List[Junk]): List[GlobalEvent] =
    if (selected.size != 2) Nil
    else
      partsList(selected, junk) match {
        case Some(p1) :: Some(p2) :: _ if p1 == p2 => List(GameEvent.CollectPart(p1))
        case _                                     => Nil
      }

  def update(model: Model, clicked: JunkId): Outcome[Model] = {
    // Update selection.
    val next =
      if (model.selected.size < 2) model.selected + clicked
      else model.selected

    // Check for match.
    val events = Selection.checkMatch(next, model.junk)

    Outcome(model.copy(selected = next)).addGlobalEvents(events)
  }
}
