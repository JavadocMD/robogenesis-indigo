package game

import indigo.shared.events.GlobalEvent

sealed trait GameEvent extends GlobalEvent with Product with Serializable

object GameEvent:
  case class SetBeltSpeed(v: Double) extends GameEvent
  case class SetFactory(on: Boolean) extends GameEvent
  case class CollectPart(p: Part)    extends GameEvent
  case class AddTask(t: Task)        extends GameEvent
