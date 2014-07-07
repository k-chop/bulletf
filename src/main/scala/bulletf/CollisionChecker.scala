package bulletf



import collection.mutable

abstract class CollisionChecker[A, B] {

  def check(target: A, pool: Seq[B]): State

}

object CollisionCheckerShip extends CollisionChecker[Ship, BulletLike] {
  import MathUtil.sq

  def checkAll(ship: Ship, enemies: Seq[Enemy], emitters: Seq[Emitter]): State = {
    CollisionCheckerShip.check(ship, enemies) match {
      case a @ ShotBy(_) => a
      case _ =>
        CollisionCheckerShip.check(ship, emitters) match {
          case b @ ShotBy(_) => b
          case _ => Live
        }
    }
  }

  def isHit(target: Ship, s: HasCollision) = sq(target.pos.x - s.pos.x) + sq(target.pos.y - s.pos.y) < sq(s.radius + target.radius)

  // 1つ見つけた時点で探索打ち切り隊
  def check(target: Ship, pool: Seq[BulletLike]): State = {

    // 無敵時間が1以上ならそもそも判定取らない
    if (0 < target.invincibleTime) return Live

    @annotation.tailrec
    def recur(ps: Seq[BulletLike]): State = ps match {

      case (e: Enemy) +: tail if e.enable =>
        val ehit = if (e.live) isHit(target, e) else false
        if (!ehit) { // 当たってなければ子もチェック
          check(target, e.childs) match {
            case s: ShotBy[_] => s
            case _ => recur(tail)
          }
        } else {
          ShotBy(e)
        }

      case (b: Bullet) +: tail if b.enable =>
        if (isHit(target, b)) ShotBy(b) else recur(tail)

      case (em: Emitter) +: tail if em.enable => // Emitterに当たり判定はないので子のチェック
        check(target, em.childs) match {
          case s: ShotBy[_] => s
          case _=> recur(tail)
        }

      case _ => // empty: 全部のチェックスルーしたので当たってない
        Live
    }

    recur(pool)
  }

}

object  CollisionCheckerEnemy extends CollisionChecker[Enemy, Shot] {
  import MathUtil.sq

  def checkAll(targets: Seq[Enemy], pool: Seq[Shot], eCallBack: (Enemy, Shot) => Unit): Unit = {

    @annotation.tailrec
    def recur(es: Seq[Enemy]): Unit = es match {
      case (e: Enemy) +: tail =>
        if (e.live) {
          check(e, pool) match {
            case ShotBy(s: Shot) =>
              eCallBack(e, s)
              recur(tail)
            case _ =>
              recur(tail)
          }
        } else recur(tail)
      case _ => // empty
    }

    recur(targets)
  }

  def isHit(target: Enemy, s: HasCollision) = sq(target.pos.x - s.pos.x) + sq(target.pos.y - s.pos.y) < sq(s.radius + target.radius)

  def check(target: Enemy, pool: Seq[Shot]): State = {

    @annotation.tailrec
    def recur(ss: Seq[Shot]): State = ss match {
      case (s: Shot) +: tail =>
        if (s.enable)
          if (isHit(target, s)) ShotBy(s) else recur(tail)
        else
          recur(tail)
      case _ => // all check through
        Live
    }

    recur(pool)
  }

}