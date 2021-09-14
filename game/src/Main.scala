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
object Main extends IndigoGame[BootData, GameData, Model, Unit]:

  // Screen size. (Hard-coded for this game.)
  val vw = 1200
  val vh = 800

  def initialScene(bootData: BootData): Option[SceneName] = None

  def scenes(bootData: BootData): NonEmptyList[Scene[GameData, Model, Unit]] = NonEmptyList {
    GameScene
  }

  val eventFilters: EventFilters = EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData]] = Outcome {
    val assetPath = flags.getOrElse("assetPath", "")
    val viewport  = GameViewport(vw, vh)
    val data      = BootData(assetPath, viewport)
    val config = GameConfig(
      clearColor = RGBA.Black,
      frameRate = 60,
      magnification = 1,
      viewport = data.viewport
    )

    BootResult(config, data)
      .withAssets(Assets.load(assetPath))
  }

  def initialModel(startupData: GameData): Outcome[Model] = Outcome(Model.initial)

  def initialViewModel(startupData: GameData, model: Model): Outcome[Unit] = Outcome(())

  def setup(bootData: BootData, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[GameData]] = Outcome {
    val data = GameData(bootData.viewport)
    Startup.Success(data)
  }

  def updateModel(context: FrameContext[GameData], model: Model): GlobalEvent => Outcome[Model] =
    _ => Outcome(model)

  def updateViewModel(context: FrameContext[GameData], model: Model, viewModel: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  def present(context: FrameContext[GameData], model: Model, viewModel: Unit): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)

end Main
