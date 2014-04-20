package bulletf

import collection.mutable
import scala.util.Random

trait BackGround extends Runnable with Drawable

class BackGroundBasicStars extends BackGround with HasInnerFunc {

  private def initSpeed = 1 + Random.nextDouble * 10
  private def initSize = Random.nextBoolean() //1 + Random.nextFloat * 3

  private val star4 = Sprite.get('STAR4)
  private val star6 = Sprite.get('STAR6)

  private[BackGroundBasicStars] class Star(val pos: Position, var speed: Double, var size: Boolean)
    extends Runnable with Drawable
  {

    def update() {
      pos.y += speed
      if (pos.y > Game.height) {
        pos.y = 0
        speed = initSpeed
        size = initSize
      }
    }

    def draw() {
      if (size) {
        star4.draw(pos)
      } else {
        star6.draw(pos)
      }
    }

  }

  private val stars = mutable.ArrayBuffer.fill[Star](50){
    new Star(Position(Random.nextDouble * Game.width, 0), initSpeed, initSize)
  }

  def update() {
    stars foreach updateFunc
  }

  def draw() {
    stars.foreach(drawFunc)
  }

}
