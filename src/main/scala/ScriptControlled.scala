package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.Constants.script._

object ScriptControlled {
  lazy val initialPc: Array[Int] = Array.fill(MAX_NEST){0}
  lazy val initialLc: Array[Int] = Array.fill(MAX_NEST){-1}
  lazy val initialVars: Array[Double] = Array.fill(MAX_NEST){0.0}
}

trait ScriptControlled extends Movable with CanInit with ParamCustomizable {

  // 残りウェイトのカウンタ
  var waitCount: Int = -1
  // どの階層でウェイト中か
  var waitingNest: Int = 0
  // プログラムカウンタ
  val pc: Array[Int] = copyPc
  // ループカウンタ
  val lc: Array[Int] = copyLc
  // 変数($0～$9)
  val vars: Array[Double] = copyVars
  // 死亡確認
  var enable: Boolean = true
  // 生成されてからの時間(frame)
  var time: Int = 0

  // enable = falseのエイリアス
  def disable() {
    enable = false
  }

  def clearLcPc() {
    var i = 0
    while(i < MAX_NEST) {
      pc(i) = 0; i += 1
    }
    i = 0
    while(i < MAX_NEST) {
      lc(i) = -1; i += 1
    }
    i = 0
  }

  // スクリプトの実行が終わった時に呼び出される。
  def onEndScript(delta: Int)

  //----------------------------------------------

  protected def copyPc: Array[Int] = {
    val dest = Array.ofDim[Int](MAX_NEST)
    Array.copy(ScriptControlled.initialPc, 0, dest, 0, MAX_NEST)
    dest
  }

  protected def copyLc: Array[Int] = {
    val dest = Array.ofDim[Int](MAX_NEST)
    Array.copy(ScriptControlled.initialLc, 0, dest, 0, MAX_NEST)
    dest
  }

  protected def copyVars: Array[Double] = {
    val dest = Array.ofDim[Double](MAX_NEST)
    Array.copy(ScriptControlled.initialVars, 0, dest, 0, MAX_NEST)
    dest
  }
}
