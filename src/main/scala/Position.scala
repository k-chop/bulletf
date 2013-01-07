package com.github.whelmaze.bulletf

object Position {
  
  final def center() = Position(centerX, centerY)  
  
}

case class Position (val x: Double, val y: Double) {

  type T2 = (Double, Double)
  
  def update(t: T2) = t match {
    case (nx, ny) => Position(nx, ny)
  }

  def add(t: T2) = t match {
    case (vx, vy) => Position(x + vx, y + vy)
  }
  
}
