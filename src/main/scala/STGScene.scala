package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import collection.mutable
import scala.annotation.tailrec

class STGScene extends Scene {
//  this: Scene =>

  lazy val ship = new Ship()
  lazy val col = new CollisionChecker(ship)

  lazy val drawObject = mutable.ListBuffer.empty[Sprite]
  lazy val addPool = mutable.ListBuffer.empty[Sprite]
  lazy val runner = new ScriptRunner(addPool, ship)

  private[this] var c: Int = 0
  
  def name() = "STG"
  
  def update(delta: Int): Scene = {
    //val bustered = col.check(drawObject.toList)
    drawObject foreach { _.update(delta) }

    drawObject ++= addPool
    addPool.clear

    drawObject --= (drawObject filterNot {_.inside})
    
    /*bustered match {
      case Shooted(x) => new GameOverScene
      case Live => this
      case Lost => error("ねーよｗｗｗｗｗｗｗｗｗｗ")
    }*/
    
    this
  }

  def run() = {
  }

  override def draw() = {
    c += 1
    c %= 60
    if (c == 0) println("objects: " + drawObject.size)
    drawObject foreach { _.draw }
  }
  
  def init() = {

    //drawObject += ship

    List('loadtest).foreach{ s => runner.build(s.name) }
    
    drawObject += STGObjectFactory.newEmitter(runner.get('loadtest), 'nullpo)
    
    // script load
    
    println(name + " inited.")

  }

  def dispose() = {
    
    drawObject.clear
    
    println(name + " disposed.")

  }

}
