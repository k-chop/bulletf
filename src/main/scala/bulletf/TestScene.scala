package bulletf

import collection.mutable

import org.lwjgl.input.Keyboard._
import ui._

class TestScene extends Scene with HasInnerFunc {
//  this: Scene =>

  lazy val ship = new Ship()
  lazy val col = new CollisionCheckerShip(ship)
  lazy val emitters = mutable.ListBuffer.empty[Emitter]
  lazy val enemies = mutable.ListBuffer.empty[Enemy]
  lazy val effects = mutable.ListBuffer.empty[Effect]
  lazy val score: ClearableScoreBoard = ui.ScoreBoard.init(0)
  lazy val lives: LifeBoard = new ui.LifeBoard(ship)
  lazy val background: BackGround = new BackGroundBasicStars
  Global.scoreboard.set(score)
  Global.shipReference.set(ship)

  private[this] var c: Int = 0
  private[this] var b: Boolean = false
  
  def name() = "fps test"

  def update(delta: Int): Scene = {

    // fetch from enemy_pool
    if (Global.enemy_pool.nonEmpty) {
      enemies ++= Global.enemy_pool.fetch()
    }

    // fetch from effect_pool
    if (Global.effect_pool.nonEmpty) {
      effects ++= Global.effect_pool.fetch()
    }

    // need init objects
    if (Global.needInit_pool.nonEmpty) {
      Global.needInit_pool.allInit()
    }


    background.update(delta)
    ship.update(delta)

    updateFunc.set(delta)
    emitters foreach updateFunc.func
    enemies foreach updateFunc.func
    effects foreach updateFunc.func

    // ↓これ各シーンでやることじゃなくね……
    // collisioncheckerに丸投げで良いのでは

    // enemy collision check
    enemies.toList foreach { e =>
      if (e.live) {
        val cc = new CollisionCheckerEnemy(e)
        cc.check(ship.ownObjects.toList) match {
          case ShotBy(s: Shot) =>
            //println(s"hit: ${s.pos}")
            e.damage(s)
            s.destroy()
            Global.scoreboard.add(10)
            SoundSystem.playSymbol('test2, vol = 0.04f)
          case _ =>
        }
      }
    }

    // ship collision check
    val damaged = col.check(enemies.toList) match {
      case ShotBy(x) =>
        println(s"shot by ${x.asInstanceOf[HasCollision].pos}")
        ship.damage(x)
        true
      case _ =>
        col.check(emitters.toList) match {
          case ShotBy(x) =>
            println(s"shot by ${x.asInstanceOf[HasCollision].pos}")
            ship.damage(x)
            true
          case _ => false
        }
    }

    // 自機がダメージ食らった場合
    val nextScene = if (damaged) {
      if (ship.life <= 0) { //ライフが0以下(死亡)
        new GameOverScene
      } else { // 残機まだある場合は初期位置に戻す
        ship.pos = Ship.initialPosition
        this
      }
    } else this

    if (Input.key.keydown(KEY_R)) {
      init()
    }

    c += 1

    nextScene
  }

  def run() {
  }

  override def draw() {
    b = !b
    //if (c == 0) println("objects: " + emitters.foldLeft(0)((i,j)=> i + j.size))

    background.draw()
    ship.draw()
    emitters foreach drawFunc
    enemies foreach drawFunc
    effects foreach drawFunc
    score.draw()
    lives.draw()
  }
  
  def init() {

    emitters.clear()
    enemies.clear()
    BehaviorManager.clear()
    score.clear()
    effects.clear()
    Global.enemy_pool.clear()
    Global.effect_pool.clear()
    Global.needInit_pool.clear()

    List('loadtest, 'effects).foreach{ s => BehaviorManager.build(s.name) }
    
    emitters += STGObjectFactory.initialEmitter(BehaviorManager.get('main))
    
    // script load
    
    println(name + " inited.")

  }

  def dispose() {
    
    emitters.clear()
    
    println(name + " disposed.")

  }

}
