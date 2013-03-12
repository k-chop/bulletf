package com.github.whelmaze.bulletf

import java.io.{ FileInputStream, IOException }

class Sprite(_resource: Symbol, _pos: Position, _speed: Double, _angle: Double) extends HasCollision {
  
  protected val texture = TextureFactory.get(_resource)
  var pos = _pos
  var speed = _speed
  var angle = Angle(_angle)
  protected var flip = 0
  var radius: Double = 0
  
  def this(resource: Symbol) = this(resource, Position(0, 0), 0, 0)

  def this(resource: Symbol, pos: Position) = this(resource, pos, 0, 0)
  
  def update(delta: Int) = {}
  
  def draw() = Drawer.draw(texture, pos)

  def inside() = (0-radius <= pos.x  && pos.x <= constants.screenWidth+radius && 0-radius <= pos.y && pos.y <= constants.screenHeight+radius)
  
}





