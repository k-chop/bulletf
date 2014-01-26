package bulletf
package ui

class LifeBoard(val from: LifeAccess) extends Drawable {

  val sprite = Sprite.get('ship)

  private val basex: Int = 20
  private val basey: Int = 70
  private[this] val tp = Position(0,0)

  def draw() {
    def drawIn(rest: Int, x: Int, y: Int) {
      if (0 < rest) {
        tp.x = x; tp.y = y
        sprite.draw(tp)
        drawIn(rest-1, x+20, y)
      }
    }
    drawIn(from.life-1, basex, basey/2)
  }

}
