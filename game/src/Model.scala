package game

import indigo._

case class Model(
    beltSpeed: Double = 0,
    parts: List[Part] = Nil,
    junk: List[Junk] = Nil,
    selected: Set[JunkId] = Set.empty,
    tasks: List[Task] = Nil
)

object Model:
  // def initial = Model()
  def initial = Model( // tmp
    junk = List(
      Junk(JunkId(1), Some(Part.Head), 100, 75),
      Junk(JunkId(2), None, 200, 275),
      Junk(JunkId(3), Some(Part.Head), 300, 475)
    )
  )

trait Part:
  val graphic: Graphic[Material.Bitmap]
  val graphicSmall: Graphic[Material.Bitmap]

object Part:
  case object Treds extends Part:
    val graphic      = Assets.treds.graphic
    val graphicSmall = Assets.treds.graphic.scaleBy(0.5, 0.5).moveTo(25, 25)

  case object Head extends Part:
    val graphic      = Assets.head.graphic
    val graphicSmall = Assets.head.graphic.scaleBy(0.5, 0.5).moveTo(25, 25)

  case object Body extends Part:
    val graphic      = Assets.body.graphic
    val graphicSmall = Assets.body.graphic.scaleBy(0.5, 0.5).moveTo(25, 25)

  val parts = Seq(Treds, Head, Body)

  def random(dice: Dice): Part = parts(dice.roll(parts.length) - 1)

end Part
