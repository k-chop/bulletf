package bulletf



import Constants.script._
import collection.mutable

object Enemy {

  final private[Enemy] val sti = (s: String) => s.toInt
}

class Enemy(action: Behavior, resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike[BulletLike] with CanProduceAll with HasCollision with HasInnerFunc
{

  val sprite = Sprite.get(resource)
  val radius: Double = sprite.rect.w / 4.0

  var live: Boolean = true
  // 体力
  var health: Int = 10
  // 移動用の状態
  val status = MoveStrategyStatus.nop

  protected var ownObjects = mutable.ListBuffer.empty[BulletLike]

  // initブロックの実行
  override def init() {
    action.init(this)
  }

  override def setParam(params: Map[Symbol, String]) {
    val t = params.get('health) map Enemy.sti
    this.health = if (t.isEmpty) 10 else t.get
  }

  override def disable() {
    super.disable()
    live = false
  }

  // スクリプトの実行が終わっていてまだ弾が残っているなら等速直線運動
  // 弾も消え、自身も画面外に行ったら死亡
  def onEndScript() {
    if (ownObjects.isEmpty && !live) disable()
    if (live) BasicBehavior.run(this)
  }

  def update() {
    if (enable) {
      if (nextAddPool.nonEmpty) {
        ownObjects ++= nextAddPool // この時点でinitが済んでる
        nextAddPool.clear()
      }
      if (!inside) live = false
      if (live) {
        time += 1
        if (0 < status.restFrame) { // 移動が済んでない場合移動、asyncならactionも一緒に実行
          MoveStrategy.move(this, status)
          if (status.async)
            action.run(this)
        } else { // 移動が済んでる場合action実行
          action.run(this)
        }
        status.updated()
      }
      ownObjects foreach updateFunc
      if (time % 120 == 0) // per 2sec
        ownObjects = ownObjects.filter(enableFunc)
    }
  }

  def draw() {
    if (enable) {
      if (live) sprite.draw(pos, 0, 1.0, 1.0)
      ownObjects.foreach(drawFunc)
    }
  }

  def damage(s: Shot) {
    health -= s.power
    if (health < 0) {
      die()
    }
  }

  def die() {
    live = false
    // 死んだ時もスクリプトで制御し隊
    effect(BehaviorManager.get('death), 'invader01, pos.cloneBy(0,0), Angle.zero, 0)
  }

  def size = ownObjects.size

  def inside = 0 - (radius * 2) <= pos.x && pos.x <= constants.screenWidth + (radius * 2) && 0 - (radius * 2) <= pos.y && pos.y <= constants.screenHeight + (radius * 2)
}
