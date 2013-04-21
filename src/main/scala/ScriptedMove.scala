package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.Constants.script._

object ScriptedMove {
  lazy val initialPc: Array[Int] = Array.fill(MAX_NEST){0}
  lazy val initialLc: Array[Int] = Array.fill(MAX_NEST){-1}
  lazy val initialVars: Array[Double] = Array.fill(MAX_NEST){0.0}
}

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

  // スクリプトの実行が終わった時に呼び出される。
  def onEndScript(delta: Int)

  protected def copyPc: Array[Int] = {
    val dest = Array.ofDim[Int](MAX_NEST)
    Array.copy(ScriptedMove.initialPc, 0, dest, 0, MAX_NEST)
    dest
  }

  protected def copyLc: Array[Int] = {
    val dest = Array.ofDim[Int](MAX_NEST)
    Array.copy(ScriptedMove.initialLc, 0, dest, 0, MAX_NEST)
    dest
  }

  protected def copyVars: Array[Double] = {
    val dest = Array.ofDim[Double](MAX_NEST)
    Array.copy(ScriptedMove.initialVars, 0, dest, 0, MAX_NEST)
    dest
  }
}
