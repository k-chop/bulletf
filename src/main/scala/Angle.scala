package com.github.whelmaze.bulletf

case class Angle(val dir: Double) {
  
  def update(newDir: Double) = Angle(newDir)
  def normalize() = Angle( if(dir > 360) dir + 360 % 360 else dir )

}
