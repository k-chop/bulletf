package bulletf



import org.newdawn.slick.Sound
import collection.mutable
import scala.util.Try

object SoundSystem {
  // この辺りはいずれ設定ファイルから読み込むので適当で

  private[this] var arr = Array.empty[Sound]
  private[this] var cooltimeDef = Array.empty[Int]
  private[this] var symmap = Map.empty[Symbol, Int]
  private[this] var cooltime = Array.empty[Int]

  def update(delta: Int) {
    var i = arr.length - 1
    while(0 <= i) {
      if (0 < cooltime(i)) cooltime(i) -= 1
      i -= 1
    }
  }

  def play(id: Int, pitch: Float = 1.0f, vol: Float = 0.5f) {
    require(id < arr.length)
    if (cooltime(id) < 1) {
      arr(id).play(pitch, vol)
      cooltime(id) = cooltimeDef(id)
    }
  }

  def play(id: Int) {
    play(id, 1.0f, 0.5f)
  }

  def playSymbol(s: Symbol, pitch: Float = 1.0f, vol: Float = 0.5f) {
    if (symmap.isDefinedAt(s)) play(symmap(s), pitch, vol)
  }

  def playSymbol(s: Symbol) {
    playSymbol(s, 1.0f, 0.5f)
  }

  def init() {
    val arrbuf = mutable.ArrayBuffer.empty[Sound]
    val colbuf = mutable.ArrayBuffer.empty[Int]
    val mapbuf = mutable.HashMap.empty[Symbol, Int]

    val transform = { s: String => s.replace(" ","") } andThen { s: String => s.split(":") }

    io.Source.fromFile("sound/mapping.txt")("UTF-8").getLines().map{
      transform
    }.zipWithIndex.foreach {
      case (Array(symId, filename, ctime), id) =>
        mapbuf += (Symbol(symId) -> id)
        arrbuf += new Sound(s"${Resource.soundpath}$filename")
        colbuf += Try{ ctime.toInt }.getOrElse(1)
        println(s"SoundSystem: Load Sound (id:$id, symId:$symId, file:$filename, cooltime:$ctime)")
      case s =>
        println(s"SoundSystem: invalid definition line\n -> $s")
    }
    arr = arrbuf.toArray
    cooltimeDef = colbuf.toArray
    symmap = mapbuf.toMap
    cooltime = Array.ofDim(arr.length)
    println(s"SoundSystem: ${arr.length} sounds loaded.")
  }

  def free() {
    arr.foreach(_.release())
    println("SoundSystem: Freeing sound resources complete.")
  }
}
