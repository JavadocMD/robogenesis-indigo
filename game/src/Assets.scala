package game

import indigo._

object Assets:

  trait AssetDef:
    def assetType(baseUrl: String): AssetType

  // Texture
  class T(name: String) extends AssetDef:
    lazy val assetName = AssetName(name)
    lazy val material  = Material.Bitmap(assetName)

    def path(baseUrl: String)      = AssetPath(baseUrl + s"assets/$name.png")
    def assetType(baseUrl: String) = AssetType.Image(assetName, path(baseUrl))

  // Simple graphic
  class G(name: String, width: Int, height: Int) extends AssetDef:
    lazy val assetName = AssetName(name)
    lazy val material  = Material.Bitmap(assetName)
    lazy val graphic   = Graphic(width, height, material)

    def path(baseUrl: String)      = AssetPath(baseUrl + s"assets/$name.png")
    def assetType(baseUrl: String) = AssetType.Image(assetName, path(baseUrl))

  // Audio
  class A(name: String) extends AssetDef:
    lazy val assetName = AssetName(name)
    lazy val play      = PlaySound(assetName, Volume.Max)

    def path(baseUrl: String)      = AssetPath(baseUrl + s"assets/$name.wav")
    def assetType(baseUrl: String) = AssetType.Audio(assetName, path(baseUrl))

  // All assets
  val background = T("background")
  val partbox    = G("partbox", 110, 110)
  val belt       = G("belt", 400, 200)
  val gears      = G("gears", 400, 200)
  val junk1      = G("junk1", 100, 100)
  val junk2      = G("junk2", 100, 100) // see-through version of junk pile
  val treds      = G("treds", 100, 100)
  val body       = G("body", 100, 100)
  val head       = G("head", 100, 100)
  val tutorial   = G("tutorial", 915, 800)
  val endscene   = G("endscene", 500, 500)
  val radio      = G("radio", 500, 500)
  val robo       = G("robo", 500, 500)
  val scanner    = A("scanner")
  val capture    = A("capture")
  val static     = A("static")
  val thrum      = A("thrum")
  val machine    = A("machine")
  val roboBleep  = A("robo")

  def load(baseUrl: String): Set[AssetType] = Set(
    background,
    partbox,
    belt,
    gears,
    junk1,
    junk2,
    treds,
    body,
    head,
    endscene,
    tutorial,
    radio,
    robo,
    scanner,
    capture,
    static,
    thrum,
    machine,
    roboBleep
  ).map(_.assetType(baseUrl))

// val bpdDiamond = new BitmapFont(Gdx.files.internal("data/bpDotsDiamond.fnt"), atlas.findRegion("bpDotsDiamond"))
// val bpdMinus = new BitmapFont(Gdx.files.internal("data/bpDotsMinus.fnt"), atlas.findRegion("bpDotsMinus"))
// val bpdVertical = new BitmapFont(Gdx.files.internal("data/bpDotsVertical.fnt"), atlas.findRegion("bpDotsVertical"))

// val speechBubbleStyle = new LabelStyle(bpdMinus, new Color(1f, 1f, 1f, 1f))
// val partsNeededStyle = new LabelStyle(bpdVertical, new Color(1f, 1f, 1f, 1f))

end Assets
