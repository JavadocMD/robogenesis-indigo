package game

import game.Assets
import indigo._
import indigoextras.datatypes.DecreaseWrapAt
import indigoextras.subsystems._

case class BeltModel(beltOffset: DecreaseWrapAt)

object BeltModel {
  val beltWidth = 400
  val gearWidth = 400
  val count     = (0 until 4).toList // render this many of each

  val initial = BeltModel(
    beltOffset = DecreaseWrapAt(100, -beltWidth)
  )
}

class Belt(y: Int, speed: Double) extends SubSystem {
  import BeltModel._

  type EventType      = FrameTick
  type SubSystemModel = BeltModel

  def eventFilter: GlobalEvent => Option[FrameTick] = {
    case FrameTick => Some(FrameTick)
    case _         => None
  }

  val initialModel: Outcome[BeltModel] = Outcome(BeltModel.initial)

  def update(context: SubSystemFrameContext, model: BeltModel): FrameTick => Outcome[BeltModel] = { case FrameTick =>
    Outcome {
      model.copy(beltOffset = model.beltOffset.update(context.delta))
    }
  }

  val gears = for { i <- count } yield Assets.gears.moveTo(i * gearWidth, y)

  def present(context: SubSystemFrameContext, model: BeltModel): Outcome[SceneUpdateFragment] = Outcome {
    val x0    = model.beltOffset.value.toInt
    val belts = for { i <- count } yield Assets.belt.moveTo(i * beltWidth + x0, y)
    SceneUpdateFragment(Layer(BindingKey("belts"), gears ++ belts))
  }
}
