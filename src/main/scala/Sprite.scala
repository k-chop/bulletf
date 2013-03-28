package com.github.whelmaze.bulletf

import java.io.{ FileInputStream, IOException }

class Sprite(_resource: Symbol) {
  
  val texture = TextureFactory.get(_resource)

  def draw(pos: Position) {
    Game.view2d()
    Drawer.draw(texture, pos)
  }

}





