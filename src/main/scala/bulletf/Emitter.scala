package bulletf



import collection.mutable

class Emitter(action: Behavior, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike[BulletLike] with CanProduceAll
{

  protected var ownObjects = mutable.ListBuffer.empty[BulletLike]

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
      ownObjects foreach CommonFunction.updateFunc
      if (time % 120 == 0) // per 2sec
        ownObjects = ownObjects filter CommonFunction.enableFunc
    }
  }

  def draw() {
    if (enable) ownObjects foreach CommonFunction.drawFunc
  }

  def size = ownObjects.size

  def clear() {
    // 消滅の際エフェクト出したりする場合もあるから、foreachでdisableにした方が良いかも？
    // 得点アイテムへの変化とかもすんだろ
    ownObjects.clear()
  }

}
