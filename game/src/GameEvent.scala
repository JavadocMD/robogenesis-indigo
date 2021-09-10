package game

import indigo.shared.events.GlobalEvent

sealed trait GameEvent extends GlobalEvent with Product with Serializable

object GameEvent {
  case class SetBeltSpeed(v: Double) extends GameEvent
  case class CollectPart(p: Part)    extends GameEvent
}
