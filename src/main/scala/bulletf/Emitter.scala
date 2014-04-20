package bulletf



import collection.mutable

class Emitter(action: Behavior, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike with CanProduceAll with HasInnerFunc
{

/*  var waitCount: Int = -1
  var waitingNest: Int = 0
  val pc = copyPc
  val lc = copyLc
  val vars = copyVars
  var enable = true
  var time = 0*/

  var ownObjects = mutable.ListBuffer.empty[BulletLike]

  override def init() {
    action.init(this)
  }

  // スクリプトの実行が終わっていたら、持ち弾が0になるまで待ってから死ぬ
  def onEndScript() {
    if (ownObjects.isEmpty && nextAddPool.isEmpty) disable()
  }

  def update() {
    if (enable) {
      if (nextAddPool.nonEmpty) {
        ownObjects ++= nextAddPool
        nextAddPool.clear()
      }
      time += 1
      action.run(this)
      ownObjects foreach updateFunc
      if (time % 120 == 0) // per 2sec
        ownObjects = ownObjects filter enableFunc
    }
  }

  def draw() {
    if (enable) ownObjects foreach drawFunc
  }

  def size = ownObjects.size

  def clear() {
    // 消滅の際エフェクト出したりする場合もあるから、foreachでdisableにした方が良いかも？
    // 得点アイテムへの変化とかもすんだろ
    ownObjects.clear()
  }

}
