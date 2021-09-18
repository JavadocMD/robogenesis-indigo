package game

import indigo._

opaque type JunkId = Int
object JunkId:
  def apply(id: Int): JunkId                     = id
  given CanEqual[JunkId, JunkId]                 = CanEqual.derived
  given CanEqual[Option[JunkId], Option[JunkId]] = CanEqual.derived

class Junk(
    val id: JunkId,
    val contents: Option[Part],
    val x: Double,
    val y: Double,
    val initialY: Double,
    val conveyed: Boolean
):
  val bounds = Rectangle(x.toInt, y.toInt, 100, 100)

  def moveTo(x: Double, y: Double)    = Junk(id, contents, x, y, initialY, conveyed)
  def withConveyed(conveyed: Boolean) = Junk(id, contents, x, y, initialY, conveyed)

  def draw(isSelected: Boolean): SceneNode =
    val node = (isSelected, contents) match
      case (false, _)         => Assets.junk1.graphic
      case (true, None)       => Assets.junk2.graphic
      case (true, Some(part)) => Group(part.graphicSmall, Assets.junk2.graphic)
    node.moveTo(x.toInt, y.toInt)

end Junk

object Junk:
  val EmptyGraphic = Shape.Box(Rectangle(0, 0, 100, 100), Fill.None)

  def update(model: Model, delta: Seconds): Outcome[Model] =
    val dx = delta.toDouble * model.beltSpeed
    val js =
      for j <- model.junk if j.x > -100
      yield
        if !j.conveyed then j
        else j.moveTo(j.x + dx, j.y)
    Outcome(model.copy(junk = js))

  def scene(context: FrameContext[GameData], model: Model, viewModel: Unit): SceneUpdateFragment =
    val junk = for
      j <- model.junk
      isSelected = model.selected.contains(j.id)
    yield j.draw(isSelected)
    SceneUpdateFragment(Layer(GameScene.LayerKey.junk, junk))

end Junk
