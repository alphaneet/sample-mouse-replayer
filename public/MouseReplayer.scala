object MouseReplayer extends Applet {
  val isApplet = false
  def firstSequence() = titleSequence()

  override def titleSequence() =
    sequence = new TitleSequence(this)
  override def drawSequence() =
    sequence = new DrawSequence(this)
  override def loadSequence() =
    sequence = new ApplicationLoadSequence(this)
  def traceSequence(replayXML: scala.xml.Elem) =
    sequence = new ApplicationTraceSequence(this, replayXML)
  
  def main(args: Array[String]) = runSketch()  
}

class TitleSequence(val applet: Applet) extends Sequence
{
  import processing.core.PVector

  val gg = new GraphicsGenerator(applet)
  val title = gg.createLabel("Mouse Replayer", 400, 100, 35, 0xFFFFFF, 0)
  
  val buttonManager = new ButtonManager(applet)
  val menuTexts = List("Start", "Load", "Exit")    
  val menuMethods = List(start _, load _, exit _)
  val height = 45

  (0 until menuTexts.length).foreach {
    i =>
    val text = menuTexts(i)    
    val labels = (
      gg.createLabel(text, 400, height, 25, 0, 0xFFFFFF),
      gg.createLabel(text, 400, height, 25, 0xFFFFFF, 0),
      gg.createLabel(text, 400, height, 25, 0xAAAAAA, 0),
      gg.createLabel(text, 400, height, 25, 0xAAAAAA, 0xFFFFFF)
    )
    val pos = new PVector(0, 140 + i * height)    
    val button = buttonManager.register(labels, pos) {
      menuMethods(i)()
    }
  }

  def start() = applet.drawSequence()
  def load() = applet.loadSequence()
  def exit() = applet.exit()
  
  def draw() {    
    applet.background(255)
    buttonManager.checkMouse()
    buttonManager.draw()
    applet.image(title, 0, 0)
  }
}

class DrawSequence(val applet: Applet) extends Sequence {
  import processing.core.PVector
  
  val gg = new GraphicsGenerator(applet)
  
  var prevPos: PVector = null  
  val positions = scala.collection.mutable.ArrayBuffer[PVector]()
  
  var count = 0
  var _scene = initialize _
  def scene = _scene
  def scene_=(scene: () => Unit) {
    count = 0
    _scene = scene
  }

  def notice(text: String) {
    applet.background(255)    
    val label = gg.createLabel(text, 400, 30, 13, 0xFF0000)
    applet.image(label, 0, 270)    
  }
  
  def clear() {
    notice("| 保存 > S | クリア > C | リプレイ一覧 > L | タイトル > Q |")    
    positions.clear()
    prevPos = null    
  }
  
  def initialize() {
    clear()
    scene = capture _
  }

  def capture() {
    val pos = new PVector(applet.mouseX, applet.mouseY)
    if (prevPos != null) {
      applet.line(prevPos.x, prevPos.y, pos.x, pos.y)
    }
    prevPos = pos
    positions += pos
    
    if (applet.isKeyPressed) {
      applet.key match {
        case 's' | 'S' => {
          saveXML()
          notice("保存しました")
          scene = saveWait _
        }
        case 'c' | 'C' => clear()
        case 'l' | 'L' => applet.loadSequence()
        case 'q' | 'Q' => applet.titleSequence()
        case _ =>
      }
    }
  }
  
  def saveWait() = if (count >= 20) scene = initialize _

  def saveXML() {
    val data = positions.foldLeft("<replay>\n") {
      (sum, pos) =>        
      sum + format("<pos><x>%d</x><y>%d</y></pos>\n",
                   pos.x.toInt, pos.y.toInt)
    }
    EasyClient.multiPost(Server.path("replays"),
                         "replay[upload_xml]",
                         data + "</replay>")
  }

  def draw() {
    count += 1    
    scene()
  }
}

class ApplicationLoadSequence(applet: Applet, currentPage: Int = 0)
extends LoadSequence(applet, currentPage)
{
  createMenuButton("スタート", 0, 100) {
    applet.drawSequence()
  }
  createMenuButton("タイトル", 300, 100) {
    applet.titleSequence()
  }
}

class ApplicationTraceSequence(applet: Applet, replayXML: scala.xml.Elem)
extends TraceSequence(applet, replayXML)
{
  val finishText =
    """もう一度再生する場合は R キーを押してください。
      |リプレイ一覧に戻る場合は L キーを押してください。
      |タイトル画面に戻る場合は Q キーを押してください。""".stripMargin
  
  def finish() {
    if (applet.isKeyPressed) {
      applet.key match {
        case 'r' | 'R' => scene = initialize _
        case 'l' | 'L' => applet.loadSequence()
        case 'q' | 'Q' => applet.titleSequence()
        case _ => 
      }
    }
  }    
}
