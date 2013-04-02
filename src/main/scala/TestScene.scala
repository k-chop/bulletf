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
  lazy val emitters = mutable.ListBuffer.empty[Emitter]
  lazy val runner = new BehaviorManager(ship)

  private[this] var c: Int = 0
  private[this] var b: Boolean = false
  
  def name() = "fps test"
  private[this] val st: Position = Position(constants.centerX, 10)

  def update(delta: Int): Scene = {

    ship.update(delta)
    emitters foreach { _.update(delta) }
    // 当たり判定どうしよ...

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
//    print(c+" ")
    if (c == 0) println("objects: " + emitters.foldLeft(0)((i,j)=> i + j.size))
    
    //  print(c+" ")
    ship.draw()
    emitters foreach { _.draw() }

  }
  
  def init() {
    BGM.play(0)

    emitters.clear()
    runner.clear()
    List('loadtest).foreach{ s => runner.build(s.name) }
    
    emitters += STGObjectFactory.newEmitter(runner.get('main), 'nullpo)
    
    // script load
    
    println(name + " inited.")

  }

  def dispose() {
    
    emitters.clear()
    
    println(name + " disposed.")

  }

}
