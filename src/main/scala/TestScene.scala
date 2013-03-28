package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import collection.mutable
import scala.annotation.tailrec

import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Keyboard._

class TestScene extends Scene {
//  this: Scene =>

  lazy val ship = new Ship()
  lazy val col = new CollisionChecker(ship)
  lazy val drawObject = mutable.ListBuffer.empty[Sprite]
  lazy val addPool = mutable.ListBuffer.empty[Sprite]
  lazy val runner = new ScriptRunner(addPool, ship)

  private[this] var c: Int = 0
  private[this] var b: Boolean = false
  
  def name() = "fps test"
  private[this] val st: Position = Position(constants.centerX, 10)

  def update(delta: Int): Scene = {

    ship.update(delta)
    drawObject foreach { obj => if(obj.inside) obj.update(delta) }
    val bustered = col.check(drawObject.toList)
    //if (c % 60 == 0)
    //  drawObject += STGObjectFactory.newBullet(BasicBehaivor, 'mini, st, 0.15, Angle(90))
    drawObject ++= addPool
    addPool.clear()

    if (c==0)
      drawObject --= (drawObject filterNot {_.inside})

    if (Input.key.keydown(KEY_R)) {
      init()
    }

    bustered match {
       case Shooted(x) => new GameOverScene
       case Live => this
       case Lost => sys.error("")
    }
  }

  def run() {
  }

  override def draw() {
    c += 1
    c %= 120
    b = !b
//    print(c+" ")
    if (c == 0) println("objects: " + drawObject.size)
    
    //  print(c+" ")
    ship.draw()
    drawObject foreach { obj => if (obj.inside) obj.draw() }

  }
  
  def init() {

    drawObject.clear()
    runner.clear()
    List('loadtest).foreach{ s => runner.build(s.name) }
    
    drawObject += STGObjectFactory.newEmitter(runner.get('main), 'nullpo)
    
    // script load
    
    println(name + " inited.")

  }

  def dispose() {
    
    drawObject.clear
    
    println(name + " disposed.")

  }

}
