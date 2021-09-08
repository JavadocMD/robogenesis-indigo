package game

import indigo._
import indigo.shared.materials.Material.ImageEffects

object Assets {

  object names {
    val background = AssetName("background")
    val partbox    = AssetName("partbox")
    val belt       = AssetName("belt")
    val gears      = AssetName("gears")
  }

  def load(baseUrl: String): Set[AssetType] = Set(
    AssetType.Image(names.background, AssetPath(baseUrl + "assets/background.png")),
    AssetType.Image(names.partbox, AssetPath(baseUrl + "assets/partbox.png")),
    AssetType.Image(names.belt, AssetPath(baseUrl + "assets/belt.png")),
    AssetType.Image(names.gears, AssetPath(baseUrl + "assets/gears.png"))
  )

  object mats {
    val background = Material.Bitmap(names.background).tile
    val partbox    = Material.Bitmap(names.partbox)
    val belt       = Material.Bitmap(names.belt)
    val gears      = Material.Bitmap(names.gears)
  }

  val partBox = Graphic(110, 110, mats.partbox)
  val belt    = Graphic(400, 200, mats.belt)
  val gears   = Graphic(400, 200, mats.gears)

  // val endscene = atlas.findRegion("endscene")
  // val tutorial = atlas.findRegion("tutorial")
  // val radio = atlas.findRegion("radio")
  // val robo = atlas.findRegion("robo")
  // val junk1 = atlas.findRegion("junk-pile-1")

  // val treds = atlas.findRegion("treds")
  // val body = atlas.findRegion("body")
  // val head = atlas.findRegion("head")

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
