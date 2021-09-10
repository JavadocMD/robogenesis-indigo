package game

import indigo._

case class Model(
    beltSpeed: Double = 0,
    parts: List[Part] = Nil
) {
  def collectPart(p: Part) = this.copy(parts = p :: parts)
}

object Model {
  def initial = Model()
}

trait Part {
  val graphic: Graphic[Material.Bitmap]
}

object Part {
  case object Treds extends Part {
    val graphic = Assets.treds.graphic
  }
  case object Head extends Part {
    val graphic = Assets.head.graphic
  }
  case object Body extends Part {
    val graphic = Assets.body.graphic
  }

  val parts = Seq(Treds, Head, Body)

  def random(dice: Dice): Part = parts(dice.roll(parts.length) - 1)
}
