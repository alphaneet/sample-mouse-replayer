trait BaseApplet extends processing.core.PApplet {
  val isApplet: Boolean
  
  def firstSequence(): Unit
  protected var sequence: Sequence = _
  
  def title = if (!isApplet) "" else frame.getTitle
  def title_=(title: String) = if (!isApplet) frame.setTitle(title)  
  
  private var _setup = () => {}
  def setup(setup: => Unit) = _setup = setup _  
  override def setup() {
    firstSequence()
    _setup()
  }
  
  override def draw() = sequence.draw()

  var isKeyPressed = false
  override def keyPressed() {
    isKeyPressed = true
    sequence.keyPressed()
  }
  override def keyReleased() {
    isKeyPressed = false
    sequence.keyReleased()
  }
  override def keyTyped() = sequence.keyTyped()
  
  var isMousePressed = false
  override def mousePressed() {
    isMousePressed = true
    sequence.mousePressed()
  }
  override def mouseReleased() {
    isMousePressed = false
    sequence.mouseReleased()
  }
}

trait Sequence {
  this: { val applet: BaseApplet } =>

  def draw()
  def keyPressed() {}
  def keyReleased() {}
  def keyTyped() {}
  def mousePressed() {}
  def mouseReleased() {}
}

trait DrawManager {
  import scala.collection.mutable.ArrayBuffer
  private val beforeDrawChain = ArrayBuffer[() => Unit]()
  private val afterDrawChain = ArrayBuffer[() => Unit]()
  
  final def draw() {
    beforeDrawChain.foreach(_())
    drawMain()
    afterDrawChain.foreach(_())
  }
  def drawMain()
  def beforeDraw(draw: => Unit) = beforeDrawChain += draw _
  def afterDraw(draw: => Unit) = afterDrawChain += draw _
}

trait DrawSupport {
  import processing.core.PGraphics
  def drawPGraphics(g: PGraphics)(draw: PGraphics => Unit) {
    g.beginDraw()
    draw(g)
    g.endDraw()                
  }  
}

object EasyClient {
  import com.sun.jersey.api.client.Client
  import com.sun.jersey.multipart.FormDataMultiPart
  import javax.ws.rs.core.MediaType
  
  def getXML(url: String) = {
    val ret = Client.create.resource(url).get(classOf[String])
    scala.xml.XML.loadString(ret)
  }

  def multiPost(url: String, fieldName: String, data: String) = {
    val form = new FormDataMultiPart().field(fieldName, data)
    Client.create.resource(url).
    `type`(MediaType.MULTIPART_FORM_DATA).post(classOf[String], form)
  }
}

object ButtonStatus extends Enumeration {
  type ButtonStatus = Value
  val UP, OVER, DOWN, DISABLED = Value
}

object ButtonManager {
  private var isLock = false
}

class ButtonManager(applet: BaseApplet) {  
  import processing.core.{PImage, PVector, PConstants}
  
  class Button(
    _images: Tuple4[PImage, PImage, PImage, PImage],
    val pos: PVector,
    val action: () => Unit
  ) {
    val images = List(_images._1, _images._2, _images._3, _images._4)
    var status = ButtonStatus.UP
    def checkMouse: Boolean = {
      if (status == ButtonStatus.DISABLED) return false
      val isOver = isOverMouse(images(status.id), this)
      val result = isOver && mouseClicked(this)
      if (mousePressed) {
        if (isOver && !ButtonManager.isLock) {
          status = ButtonStatus.DOWN
          ButtonManager.isLock = true
        }
      } else {
        status = if (isOver) ButtonStatus.OVER else ButtonStatus.UP
        ButtonManager.isLock = false
      }
      result
    }
    def image() = applet.image(images(status.id), pos.x, pos.y)
  }  
  private val buttons = scala.collection.mutable.ArrayBuffer[Button]()

  def isOverMouse(image: PImage, button: Button): Boolean =
    applet.mouseX > button.pos.x &&
    applet.mouseX < button.pos.x + image.width &&
    applet.mouseY > button.pos.y &&
    applet.mouseY < button.pos.y + image.height

  /**
   * override 例
   * applet.mousePressed - マウスのどのボタンでも true
   * applet.mousePressed && applet.mouseButton == PConstants.LEFT
   *   - 左クリックの時 true
   */
  def mousePressed: Boolean =
    applet.isMousePressed && applet.mouseButton == PConstants.LEFT
  
  /**
   * override 例
   * mousePressed - ボタンを押してる間ずっと true
   * mousePressed && button.status == Button.OVER
   *   - 押した時に true
   * !mousePressed && button.status == Button.DOWN
   *   - 押して離した時に true
   */
  def mouseClicked(button: Button): Boolean =
    !mousePressed && button.status == ButtonStatus.DOWN

  def register(image: PImage, pos: PVector)(action: => Unit): Button = 
    register((image, image, image), pos)(action)
  
  def register(images: Triple[PImage, PImage, PImage],
               pos: PVector)(action: => Unit): Button = 
    register((images._1, images._2, images._3,
              applet.createImage(0, 0, PConstants.ARGB)), pos)(action)
  
  def register(images: Tuple4[PImage, PImage, PImage, PImage],
               pos: PVector)(action: => Unit): Button = {
    val button = new Button(images, pos, action _)
    buttons += button
    button
  }
  def unregister(button: Button): Button = {
    buttons -= button
    button
  }
  def checkMouse() =
    buttons withFilter(_.checkMouse) foreach(_.action())
  def draw() = buttons.foreach(_.image())
}

class GraphicsGenerator(applet: processing.core.PApplet) {    
  import processing.core.{PGraphics, PImage}
  import processing.core.PConstants._
  
  def rgb(c: Int)(implicit g: PGraphics): (Float, Float, Float) =
    (g.red(c), g.green(c), g.blue(c))

  def hsb(c: Int)(implicit g: PGraphics): (Float, Float, Float) =
    (g.hue(c), g.saturation(c), g.brightness(c))

  def createLabel(text: String, w: Int, h: Int, size: Int,
                  front: Int, back: Int = -1): PImage = {
    implicit val g: PGraphics = applet.createGraphics(w, h, JAVA2D)
    g.beginDraw
    g.smooth

    if (back >= 0) {
      val b = rgb(back)
      g.background(b._1, b._2, b._3)
    }

    val f = rgb(front)
    g.fill(f._1, f._2, f._3)
    //g.textSize(size)
    g.textFont(applet.createFont("", size))
    g.textAlign(CENTER)
    val des = g.textDescent.toInt
    g.text(text, w>>1, (h>>1) + des + (des>>1))
    g.endDraw

    val img: PImage = applet.createImage(w, h, ARGB)
    img.set(0, 0, g)
    g.dispose

    img
  }
}
