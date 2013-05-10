package com.github.whelmaze.bulletf

object Shot {

  // 体当たり時の攻撃判定用。再利用するのでdestroyされない
  lazy val hurlShip = new Shot(BasicBehavior, 'null, Position.outside, Angle.zero, 0) {
    override val power = 20
    override def destroy() {}
  }

}

class Shot(val action: Behavior, val resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with HasCollision
{

  val sprite: Sprite = Sprite.get(resource)

/*  var waitCount = -1
  var waitingNest = 0

  val pc = copyPc
  val lc = copyLc
  val vars = copyVars
  var enable = true
  var time = 0*/

  val power = 5

  def destroy() {
    disable()
  }

  override def init() {
    action.init(this)
  }

  // 当たり判定の半径
  val radius = sprite.rect.w / 4.0

  // スクリプトの実行が終わったら等速直線運動へシフト
  def onEndScript(delta: Int) {
    BasicBehavior.run(delta)(this)
  }

  def update(delta: Int) {
    if (enable) action.run(delta)(this)
    if (!inside) disable()
  }

  def draw() {
    if (enable) sprite.draw(pos, angle.dir-90, 1.0, 1.0, time)
  }

  def inside = (0-(radius*2) <= pos.x  && pos.x <= constants.screenWidth+(radius*2) && 0-(radius*2) <= pos.y && pos.y <= constants.screenHeight+(radius*2))

}