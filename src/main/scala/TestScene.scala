package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import collection.mutable
import scala.annotation.tailrec

import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Keyboard._

class TestScene extends Scene with HasInnerFunc {
//  this: Scene =>

  lazy val ship = new Ship()
  lazy val col = new CollisionChecker(ship)
  lazy val emitters = mutable.ListBuffer.empty[Emitter]
  lazy val enemies = mutable.ListBuffer.empty[Enemy]
  lazy val runner = new BehaviorManager(ship)

  private[this] var c: Int = 0
  private[this] var b: Boolean = false
  
  def name() = "fps test"

  protected val updateFunc = new InnerUpdateFunc

  def update(delta: Int): Scene = {

    ship.update(delta)
    updateFunc.set(delta)
    emitters foreach updateFunc.func

    enemies foreach updateFunc.func

    (enemies.toList) foreach {
      case e: Enemy =>
        val cc = new CollisionChecker(e)
        cc.check(ship.ownObjects.toList) match {
          case Shooted(_) => e.disable()
          case _ =>
        }
      case _ =>
    }

    // fetch enemy_pool
    if (Global.enemy_pool.nonEmpty) {
      enemies ++= Global.enemy_pool.fetch()
    }

    if (Input.key.keydown(KEY_R)) {
      init()
    }

/*
    bustered match {
       case Shooted(x) => new GameOverScene
       case Live => this
       case Lost => sys.error("")
    }
*/
    this
  }

  def run() {
  }

  override def draw() {
    c += 1
    c %= 120
    b = !b
    //if (c == 0) println("objects: " + emitters.foldLeft(0)((i,j)=> i + j.size))
    
    ship.draw()
    emitters foreach drawFunc
    enemies foreach drawFunc
  }
  
  def init() {
    //BGM.play(0)

    emitters.clear()
    enemies.clear()
    runner.clear()
    List('loadtest).foreach{ s => runner.build(s.name) }
    
    emitters += STGObjectFactory.initialEmitter(runner.get('main))
    
    // script load
    
    println(name + " inited.")

  }

  def dispose() {
    
    emitters.clear()
    
    println(name + " disposed.")

  }

}
