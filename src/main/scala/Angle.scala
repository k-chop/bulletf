package com.github.whelmaze.bulletf

case class Angle(dir: Double) extends AnyVal {
  
  def update(newDir: Double) = Angle(newDir)
  def normalized = Angle( if(dir > 360) dir + 360 % 360 else dir )

}
