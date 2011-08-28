trait Applet extends BaseApplet {
  setup {
    size(400, 300, processing.core.PConstants.P2D)
    frameRate(24)    
    title = "Mouse Replayer"
  }

  def titleSequence() {}
  def drawSequence() {}
  def loadSequence() {
    sequence = new LoadSequence(this)
  }
  def traceSequence(replayXML: scala.xml.Elem): Unit
}

object Server {
  //val host = "http://localhost:3000/"
  
  val host = "http://mouse-replayer.heroku.com/"
  
  def path(path: String) = host + path
}

class LoadSequence(val applet: Applet, var currentPage: Int = 0)
extends Sequence
{
  import processing.core.PVector  

  val replaysXML = {
    val url = new java.net.URL(Server.path("replays.xml"))
    scala.xml.XML.load(url) \ "replay"
  }
  val gg = new GraphicsGenerator(applet)
  val buttonManager = new ButtonManager(applet)  
  val replayButtons = scala.collection.mutable.ArrayBuffer[buttonManager.Button]()
  val height = 38
  val maxLine = 6
  val title = gg.createLabel("リプレイデータ一覧",
                             400, height, 22, 0xFFFFFF, 0)

  val prevButton = createMenuButton("< 戻る", 100, 100) {
    currentPage -= 1
    updateReplayButtons()
  }
  val nextButton = createMenuButton("次へ >", 200, 100) {
    currentPage += 1
    updateReplayButtons()
  }

  updateReplayButtons()

  def updateReplayButtons() {
    if (currentPage < 0) currentPage = 0
    val maxPage = (replaysXML.length - 1) / maxLine
    if (currentPage > maxPage) currentPage = maxPage
    
    prevButton.status = if (currentPage == 0) ButtonStatus.DISABLED
                        else ButtonStatus.UP   
    nextButton.status = if (currentPage >= maxPage) ButtonStatus.DISABLED
                        else ButtonStatus.UP

    while(replayButtons.length != 0) {
      val replay = replayButtons.remove(0)
      buttonManager.unregister(replay)      
    }

    val startPage = (maxLine * currentPage)
    (0 until maxLine).foreach {
      i =>
      val index = startPage + i
      if (index < replaysXML.length) {
        val replayXML = replaysXML(index)
        val text = (replayXML \ "name").text.dropRight(4)
        val labels = (
          gg.createLabel(text, 400, height, 18, 0, 0xFFFFFF),
          gg.createLabel(text, 400, height, 18, 0xFFFFFF, 0),
          gg.createLabel(text, 400, height, 18, 0xAAAAAA, 0)
        )
        val pos = new PVector(0, height + i * height)

        replayButtons += buttonManager.register(labels, pos) {
          val id = (replayXML \ "id").text.toInt
          val url = new java.net.URL(Server.path("replays/" + id + ".xml"))
          applet.traceSequence(scala.xml.XML.load(url))
        }
      }
    }    
  }   

  def createMenuButton(text: String, x: Int, w: Int)(action: => Unit) = {
    val size = 16
    val labels = (
      gg.createLabel(text, w, 35, size, 0xFFFFFF, 0),
      gg.createLabel(text, w, 35, size, 0xFFFFFF, 0x555555),
      gg.createLabel(text, w, 35, size, 0xFFFFFF, 0),
      gg.createLabel(text, w, 35, size, 0x888888, 0)
    )
    val pos = new PVector(x, height * (maxLine + 1))
    buttonManager.register(labels, pos)(action)
  }  
  
  def draw() {
    applet.background(255)
    applet.image(title, 0, 0)

    applet.fill(0)
    applet.stroke(0)
    applet.rect(0, height * (maxLine + 1), applet.width, 34)
    
    buttonManager.checkMouse()
    buttonManager.draw()
  }  
}

abstract class TraceSequence(val applet: Applet, replayXML: scala.xml.Elem)
         extends Sequence
{
  import processing.core.PVector

  val finishText: String
  
  val gg = new GraphicsGenerator(applet)

  var prevPos: PVector = null  
  val positions = scala.collection.mutable.ArrayBuffer[PVector]()

  var count = 0
  var _scene: () => Unit = initialize _
  
  def scene = _scene
  def scene_=(scene: () => Unit) {
    count = 0
    _scene = scene
  }
  
  def initialize() {
    applet.background(255)
    prevPos = null
    
    (replayXML \ "pos").foreach {
      xml =>
      val x = (xml \ "x").text.toInt
      val y = (xml \ "y").text.toInt
      positions += new PVector(x, y)
    }
    
    scene = start _
  }

  def start() = if (count >= 20) scene = trace _
  
  def trace() {
    if (positions.length != 0) {
      val pos = positions.remove(0)
      if (prevPos != null) {
        applet.line(prevPos.x, prevPos.y, pos.x, pos.y)
      }
      prevPos = pos
    } else scene = end _
  }
  
  def end() {
    if (count >= 20) {
      val label = gg.createLabel(finishText, 400, 100, 14, 0xFFFFFF)
      applet.fill(0)
      applet.stroke(0)
      applet.rect(0, 200, applet.width, 100)
      applet.image(label, 0, 180)
      scene = finish _
    }
  }

  def finish(): Unit
  
  def draw() {
    count += 1
    scene()
  }
}
