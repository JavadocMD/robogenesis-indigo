package game

import game.Assets
import indigo._
import indigoextras.subsystems._

import GameEvent._
import GameScene.LayerKey

enum Dialog:
  case None
  case Anim(actor: Dialog.Actor, offset: Double, shade: Double)
  case Line(actor: Dialog.Actor, text: String)

object Dialog:
  import Config.{vw, vh}

  sealed trait Actor(val graphic: Graphic[_], val voice: PlaySound)

  object Actor:
    case object Radio extends Actor(Assets.radio.graphic, Assets.static.play)
    case object Robo  extends Actor(Assets.robo.graphic, Assets.roboBleep.play)

  object Tasks:
    val delay1 = Seconds(0.2)
    val delay2 = Seconds(0.4)

    def startDialog(using actor: Actor): Task = Task.Sequence(
      Task.Interpolate(
        delay1,
        Interpolate.powIn(2),
        (alpha, model) =>
          val offset = vw * (1.0 - alpha)
          model.copy(dialog = Anim(actor, offset, 1.0))
      ),
      Task.Delay(delay2)
    )

    def endDialog(using actor: Actor): Task = Task.Sequence(
      Task.Then(_.copy(dialog = Anim(actor, 0, 1.0))),
      Task.Delay(delay2),
      Task.Interpolate(
        delay1,
        Interpolate.powIn(2),
        (alpha, model) =>
          val offset = vw * alpha
          model.copy(dialog = Anim(actor, offset, 1.0))
      ),
      Task.Delay(delay2)
    )

    def speak(text: String)(using actor: Actor): Task = Task.Sequence(
      Task.Then(_.copy(dialog = Line(actor, text))),
      Task.Event(actor.voice),
      Task.Delay(delay1),
      Task.WaitForClick
    )

    def turnOnTheLights(duration: Seconds): Task = Task.Sequence(
      Task.Interpolate(
        duration,
        Interpolate.powInOut(2),
        (alpha, model) =>
          val opacity = 1.0 - alpha
          model.copy(dialog = Anim(Actor.Radio, vw, opacity))
      )
    )

    def turnOffTheLights(duration: Seconds): Task = Task.Sequence(
      Task.Interpolate(
        duration,
        Interpolate.powInOut(2),
        (alpha, model) =>
          val opacity = alpha
          model.copy(dialog = Anim(Actor.Radio, vw, opacity))
      )
    )

  end Tasks

  def scene(context: FrameContext[GameData], model: Model, viewModel: Unit): SceneUpdateFragment =
    model.dialog match
      case Dialog.None                       => SceneUpdateFragment.empty
      case Dialog.Anim(actor, offset, shade) => drawSpeech(actor, "", offset.toInt, shade)
      case Dialog.Line(actor, text)          => drawSpeech(actor, text, 0, 1.0)

  val lime_35         = RGBA.fromHexString("daffce").withAlpha(0.35)
  val bubble          = Shape.Box(Rectangle(0, 0, 800, 350), Fill.Color(lime_35))
  val shadeMaxOpacity = 0.6
  def shadeBox(alpha: Double) =
    val color = RGBA.Black.withAlpha(shadeMaxOpacity * alpha)
    Shape.Box(Rectangle(0, 0, vw, vh), Fill.Color(color))

  def drawSpeech(actor: Actor, text: String, offset: Int, shade: Double) =
    // TODO: render text!
    var nodes = List(
      bubble.moveTo(200 + offset, 75),
      actor.graphic.moveTo(0 - offset, 0)
    )
    if (shade > 0) {
      nodes ::= shadeBox(shade)
    }
    SceneUpdateFragment(Layer(LayerKey.dialog, nodes))

end Dialog
