package game

import game.GameEvent._
import indigo._
import indigo.scenes._
import indigoextras.subsystems.FPSCounter

object GameScene extends Scene[GameData, Model, Unit]:

  type SceneModel     = Model
  type SceneViewModel = Unit

  val name: SceneName                 = SceneName("game")
  val modelLens: Lens[Model, Model]   = Lens.keepLatest
  val viewModelLens: Lens[Unit, Unit] = Lens.keepLatest
  val eventFilters: EventFilters      = EventFilters.Permissive

  val subSystems: Set[SubSystem] =
    (for y <- Config.beltYs yield Belt(Config.vw, y)) + FPSCounter(Point(0, 0), 60, LayerKey.ui)

  def updateModel(context: FrameContext[GameData], model: Model): GlobalEvent => Outcome[Model] =
    case KeyboardEvent.KeyDown(Key.ENTER) => // temp
      Outcome(model).addGlobalEvents(SetBeltSpeed(-100), SetFactory(on = true))
    case KeyboardEvent.KeyDown(Key.KEY_P) => // temp
      Outcome(model).addGlobalEvents(CollectPart(Part.random(context.dice)))
    case KeyboardEvent.KeyDown(Key.KEY_M) => // temp
      println(model)
      Outcome(model)
    case MouseEvent.Click(x, y) => Selection.select(model, x, y)
    case SetBeltSpeed(v)        => Outcome(model.copy(beltSpeed = v))
    case SetFactory(on)         => Outcome(model.copy(factory = model.factory.copy(on = on)))
    case CollectPart(p)         => Outcome(model.copy(parts = p :: model.parts))
    case AddTask(t)             => Outcome(model.copy(tasks = t :: model.tasks))
    case FrameTick =>
      List(
        Junk.update(_, context.delta),
        JunkFactory.update(_, context.delta, context.dice),
        Task.update(_, context.delta)
      ).foldLeft(Outcome(model)) { (acc, curr) =>
        acc.flatMap(curr)
      }
    case _ => Outcome(model)
  end updateModel

  def updateViewModel(
      context: FrameContext[GameData],
      model: Model,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  object LayerKey:
    val background = BindingKey("background")
    val ui         = BindingKey("ui")
    val belts      = BindingKey("belts")
    val junk       = BindingKey("junk")

  val baseScene = SceneUpdateFragment(
    Layer(LayerKey.background, Background.nodes),
    Layer(LayerKey.belts),
    Layer(LayerKey.junk),
    Layer(LayerKey.ui)
  )

  def present(c: FrameContext[GameData], m: Model, vm: Unit): Outcome[SceneUpdateFragment] =
    Outcome {
      baseScene |+| Inventory.scene(c, m, vm) |+| Junk.scene(c, m, vm)
    }

end GameScene

/** Scene nodes for the entirely static background. */
object Background:
  import Config.{vw, vh}

  val background  = Graphic(vw, vh, Assets.background.material.tile)
  val lime_35     = RGBA.fromHexString("daffce").withAlpha(0.35)
  val topStrip    = Shape.Box(Rectangle(0, 0, vw, 75), Fill.Color(lime_35))
  val bottomStrip = Shape.Box(Rectangle(0, vh - 125, vw, 125), Fill.Color(lime_35))
  val nodes       = List(background, topStrip, bottomStrip)

end Background
