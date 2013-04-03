package com.github.whelmaze.bulletf

object Angle {
  lazy val zero = Angle(0)
}

case class Angle(dir: Double) extends AnyVal {
  
  def update(newDir: Double) = Angle(newDir)
  def normalized = Angle( if(dir > 360) dir + 360 % 360 else dir )
  def +(left: Angle) = Angle(dir + left.dir)
}
