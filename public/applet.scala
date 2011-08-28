class AppletMain extends Applet {
  val isApplet = true
  def firstSequence() = loadSequence()
  def traceSequence(replayXML: scala.xml.Elem) =
    sequence = new AppletTraceSequence(this, replayXML)
}

class AppletTraceSequence(applet: Applet, replayXML: scala.xml.Elem)
extends TraceSequence(applet, replayXML)
{
  val finishText =
    """もう一度再生する場合は R キーを押してください。
      |リプレイ一覧に戻る場合は L キーを押してください。""".stripMargin

  def finish() {
    if (applet.isKeyPressed) {
      applet.key match {
        case 'r' | 'R' => scene = initialize _
        case 'l' | 'L' => applet.loadSequence()
        case _ => 
      }
    }
  }    
}
