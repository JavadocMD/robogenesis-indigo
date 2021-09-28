package game

import game.Assets
import game.GameEvent._
import indigo._
import indigoextras.subsystems._

object Belt:
  val beltWidth = 400
  val gearWidth = 400

  case class Model(speed: Double = 0, x: Double = 0):
    def setSpeed(v: Double) = this.copy(speed = v)
    def move(t: Seconds) =
      val tmpX = x + t.toDouble * speed
      val newX = if (tmpX > -beltWidth) tmpX else tmpX + beltWidth // wrap-around
      this.copy(x = newX)

end Belt

/** Animates the conveyor belt graphic according to the current speed. */
class Belt(viewWidth: Int, y: Int) extends SubSystem:
  import Belt._

  type EventType      = GlobalEvent
  type SubSystemModel = Model

  def eventFilter: GlobalEvent => Option[EventType] =
    case FrameTick       => Some(FrameTick)
    case e: SetBeltSpeed => Some(e)
    case _               => None

  val initialModel: Outcome[Model] = Outcome(Model())

  def update(context: SubSystemFrameContext, model: Model): EventType => Outcome[Model] =
    case SetBeltSpeed(v) => Outcome(model.setSpeed(v))
    case FrameTick       => Outcome(model.move(context.delta))
    case _               => Outcome(model)

  // render this many of each
  val count = List.range(0, viewWidth / beltWidth + 1)
  // the gear graphics are static
  val gears = for i <- count yield Assets.gears.graphic.moveTo(i * gearWidth, y)

  def present(context: SubSystemFrameContext, model: Model): Outcome[SceneUpdateFragment] = Outcome {
    val xOffset = model.x.toInt
    val belts   = for i <- count yield Assets.belt.graphic.moveTo(i * beltWidth + xOffset, y)
    SceneUpdateFragment(Layer(GameScene.LayerKey.belts, gears ++ belts))
  }

end Belt
