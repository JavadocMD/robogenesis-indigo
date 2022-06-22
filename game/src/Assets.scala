package game

import indigo._
import indigo.json.Json

object Assets:

  trait AssetDef:
    def assets(baseUrl: String): List[AssetType]

  // Texture
  class T(name: String) extends AssetDef:
    lazy val assetName          = AssetName(name)
    lazy val material           = Material.Bitmap(assetName)
    def path(baseUrl: String)   = AssetPath(baseUrl + s"assets/$name.png")
    def assets(baseUrl: String) = AssetType.Image(assetName, path(baseUrl)) :: Nil

  // Simple graphic
  class G(name: String, size: Size) extends T(name):
    lazy val graphic = Graphic(size.width, size.height, material)

  // Audio
  class A(name: String) extends AssetDef:
    lazy val assetName          = AssetName(name)
    lazy val play               = PlaySound(assetName, Volume.Max)
    def path(baseUrl: String)   = AssetPath(baseUrl + s"assets/$name.wav")
    def assets(baseUrl: String) = AssetType.Audio(assetName, path(baseUrl)) :: Nil

  // JSON
  class J(name: String) extends AssetDef:
    lazy val assetName          = AssetName(name + ":json")
    def path(baseUrl: String)   = AssetPath(baseUrl + s"assets/$name.json")
    def assets(baseUrl: String) = AssetType.Text(assetName, path(baseUrl)) :: Nil

  // Font
  class F(name: String, size: Size, unknownChar: String) extends AssetDef:
    val img                     = T(name)
    val json                    = J(name)
    lazy val fontKey            = FontKey(name)
    def assets(baseUrl: String) = img.assets(baseUrl) ++ json.assets(baseUrl)
    def fontInfo(assetCollection: AssetCollection): Option[FontInfo] =
      for
        json  <- assetCollection.findTextDataByName(json.assetName)
        chars <- Json.readFontToolJson(json)
        unk   <- chars.find(_.character == unknownChar)
      yield FontInfo(
        fontKey = fontKey,
        fontSheetBounds = size,
        unknownChar = unk,
        fontChars = chars,
        caseSensitive = true
      )

  // All assets
  val background      = T("background")
  val partbox         = G("partbox", Size(110, 110))
  val belt            = G("belt", Size(400, 200))
  val gears           = G("gears", Size(400, 200))
  val junk1           = G("junk1", Size(100, 100))
  val junk2           = G("junk2", Size(100, 100)) // see-through version of junk pile
  val treds           = G("treds", Size(100, 100))
  val body            = G("body", Size(100, 100))
  val head            = G("head", Size(100, 100))
  val tutorial        = G("tutorial", Size(915, 800))
  val endscene        = G("endscene", Size(500, 500))
  val radio           = G("radio", Size(500, 500))
  val robo            = G("robo", Size(500, 500))
  val scanner         = A("scanner")
  val capture         = A("capture")
  val static          = A("static")
  val thrum           = A("thrum")
  val machine         = A("machine")
  val roboBleep       = A("robo")
  val fontBpDotsMinus = F("BPdotsMinus-Bold", Size(399, 387), "?")

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
    roboBleep,
    fontBpDotsMinus
  ).flatMap(_.assets(baseUrl))

end Assets
