package com.github.whelmaze.bulletf
package script

import scala.collection.mutable.{ HashMap, ListBuffer } 
import scala.io.Source

// クラス名おかしくね……ActionRepositoryとかか？

class ScriptRunner(val pool: ListBuffer[Sprite], val ship: Ship) {

  val map = HashMap.empty[Symbol, Behaivor]

  map += ('simple -> BasicBehaivor)
  
  def build(ref: String) = {
    val src = Source.fromFile( Resource.scriptPath + ref + ".dsf" ).mkString("")
    
    DSFParser.parse(src) foreach { case (name, ops) =>
      val action = new ScriptBehaivor(this, pool, ops)
      println("action[" + name.name + "] created.")
      map += ( name -> action )
    }
  }
  
  def get(ref: Symbol): Behaivor = map.get(ref) match {
    // 目的のアクションがなかった場合どうする？
    // →buildの時点で依存するアクションはロードしておくべき．
    // あとで．
    case Some(x) => x
    case None => throw new Exception//build(ref.name)
  }

  def getAimToShip(bullet: Bullet) = {
    import scala.math._
    toDegrees( atan2(ship.pos.y - bullet.pos.y, ship.pos.x - bullet.pos.x) )
  }
  
}
