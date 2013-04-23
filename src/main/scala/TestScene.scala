package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import collection.mutable
import scala.annotation.tailrec

import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Keyboard._
import com.github.whelmaze.bulletf.ui.ScoreBoard

class TestScene extends Scene with HasInnerFunc {
//  this: Scene =>

  lazy val ship = new Ship()
  lazy val col = new CollisionCheckerShip(ship)
  lazy val emitters = mutable.ListBuffer.empty[Emitter]
  lazy val enemies = mutable.ListBuffer.empty[Enemy]
  lazy val runner = new BehaviorManager(ship)
  lazy val score: ScoreBoard = ui.ScoreBoard.init(0)
  Global.scoreboard.set(score)

  private[this] var c: Int = 0
  private[this] var b: Boolean = false
  
  def name() = "fps test"

  protected val updateFunc = new InnerUpdateFunc

  def update(delta: Int): Scene = {

    ship.update(delta)
    updateFunc.set(delta)
    emitters foreach updateFunc.func
    enemies foreach updateFunc.func

    // ↓これ各シーンでやることじゃなくね……
    // collisioncheckerに丸投げで良いのでは

    // enemy collision check
    (enemies.toList) foreach { e =>
      if (e.live) {
        val cc = new CollisionCheckerEnemy(e)
        cc.check(ship.ownObjects.toList) match {
          case ShotBy(s: Shot) =>
            //println(s"hit: ${s.pos}")
            e.damage(s)
            Global.scoreboard.add(10)
            SoundEffect.playSymbol('test2)
          case _ =>
        }
      }
    }

    // ship collision check
    val nexts = if (c % 1 == 0) {
    col.check(enemies.toList) match {
      case ShotBy(x) =>
        println(s"shot by ${x.asInstanceOf[HasCollision].pos}")
        new TitleScene
      case _ =>
        col.check(emitters.toList) match {
          case ShotBy(x) =>
            println(s"shot by ${x.asInstanceOf[HasCollision].pos}")
            new TitleScene
          case _ =>
            this
        }
    }
    } else this

    // fetch enemy_pool
    if (Global.enemy_pool.nonEmpty) {
      enemies ++= Global.enemy_pool.fetch()
    }

    if (Input.key.keydown(KEY_R)) {
      init()
    }

    c += 1

    nexts
  }

  def run() {
  }

  override def draw() {
    b = !b
    //if (c == 0) println("objects: " + emitters.foldLeft(0)((i,j)=> i + j.size))
    
    ship.draw()
    emitters foreach drawFunc
    enemies foreach drawFunc
    score.draw()
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
