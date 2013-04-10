package com.github.whelmaze.bulletf

object Position {

  def apply(x: Double, y: Double) = new Position(x, y)
  final def center() = Position(constants.centerX, constants.centerY)  
  
}

class Position (var x: Double, var y: Double)
