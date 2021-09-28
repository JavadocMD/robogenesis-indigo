package game

import indigo._

case class JunkFactory(
    levels: List[Int],
    partChance: Double,
    partPeriod: Seconds,
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
    if !model.factoryOn then Outcome(model)
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
