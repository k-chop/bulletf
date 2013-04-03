package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import collection.mutable
import scala.annotation.tailrec

class STGScene extends Scene {
//  this: Scene =>

  lazy val ship = new Ship()
  lazy val col = new CollisionChecker(ship)

  lazy val enemyBullets = mutable.ListBuffer.empty[Emitter]
  lazy val runner = new BehaviorManager(ship)

  private[this] var c: Int = 0
  
  def name() = "STG"
  
  def update(delta: Int): Scene = {
    //val bustered = col.check(drawObject.toList)
    enemyBullets foreach { _.update(delta) }

    /*bustered match {
      case Shooted(x) => new GameOverScene
      case Live => this
      case Lost => error("ねーよｗｗｗｗｗｗｗｗｗｗ")
    }*/
    
    this
  }

  def run() {
  }

  override def draw() {
    c += 1
    c %= 60
    if (c == 0) println("objects: " + enemyBullets.size)
    enemyBullets foreach { _.draw() }
  }
  
  def init() {

    //drawObject += ship

    List('loadtest).foreach{ s => runner.build(s.name) }
    
    enemyBullets += STGObjectFactory.initialEmitter(runner.get('loadtest))
    
    // script load
    
    println(name + " inited.")

  }

  def dispose() {
    
    enemyBullets.clear()
    
    println(name + " disposed.")

  }

}
