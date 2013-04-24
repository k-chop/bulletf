package com.github.whelmaze.bulletf

object Position {

  def apply(x: Double, y: Double) = new Position(x, y)
  final def center = Position(constants.centerX, constants.centerY)
  final def outside = Position(-200, -200)
  
}

class Position (var x: Double, var y: Double) {

  def cloneBy(dx: Double, dy: Double): Position = new Position(x + dx, y + dy)

  override def toString = s"pos($x, $y)"
}
