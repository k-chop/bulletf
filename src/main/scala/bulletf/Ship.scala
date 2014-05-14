package bulletf


import scala.math.{sin, cos}
import scala.collection.mutable
import scala.language.postfixOps

object Ship {

  def initialPosition = Position(constants.centerX, constants.screenHeight - 50)

}

class Ship extends HasCollision with HasInnerFunc with LifeAccess {

  // 自機のショット、そのうちキャッシュするようにする
  var ownObjects = mutable.ListBuffer.empty[Shot]

  // オプション: 最大10(仮)
  lazy val options: mutable.ListBuffer[ShipOption] = {
    def n(q: Double) = {
      val r = q.toRadians
      val len = 30
      Position(cos(r)*len*1.3 + pos.x, sin(r)*len + pos.y)
    }
    val a = mutable.ListBuffer.empty[ShipOption]
    (0 to 359 by 90 zipWithIndex) foreach { case (i, j) =>
      a += new ShipOption(this, n(i), i)
    }
    a
  }

  val sprite: Sprite = Sprite.get(Resource.shipGraphic)
  // 初期位置
  var pos = Ship.initialPosition
  // 当たり判定の半径
  val radius = 4.0
  // インスタンス生成から経っている時間(フレーム)
  var time: Int = 0
  // 無敵時間
  var invincibleTime: Int = 0
  // 残機
  private[this] var remLives: Int = 3

  def life = remLives

  def damage(src: BulletLike) {
    val pt = src match {
      case s: Enemy =>
        s.damage(Shot.hurlShip) // 敵に体当たりダメージ
        1
      case s: Bullet =>
        s.disable() // 当たった弾は消滅
        1
      case _ => 0
    }
    if (0 < pt) {
      //remLives -= pt
      invincibleTime = 120 // 無敵時間2秒
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('player_death), 'ship, pos.cloneBy(0,0), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(-6,-8), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(+12,-4), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(-2,+7), Angle.zero, 0))
      Global.effect_pool.set(STGObjectFactory.newEffect(BehaviorManager.get('muzzle), 'ENG01D, pos.cloneBy(+4,+11), Angle.zero, 0))
    }
  }

  def update() {
    if (0 < invincibleTime) invincibleTime -= 1 // 無敵時間減らす
    ShipBehavior.move(this)
    options foreach updateFunc
    ownObjects foreach updateFunc
    ShipBehavior.shot(this)
    time += 1
    if (time % 120 == 0) ownObjects = ownObjects.filter(enableFunc)
  }

  def draw() {
    // 無敵時間が0の時は普通に、無敵時間の場合は3フレームに1回だけ描画(点滅)
    if ( (invincibleTime <= 0) || (invincibleTime % 3 == 0) ) {
      sprite.draw(pos)
    }
    options foreach drawFunc
    ownObjects.foreach(drawFunc)
  }

  // 外部で利用する際mutableだと困るので
  // でも現状普通にownObjectに外からアクセスできてしまうので意味が無い
  def shots: List[Shot] = ownObjects.toList

}

trait LifeAccess {
  def life: Int
}

class ShipOption(parent: HasCollision, initPos: Position, var degree: Double) extends HasCollision with Runnable with Drawable {

  val sprite = Sprite.get('a_box01)
  var speed = 4

  var time = 0

  override var pos: Position = initPos
  override val radius: Double = 0

  override def draw(): Unit = {
    sprite.draw(pos, 0f, 1f, 0.5f, time)
  }

  def update(): Unit = {
    if (Input(Input.keys.SHOT) && Input(Input.keys.LASER))
      updateInLaser()
    else
      updateOrdinary()

    time += 1
    time %= 60
  }
  
  def updateOrdinary(): Unit = {
    degree += speed
    val r = degree.toRadians
    val len = 30
    pos.x = cos(r) * len*1.3 + parent.pos.x
    pos.y = sin(r) * len + parent.pos.y
  }
  
  def updateInLaser(): Unit = {
    degree += speed * 2
    val r = degree.toRadians
    val len = 15
    pos.x = cos(r) * len + parent.pos.x
    pos.y = sin(r) * (len/3) + parent.pos.y - 25
  }

  def posStep(): Unit = {

  }

}