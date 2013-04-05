package com.github.whelmaze.bulletf

class AnimationSprite(resourceId: Symbol) extends Sprite {

  val (texture, animInfo) = TextureFactory.getAnimate(resourceId)

  val rect: Rect = animInfo.rect
  private[this] var time: Int = 0

  def draw(pos: Position) = draw(pos, 0)

  def draw(pos: Position, angle: Double) {
    Drawer.draw(texture, animInfo.next(time), pos, angle)
    time += 1
  }
}
