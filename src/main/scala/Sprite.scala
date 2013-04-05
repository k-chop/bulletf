package com.github.whelmaze.bulletf

import scala.collection.mutable
import org.newdawn.slick.opengl.Texture

object Sprite {

  private[this] val cache = mutable.HashMap.empty[Symbol, Sprite]

  def get(resource: Symbol): Sprite = cache.get(resource) match {
    case Some(cachedSprite) => cachedSprite
    case None =>
      val newSprite: Sprite = (if (resource.name.substring(0, 2) == "a_") {
        new AnimationSprite(resource)
      } else {
        new SpriteImpl(resource)
      })
      cache.update(resource, newSprite)
      newSprite
  }

  def count = cache.size

}

trait Sprite {
  val rect: Rect

  def draw(pos: Position)
  def draw(pos: Position, angle: Double)
}

class SpriteImpl(resourceId: Symbol) extends Sprite {

  val (texture, rect) = TextureFactory.get(resourceId)

  def draw(pos: Position) {
    Drawer.draw(texture, rect, pos, 0)
  }

  def draw(pos: Position, angle: Double) {
    Drawer.draw(texture, rect, pos, angle)
  }

}





