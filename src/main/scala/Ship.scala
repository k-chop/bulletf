package com.github.whelmaze.bulletf

class Ship extends Sprite(Resource.shipGraphic, Position(centerX, screenHeight - 50)) {

  radius = 2
  
  override def update(delta: Int) = ShipBehaivor.move(this, delta)
  
}
