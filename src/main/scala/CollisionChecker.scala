package com.github.whelmaze.bulletf

import collection.mutable

class CollisionChecker(val owner: HasCollision) {

  // TODO : 敵の弾なら自機だけ、自機の弾なら敵だけ、を対象にするには……？
  // 別々に作るのがてっとりばやいのかなあ。
  
  def check(pool: List[HasCollision]): State = pool find {
    case b: Bullet if collision_?(b) => true
    case _ => false
  } match {
    case Some(x: Bullet) => Shooted(x)
    case None => Live
    case _ => Lost
  }

  final def collision_?(target: Bullet): Boolean = {
    sq(owner.pos.x - target.pos.x) + sq(owner.pos.y - target.pos.y) < sq(target.radius + owner.radius)
  }

  final def sq(d: Double) = d * d
}
