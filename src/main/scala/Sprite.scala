package com.github.whelmaze.bulletf

import java.io.{ FileInputStream, IOException }

object Sprite {
  def get(resource: Symbol): Sprite = new Sprite(resource)
}

class Sprite(_resource: Symbol) {
  
  val texture = TextureFactory.get(_resource)

  def draw(pos: Position) {
    Game.view2d()
    Drawer.draw(texture, pos)
  }

}





