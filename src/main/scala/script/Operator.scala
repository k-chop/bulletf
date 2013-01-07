package com.github.whelmaze.bulletf
package script

/** 
* Op
* 弾の振る舞いを定義するクラス群の親トレイト．
* 
*/
sealed trait Op

/** 
* Repeat
* subseq以下をtime回数繰り返す．timeが0なら無限ループ．
* 
*/
case class Repeat(time: Int, subseq: Array[Op]) extends Op {
  override def toString() = "Repeat(%d, %s)" format (time, subseq.deep.toString)
} 

/** 
* Wait
* 指定したフレーム数だけアクションを実行せず待機する．
*   
*/
case class Wait(time: Int) extends Op

/** 
* Nop
* パースに失敗した残骸．
*   
*/
case object Nop extends Op

/** 
* Fire
* 弾を撃つ．弾はフレームの終わりに登録され，次のフレームから動き出す．
* 
*/
case class Fire(action: Symbol, kind: Symbol, dir: Container, speed: Container) extends Op

/** 
* SetVar
* Double型の変数をセットする．$0 - $9までが使用可能で，アクションごとに共有される．
* 
*/
case class SetVar(idx: Int, value: Container) extends Op

/** 
* GetVar
* Double型の変数を取得する．$0 - $9までが使用可能で，アクションごとに共有される．
* 
*/
case class GetVar(idx: Int) extends Op

/** 
* UpdateVar
* 変数にvalueの値を加算して更新する．
*   
*/
case class UpdateVar(idx: Int, value: Container) extends Op 

/** 
* SetSpeed
* スピードを変える．
* サポートするparamの値は absolute(絶対指定), relative(現在速度を0とする)
* paramを省略した場合absoluteを指定したことになる．
*/
case class SetSpeed(value: Container, param: Symbol) extends Op

/** 
* SetDirection
* 方向を変える．
* サポートするparamの値は absolute(絶対指定), aim(自機狙い基準)
* paramを省略した場合absoluteを指定したことになる．
*/
case class SetDirection(value: Container, param: Symbol) extends Op

