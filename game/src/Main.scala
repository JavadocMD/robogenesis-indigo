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

  // Screen size. (Hard-coded for this game.)
  val vw = 1200
  val vh = 800

  def initialScene(bootData: BootData): Option[SceneName] = None

  def scenes(bootData: BootData): NonEmptyList[Scene[GameData, Unit, Unit]] = NonEmptyList {
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

object GameScene extends Scene[GameData, Unit, Unit] {
  import Main.{vw, vh}

  type SceneModel     = Unit
  type SceneViewModel = Unit

  val name: SceneName                 = SceneName("game")
  val modelLens: Lens[Unit, Unit]     = Lens.keepLatest
  val viewModelLens: Lens[Unit, Unit] = Lens.keepLatest
  val eventFilters: EventFilters      = EventFilters.Permissive

  val subSystems: Set[SubSystem] = Set(
    Belt(75),
    Belt(275),
    Belt(475)
  )

  def updateModel(context: FrameContext[GameData], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def updateViewModel(
      context: FrameContext[GameData],
      model: Unit,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  val background = Graphic(vw, vh, Assets.background.material.tile)

  val partBoxes = for {
    i <- (0 until 5).toList
  } yield Assets.partbox.graphic.moveTo(570 + 125 * i, vh - 120)

  val lime_35     = RGBA.fromHexString("daffce").withAlpha(0.35)
  val topStrip    = Shape.Box(Rectangle(0, 0, vw, 75), Fill.Color(lime_35))
  val bottomStrip = Shape.Box(Rectangle(0, vh - 125, vw, 125), Fill.Color(lime_35))

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
