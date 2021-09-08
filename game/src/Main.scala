package game

import indigo._
import indigo.scenes._
import scala.scalajs.js.annotation.JSExportTopLevel

final case class BootData(
    assetPath: String,
    viewport: GameViewport
)

final case class GameData(
    viewport: GameViewport
)

@JSExportTopLevel("IndigoGame")
object Main extends IndigoGame[BootData, GameData, Unit, Unit] {

  def initialScene(bootData: BootData): Option[SceneName] = None

  def scenes(bootData: BootData): NonEmptyList[Scene[GameData, Unit, Unit]] = NonEmptyList {
    GameScene(bootData.viewport)
  }

  val eventFilters: EventFilters = EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData]] = Outcome {
    val assetPath = flags.getOrElse("assetPath", "")
    val vw        = flags.get("vw").map(_.toInt).getOrElse(1200)
    val vh        = flags.get("vh").map(_.toInt).getOrElse(800)
    val viewport  = GameViewport(vw, vh)

    val data = BootData(assetPath, viewport)
    val config = GameConfig(
      clearColor = RGBA.Black,
      frameRate = 60,
      magnification = 1,
      viewport = data.viewport
    )

    BootResult(config, data)
      .withAssets(Assets.load(assetPath))
  }

  def initialModel(startupData: GameData): Outcome[Unit] = Outcome(())

  def initialViewModel(startupData: GameData, model: Unit): Outcome[Unit] = Outcome(())

  def setup(bootData: BootData, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[GameData]] = Outcome {
    val data = GameData(bootData.viewport)
    Startup.Success(data)
  }

  def updateModel(context: FrameContext[GameData], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def updateViewModel(context: FrameContext[GameData], model: Unit, viewModel: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  def present(context: FrameContext[GameData], model: Unit, viewModel: Unit): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)
}

class GameScene(viewport: GameViewport) extends Scene[GameData, Unit, Unit] {

  type SceneModel     = Unit
  type SceneViewModel = Unit

  val name: SceneName                 = SceneName("game")
  val modelLens: Lens[Unit, Unit]     = Lens.keepLatest
  val viewModelLens: Lens[Unit, Unit] = Lens.keepLatest
  val eventFilters: EventFilters      = EventFilters.Permissive

  val subSystems: Set[SubSystem] = Set(
    Belt(viewport.height - 325, -100),
    Belt(viewport.height - 525, -100),
    Belt(viewport.height - 725, -100)
  )

  def updateModel(context: FrameContext[GameData], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def updateViewModel(
      context: FrameContext[GameData],
      model: Unit,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  val background = Graphic(viewport.width, viewport.height, Assets.mats.background).withDepth(Depth(100))

  val partBoxes = for {
    i <- (0 until 5).toList
  } yield Assets.partBox.moveTo(570 + 125 * i, viewport.height - 120)

  val lime_65     = RGBA(0.8549, 1, 0.8078, 0.65)
  val topStrip    = Shape.Box(Rectangle(0, 0, viewport.width, 75), Fill.Color(lime_65))
  val bottomStrip = Shape.Box(Rectangle(0, viewport.height - 125, viewport.width, 125), Fill.Color(lime_65))

  def present(context: FrameContext[GameData], model: Unit, viewModel: Unit): Outcome[SceneUpdateFragment] = Outcome {
    SceneUpdateFragment.empty
      .addLayer(
        Layer(
          BindingKey("background"),
          List(background, topStrip, bottomStrip) ++ partBoxes
        )
      )
      .addLayer(Layer(BindingKey("belts")))
  }
}
