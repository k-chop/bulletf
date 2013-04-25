package com.github.whelmaze.bulletf

import scala.collection.mutable

object Ship {

  def initialPosition = Position(constants.centerX, constants.screenHeight - 50)

}

class Ship extends HasCollision with HasInnerFunc with LifeAccess {

  // 自機のショット、そのうちキャッシュするようにする
  var ownObjects = mutable.ListBuffer.empty[Shot]

  val sprite: Sprite = Sprite.get(Resource.shipGraphic)
  // 初期位置
  var pos = Ship.initialPosition
  // 当たり判定の半径
  val radius = 4.0
  // インスタンス生成から経っている時間(フレーム)
  var time: Int = 0
  // 無敵時間
  var invincibleTime: Int = 0
  // 残機
  private[this] var remLives: Int = 3

  def life = remLives

  def damage(src: BulletLike) {
    val pt = src match {
      case s: Enemy =>
        s.damage(Shot.hurlShip) // 敵に体当たりダメージ
        1
      case s: Bullet =>
        s.disable() // 当たった弾は消滅
        1
      case _ => 0
    }
    if (0 < pt) {
      remLives -= pt
      invincibleTime = 120 // 無敵時間2秒
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('player_death), 'ship, pos.cloneBy(0,0), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(-6,-8), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(+12,-4), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(-2,+7), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(+4,+11), Angle.zero, 0))
    }
  }

  def update(delta: Int) {
    if (0 < invincibleTime) invincibleTime -= 1 // 無敵時間減らす
    ShipBehavior.move(this, delta)
    updateFunc.set(delta)
    ownObjects.foreach(updateFunc.func)
    ShipBehavior.shot(this, delta)
    time += 1
    if (time % 120 == 0) ownObjects = ownObjects.filter(enableFunc)
  }

  def draw() {
    // 無敵時間が0の時は普通に、無敵時間の場合は3フレームに1回だけ描画(点滅)
    if ( (invincibleTime <= 0) || (invincibleTime % 3 == 0) ) {
      sprite.draw(pos)
    }
    ownObjects.foreach(drawFunc)
  }

  // 外部で利用する際mutableだと困るので
  // でも現状普通にownObjectに外からアクセスできてしまうので意味が無い
  def shots: List[Shot] = ownObjects.toList

  // -------------------------------------------

  // Functionの生成を避ける為の
  protected val updateFunc = new InnerUpdateFunc

}


trait LifeAccess {
  def life: Int
}