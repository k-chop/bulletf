package com.github.whelmaze.bulletf

import collection.mutable

abstract class CollisionChecker[A, B](val target: A) {

  def check(pool: List[B]): State

}

class CollisionCheckerShip(target: Ship) extends CollisionChecker[Ship, BulletLike](target) {
  import MathUtil.sq

  def isHit(s: HasCollision) = sq(target.pos.x - s.pos.x) + sq(target.pos.y - s.pos.y) < sq(s.radius + target.radius)

  // 1つ見つけた時点で探索打ち切り隊
  def check(pool: List[BulletLike]): State = {

    // 無敵時間が1以上ならそもそも判定取らない
    if (0 < target.invincibleTime) return Live

    pool foreach {
      case s: Enemy if s.enable =>
        val ehit = if (s.live) isHit(s) else false
        if (!ehit) { // 当たってなければ子もチェック
          check(s.ownObjects.toList) match {
            case s: ShotBy[_] => return s
            case _ =>
          }
        } else { // foreachの中でearly returnっていいのかこれ...
          return ShotBy(s)
        }
      case s: Bullet if s.enable => // 当たってたらそれ返す, 当たってなかったら華麗にスルー
        if (isHit(s)) {
          return ShotBy(s)
        }
      case s: Emitter if s.enable => // Emitterに当たり判定はないので子のチェック
        check(s.ownObjects.toList) match {
          case s: ShotBy[_] => return s
          case _ =>
        }
      case _ => // ShotBy以外はスルー
    }
    Live // 全部のチェックスルーしたら当たってない
  }

}

class CollisionCheckerEnemy(target: Enemy) extends CollisionChecker[Enemy, Shot](target) {
  import MathUtil.sq

  def isHit(s: HasCollision) = sq(target.pos.x - s.pos.x) + sq(target.pos.y - s.pos.y) < sq(s.radius + target.radius)

  def check(pool: List[Shot]): State = {
    pool foreach {
      case s: Shot if s.enable =>
        if (isHit(s)) return ShotBy(s)
      case _ =>
    }
    Live
  }

}