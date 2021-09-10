package game

import indigo._
import indigo.shared.materials.Material.ImageEffects

object Assets {

  // Texture
  class T(name: String) {
    lazy val assetName = AssetName(name)
    lazy val material  = Material.Bitmap(assetName)

    def path(baseUrl: String)      = AssetPath(baseUrl + s"assets/$name.png")
    def assetType(baseUrl: String) = AssetType.Image(assetName, path(baseUrl))
  }

  // Simple graphic
  class G(name: String, width: Int, height: Int) {
    lazy val assetName = AssetName(name)
    lazy val material  = Material.Bitmap(assetName)
    lazy val graphic   = Graphic(width, height, material)

    def path(baseUrl: String)      = AssetPath(baseUrl + s"assets/$name.png")
    def assetType(baseUrl: String) = AssetType.Image(assetName, path(baseUrl))
  }

  val background = T("background")
  val partbox    = G("partbox", 110, 110)
  val belt       = G("belt", 400, 200)
  val gears      = G("gears", 400, 200)
  val junk1      = G("junk1", 100, 100)
  val treds      = G("treds", 100, 100)
  val body       = G("body", 100, 100)
  val head       = G("head", 100, 100)
  val tutorial   = G("tutorial", 915, 800)
  val endscene   = G("endscene", 500, 500)
  val radio      = G("radio", 500, 500)
  val robo       = G("robo", 500, 500)

  def load(baseUrl: String): Set[AssetType] = Set(
    background.assetType(baseUrl),
    partbox.assetType(baseUrl),
    belt.assetType(baseUrl),
    gears.assetType(baseUrl),
    junk1.assetType(baseUrl),
    treds.assetType(baseUrl),
    body.assetType(baseUrl),
    head.assetType(baseUrl),
    endscene.assetType(baseUrl),
    tutorial.assetType(baseUrl),
    radio.assetType(baseUrl),
    robo.assetType(baseUrl)
  )

  // val scanner = Gdx.audio.newSound(Gdx.files.internal("data/scanner.wav"))
  // val capture = Gdx.audio.newSound(Gdx.files.internal("data/capture.wav"))
  // val static = Gdx.audio.newSound(Gdx.files.internal("data/static.wav"))
  // val thrum = Gdx.audio.newSound(Gdx.files.internal("data/thrum.wav"))
  // val machine = Gdx.audio.newSound(Gdx.files.internal("data/machine.wav"))
  // val roboBleep = Gdx.audio.newSound(Gdx.files.internal("data/robo.wav"))

  // val bpdDiamond = new BitmapFont(Gdx.files.internal("data/bpDotsDiamond.fnt"), atlas.findRegion("bpDotsDiamond"))
  // val bpdMinus = new BitmapFont(Gdx.files.internal("data/bpDotsMinus.fnt"), atlas.findRegion("bpDotsMinus"))
  // val bpdVertical = new BitmapFont(Gdx.files.internal("data/bpDotsVertical.fnt"), atlas.findRegion("bpDotsVertical"))

  // val speechBubbleStyle = new LabelStyle(bpdMinus, new Color(1f, 1f, 1f, 1f))
  // val partsNeededStyle = new LabelStyle(bpdVertical, new Color(1f, 1f, 1f, 1f))
}
