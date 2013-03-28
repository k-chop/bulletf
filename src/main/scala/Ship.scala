package com.github.whelmaze.bulletf

class Ship extends Sprite(Resource.shipGraphic) with HasCollision {

  var pos = Position(constants.centerX, constants.screenHeight - 50)

  val sprite = new Sprite(Resource.shipGraphic)
  val radius = 2.0
  
  def update(delta: Int) {
    ShipBehaivor.move(this, delta)
  }

  def draw() {
    sprite.draw(pos)
  }
  
}
