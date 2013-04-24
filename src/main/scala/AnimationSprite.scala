package com.github.whelmaze.bulletf

class AnimationSprite(resourceId: Symbol) extends Sprite {

  val (texture, animInfo) = TextureFactory.getAnimate(resourceId)

  val rect: Rect = animInfo.rect
  private[this] var time: Int = 0

  def draw(custom_rect: Rect, pos: Position, angle: Double) {}

  def draw(pos: Position, angle: Double, scale: Double, alpha: Double) {
    Drawer.draw(texture, animInfo.next(time), pos, angle, scale, alpha)
    time += 1
  }
}
