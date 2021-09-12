package game

import game.GameEvent._
import indigo._
import indigo.scenes._

object GameScene extends Scene[GameData, Model, Unit] {
  import Main.{vw, vh}

  type SceneModel     = Model
  type SceneViewModel = Unit

  val name: SceneName                 = SceneName("game")
  val modelLens: Lens[Model, Model]   = Lens.keepLatest
  val viewModelLens: Lens[Unit, Unit] = Lens.keepLatest
  val eventFilters: EventFilters      = EventFilters.Permissive

  val subSystems: Set[SubSystem] = Set(
    Belt(vw, 75),
    Belt(vw, 275),
    Belt(vw, 475)
  )

  def updateModel(context: FrameContext[GameData], model: Model): GlobalEvent => Outcome[Model] = {
    case KeyboardEvent.KeyDown(Key.ENTER) => // temp
      Outcome(model).addGlobalEvents(SetBeltSpeed(-100))
    case KeyboardEvent.KeyDown(Key.KEY_P) => // temp
      Outcome(model).addGlobalEvents(CollectPart(Part.random(context.dice)))
    case KeyboardEvent.KeyDown(Key.KEY_M) => // temp
      println(model)
      Outcome(model)
    case MouseEvent.Click(x, y) => Selection.trySelect(model, x, y)
    case SetBeltSpeed(v)        => Outcome(model.copy(beltSpeed = v))
    case CollectPart(p)         => Outcome(model.copy(parts = p :: model.parts))
    case AddTask(t)             => Outcome(model.copy(tasks = t :: model.tasks))
    case FrameTick =>
      List(
        Junk.update(_, context.delta),
        Task.update(_, context.delta)
      ).foldLeft(Outcome(model)) { (acc, curr) =>
        acc.flatMap(curr)
      }
    case _ => Outcome(model)
  }

  def updateViewModel(
      context: FrameContext[GameData],
      model: Model,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  object LayerKey {
    val background = BindingKey("background")
    val ui         = BindingKey("ui")
    val belts      = BindingKey("belts")
    val junk       = BindingKey("junk")
  }

  val baseScene = SceneUpdateFragment(
    Layer(LayerKey.background, Background.nodes),
    Layer(LayerKey.ui),
    Layer(LayerKey.belts),
    Layer(LayerKey.junk)
  )

  def present(c: FrameContext[GameData], m: Model, vm: Unit): Outcome[SceneUpdateFragment] =
    Outcome {
      baseScene |+| Inventory.scene(c, m, vm) |+| Junk.scene(c, m, vm)
    }
}

/** Scene nodes for the entirely static background. */
object Background {
  import Main.{vw, vh}

  val background  = Graphic(vw, vh, Assets.background.material.tile)
  val lime_35     = RGBA.fromHexString("daffce").withAlpha(0.35)
  val topStrip    = Shape.Box(Rectangle(0, 0, vw, 75), Fill.Color(lime_35))
  val bottomStrip = Shape.Box(Rectangle(0, vh - 125, vw, 125), Fill.Color(lime_35))

  val nodes = List(background, topStrip, bottomStrip)
}
