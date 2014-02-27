package bulletf



import scala.collection.mutable
import org.newdawn.slick.opengl.Texture

object SpriteBatch {
  final val self = new mdesl.graphics.SpriteBatch()
}

object Sprite {

  private[this] val cache = mutable.HashMap.empty[Symbol, Sprite]

  def get(resource: Symbol): Sprite = cache.get(resource) match {
    case Some(cachedSprite) => cachedSprite
    case None =>
      val newSprite: Sprite = if (resource.name.toLowerCase.substring(0, 2) == "a_") {
        new AnimationSprite(resource)
      } else {
        new SpriteImpl(resource)
      }
      cache.update(resource, newSprite)
      newSprite
  }

  def count = cache.size

}

trait Sprite {
  val rect: Rect

  def draw(pos: Position) {
    draw(pos, 0)
  }
  def draw(pos: Position, angle: Double) {
    draw(pos, angle, 1.0)
  }
  def draw(pos: Position, angle: Double, scale: Double) {
    draw(pos, angle, scale, 1.0)
  }
  def draw(pos: Position, angle: Double, scale: Double, alpha: Double) {
    draw(pos, angle, scale, alpha, 0)
  }

  def draw(pos: Position, angle: Double, scale: Double, alpha: Double, time: Int)

  def draw(custom_rect: Rect, pos: Position, angle: Double)
}

class SpriteImpl(resourceId: Symbol) extends Sprite {

  val (texture, rect) = TextureFactory.get(resourceId)

  // 普通のSpriteはtime無視
  def draw(pos: Position, angle: Double, scale: Double, alpha: Double, time: Int) {
    val u = rect.x / texture.getWidth.toFloat
    val v = rect.y / texture.getHeight.toFloat
    val u2 = (rect.x + rect.w) / texture.getWidth.toFloat
    val v2 = (rect.y + rect.h) / texture.getHeight.toFloat
    SpriteBatch.self.draw(texture, pos.x.toFloat, pos.y.toFloat, rect.w.toFloat, rect.h.toFloat, pos.x.toFloat, pos.y.toFloat, angle.toRadians.toFloat, u, v, u2, v2)
    //SpriteBatch.self.drawRegion(texture, rect.x, rect.y, rect.w, rect.h, pos.x.toFloat, pos.y.toFloat, rect.w.toFloat, rect.h.toFloat)
    //Drawer.draw(texture, rect, pos, angle, scale, alpha)
  }

  def draw(custom_rect: Rect, pos: Position, angle: Double) {
    //Drawer.draw(texture, custom_rect, pos, angle)
  }

}





