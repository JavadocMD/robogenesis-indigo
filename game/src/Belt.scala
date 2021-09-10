package game

import game.Assets
import indigo._
import indigoextras.datatypes.{TimeVaryingValue, DecreaseWrapAt}
import indigoextras.subsystems._

case class BeltModel(beltOffset: TimeVaryingValue)

object BeltModel {
  val beltWidth = 400
  val gearWidth = 400
  val count     = (0 until 4).toList // render this many of each

  val initial = BeltModel(
    // TODO: actually I want to control this with a global belt speed (view model?)
    beltOffset = DecreaseWrapAt(100, -beltWidth)
  )
}

class Belt(y: Int) extends SubSystem {
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

  val gears = for { i <- count } yield Assets.gears.graphic.moveTo(i * gearWidth, y)

  def present(context: SubSystemFrameContext, model: BeltModel): Outcome[SceneUpdateFragment] = Outcome {
    val x0    = model.beltOffset.value.toInt
    val belts = for { i <- count } yield Assets.belt.graphic.moveTo(i * beltWidth + x0, y)
    SceneUpdateFragment(Layer(BindingKey("belts"), gears ++ belts))
  }
}
