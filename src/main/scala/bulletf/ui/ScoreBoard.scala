package bulletf
package ui

object ScoreBoard {
  def init(i: Int) = new ScoreBoardImpl(i)
}

trait ClearableScoreBoard extends ScoreBoard {
  def clear()
}

trait ScoreBoard extends Runnable with Drawable {
  def get: Int
  def add(i: Int)
}

object NullScoreBoard extends ScoreBoard {
  def get: Int = 0
  def add(i: Int) {}
  def update() {}
  def draw() {}
}

class ScoreBoardImpl(private[this] var score: Int) extends ClearableScoreBoard {
  val basex = 10
  val basey = 28
  val basew = 160

  val sprite = Sprite.get('numbers)
  lazy val rects = {
    val w = 16; val h = 18
    val a = Array.tabulate(10)(i => sprite.rect.cloneBy(i*16, 0, w, h))
    a.zipWithIndex.foreach{case (q,i) => println(s"i -> $q")}
    a
  }

  def get: Int = score

  def add(i: Int) {
    score += i
  }

  def clear() {
    score = 0
  }

  def update() {

  }

  private[this] val tp = Position(0,0)

  def draw() {
    def drawIn(i: Int, x: Int, y: Int, digit: Int) {
      if (i == 0 && digit != 0) {
        return
      } else {
        val k = i % 10
        tp.x = x; tp.y = y
        sprite.draw(rects(k), tp, 0)
        drawIn(i / 10, x - 16, y, digit+1)
      }
    }
    drawIn(score, basew, basey/2, 0)
  }

}
