package game

import indigo._
import indigo.scenes._

/** Draws scene nodes for the player's inventory. */
object Inventory:
  import Config.{vw, vh}

  val x0 = 570 // x-coord of first item
  val dx = 125 // separation between items

  val partBoxes =
    for i <- List.range(0, 5)
    yield Assets.partbox.graphic.moveTo(x0 + dx * i, vh - 120)

  def scene(context: FrameContext[GameData], model: Model, viewModel: Unit): SceneUpdateFragment =
    // draw with 5px xy offset b/c partbox is 10px larger than parts
    val parts = for {
      (p, i) <- model.parts.zipWithIndex
    } yield p.graphic.moveTo(x0 + 5 + dx * i, vh - 115)

    val nodes = partBoxes ++ parts

    SceneUpdateFragment(Layer(GameScene.LayerKey.ui, nodes))

end Inventory
