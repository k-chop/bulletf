package com.github.whelmaze.bulletf

import java.io.{ FileInputStream, IOException }

object Sprite {
  def get(resource: Symbol): Sprite = new Sprite(resource)
}

class Sprite(_resource: Symbol) {

  val (texture, rect) = TextureFactory.get(_resource)

  def draw(pos: Position) {
    Drawer.draw(texture, rect, pos, 0)
  }

  def draw(pos: Position, angle: Double) {
    Drawer.draw(texture, rect, pos, angle)
  }

}





