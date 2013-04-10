package com.github.whelmaze.bulletf

class Ship extends HasCollision {

  val sprite: Sprite = Sprite.get(Resource.shipGraphic)

  var pos = Position(constants.centerX, constants.screenHeight - 50)

  val radius = 2.0
  
  def update(delta: Int) {
    ShipBehavior.move(this, delta)
  }

  def draw() {
    sprite.draw(pos)
  }
  
}
