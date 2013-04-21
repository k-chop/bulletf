package com.github.whelmaze.bulletf

import collection.mutable

class CollisionChecker(val owner: HasCollision) {
  
  def check(pool: List[HasCollision]): State = pool find {
    case b: HasCollision if collision_?(b) => true
    case _ => false
  } match {
    case Some(x: HasCollision) => Shooted(x)
    case None => Live
    case _ => Lost
  }

  final def collision_?(target: HasCollision): Boolean = {
    sq(owner.pos.x - target.pos.x) + sq(owner.pos.y - target.pos.y) < sq(target.radius + owner.radius)
  }

  final def sq(d: Double) = d * d
}
