package com.github.whelmaze.bulletf

trait ScriptedMove extends Movable {

  // 残りウェイトのカウンタ
  var waitCount: Int
  // どの階層でウェイト中か
  var waitingNest: Int
  // プログラムカウンタ
  val pc: Array[Int]
  // ループカウンタ
  val lc: Array[Int]
  // 変数($0～$9)
  val vars: Array[Double]
  // 死亡確認
  var enable: Boolean
  // 生成されてからの時間(frame)
  var time: Int

  // enable = falseのエイリアス
  def disable() {
    enable = false
  }

  // スクリプトのfireを実行した時に呼び出される。
  def produce(action: Behavior, kind: Symbol, pos: Position, speed: Double, angle: Angle)

  // スクリプトの実行が終わった時に呼び出される。
  def onEndScript(delta: Int)
}
