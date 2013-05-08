package com.github.whelmaze.bulletf

class Effect(var action: Behavior, resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with CanProduceToGlobal
{

  var sprite: Sprite = Sprite.get(resource)

  var scale: Double = 1.0
  var alpha: Double = 1.0

  // 再利用しちゃうよ
  def reborn(action: Behavior, resource: Symbol, pos: Position, angle: Angle, speed: Double) {
    this.action = action
    this.sprite = Sprite.get(resource)
    this.pos = pos
    this.angle = angle
    this.speed = speed
  }

  def update(delta: Int) {
    if (enable) action.run(delta)(this)
  }

  /** スクリプトの実行が終わった時に呼び出される。
    * 子供はいないので即殺
    */
  def onEndScript(delta: Int) {
    disable()
  }

  def draw() {
    if (enable) sprite.draw(pos, angle.dir, scale, alpha, time)
  }

}
