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
  lazy val enemyBullet = mutable.ListBuffer.empty[Bullet]
  lazy val addPool = mutable.ListBuffer.empty[Bullet]
  lazy val runner = new BehaivorManager(addPool, ship)

  private[this] var c: Int = 0
  private[this] var b: Boolean = false
  
  def name() = "fps test"
  private[this] val st: Position = Position(constants.centerX, 10)

  def update(delta: Int): Scene = {

    ship.update(delta)
    enemyBullet foreach { obj => if(obj.inside) obj.update(delta) }
    val bustered = col.check(enemyBullet.toList)
    //if (c % 60 == 0)
    //  drawObject += STGObjectFactory.newBullet(BasicBehaivor, 'mini, st, 0.15, Angle(90))
    enemyBullet ++= addPool
    addPool.clear()

    if (c==0)
      enemyBullet --= (enemyBullet filterNot {_.inside})

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
    if (c == 0) println("objects: " + enemyBullet.size)
    
    //  print(c+" ")
    ship.draw()
    enemyBullet foreach { obj => if (obj.inside) obj.draw() }

  }
  
  def init() {

    enemyBullet.clear()
    runner.clear()
    List('loadtest).foreach{ s => runner.build(s.name) }
    
    enemyBullet += STGObjectFactory.newEmitter(runner.get('main), 'nullpo)
    
    // script load
    
    println(name + " inited.")

  }

  def dispose() {
    
    enemyBullet.clear()
    
    println(name + " disposed.")

  }

}
