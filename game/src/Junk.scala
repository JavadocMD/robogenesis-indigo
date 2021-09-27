package game

import indigo._

case class Junk(
    id: Junk.Id,
    contents: Option[Part],
    x: Double,
    y: Double,
    conveyed: Boolean
):
  lazy val bounds = Rectangle(x.toInt, y.toInt, 100, 100)

  def moveTo(x: Double, y: Double)    = copy(x = x, y = y)
  def withConveyed(conveyed: Boolean) = copy(conveyed = conveyed)

  def draw(isSelected: Boolean): SceneNode =
    val node = (isSelected, contents) match
      case (false, _)         => Assets.junk1.graphic
      case (true, None)       => Assets.junk2.graphic
      case (true, Some(part)) => Group(part.graphicSmall, Assets.junk2.graphic)
    node.moveTo(x.toInt, y.toInt)

end Junk

object Junk:

  opaque type Id = Int
  def Id(value: Int): Id = value

  extension (id: Id) def toInt: Int = id

  extension (model: Model)
    def getJunk(id: Junk.Id): Option[Junk] = model.junk.find(_.id == id)

    // warning: no-op if id isn't present
    def liftJunk(id: Junk.Id, y: Double): Model =
      val i = model.junk.indexWhere(_.id == id)
      if i < 0 then model
      else
        val j    = model.junk(i)
        val newJ = j.withConveyed(false).moveTo(j.x, y)
        model.copy(junk = model.junk.updated(i, newJ))

    def collectJunk(id1: Junk.Id, id2: Junk.Id, part: Part): Model =
      model.copy(
        parts = part :: model.parts,
        junk = model.junk.filter(j => j.id != id1 && j.id != id2),
        selected = Selection.Empty
      )

  end extension

  // Moves junk at belt speed if on conveyor; removes junk that goes off screen.
  def update(model: Model, delta: Seconds): Outcome[Model] =
    val dx = delta.toDouble * model.beltSpeed
    val js =
      for j <- model.junk if j.x > -100
      yield
        if !j.conveyed then j
        else j.moveTo(j.x + dx, j.y)
    Outcome(model.copy(junk = js))

  def scene(context: FrameContext[GameData], model: Model, viewModel: Unit): SceneUpdateFragment =
    val junk =
      for j <- model.junk
      yield j.draw(model.selected.isSelected(j.id))
    SceneUpdateFragment(Layer(GameScene.LayerKey.junk, junk))

end Junk

case class JunkFactory(
    levels: List[Int],
    partChance: Double,
    partPeriod: Seconds,
    on: Boolean = false,
    timeSince: Seconds = Seconds.zero,
    nextId: Int = 0
):
  def randomLevel(dice: Dice): Int = levels(dice.roll(levels.size) - 1)
  def createJunk(dice: Dice): Junk =
    val id   = Junk.Id(nextId)
    val part = if dice.rollDouble < partChance then Some(Part.random(dice)) else None
    val y    = randomLevel(dice) + dice.roll(41) - 21 // vary by [-20,+20]
    Junk(id, part, Config.vw, y, true)

end JunkFactory

object JunkFactory:
  val initial = JunkFactory(Config.beltYs.toList, 0.6, Seconds(1.0))

  // Create a new Junk pile if it's time.
  def update(model: Model, delta: Seconds, dice: Dice): Outcome[Model] =
    if !model.factory.on then Outcome(model)
    else
      val t = model.factory.timeSince + delta
      Outcome {
        if t < model.factory.partPeriod then updateIdle(model, t)
        else updateCreate(model, t, dice)
      }

  private[this] def updateIdle(model: Model, timeSince: Seconds): Model =
    model.copy(factory = model.factory.copy(timeSince = timeSince))

  private[this] def updateCreate(model: Model, timeSince: Seconds, dice: Dice): Model =
    val f = model.factory
    model.copy(
      junk = f.createJunk(dice) :: model.junk,
      factory = f.copy(
        timeSince = timeSince % f.partPeriod,
        nextId = f.nextId + 1
      )
    )

end JunkFactory
