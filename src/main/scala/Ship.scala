package com.github.whelmaze.bulletf

import scala.collection.mutable

class Ship extends HasCollision with HasInnerFunc {

  var ownObjects = mutable.ListBuffer.empty[Shot]
  protected val updateFunc = new InnerUpdateFunc

  val sprite: Sprite = Sprite.get(Resource.shipGraphic)

  var pos = Position(constants.centerX, constants.screenHeight - 50)

  val radius = 2.0

  var time: Int = 0

  def update(delta: Int) {
    ShipBehavior.move(this, delta)
    updateFunc.set(delta)
    ownObjects.foreach(updateFunc.func)
    ShipBehavior.shot(this, delta)
    time += 1
    if (time % 120 == 0) ownObjects = ownObjects.filter(enableFunc)
  }

  def draw() {
    sprite.draw(pos)
    ownObjects.foreach(drawFunc)
  }

  def shots: List[Shot] = ownObjects.toList

}
