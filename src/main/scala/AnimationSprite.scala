package com.github.whelmaze.bulletf

object AnimationSprite {
  def get(resource: Symbol): Sprite = new Sprite(resource)
}

class AnimationSprite(_resource: Symbol) {

  val texture = TextureFactory.get(_resource)

  def draw(pos: Position) {
    Game.view2d()
    Drawer.draw(texture, pos)
  }

}
