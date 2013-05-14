package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._
import scala.collection.mutable.ListBuffer

import scala.annotation.tailrec
import util.Try

class ScriptBehavior(val blocks: ScriptBlocks) extends Behavior {
  import implicits.autoWrapAngle

  def extract(unit: ScriptControlled)(c: Container): Double = c match {
    case StateVar(p) => p match {
      case 'x => unit.pos.x
      case 'y => unit.pos.y
      case 'aim => Global.shipReference.aim(unit.pos)
      case 'speed => unit.speed
      case 'angle => unit.angle.dir
      case 'px => Global.shipReference.x
      case 'py => Global.shipReference.y
    }

    // StrVarは解析時点でSymbolに変換するからこの時点では存在しない
    case EVar(getvar) => unit.vars(getvar.idx)
    case DVar(value) => value
    case RandomVar(begin, end) => scala.util.Random.nextDouble()*(end-begin)+begin
    case Negate(in) => extract(unit)(in) * -1
  }

  // GC回避用
  private[ScriptBehavior] class Extractor(private[this] var _unit: ScriptControlled) {

    def set(unit: ScriptControlled) { _unit = unit }

    def unset() { _unit = null }

    def apply(c: Container): Double = {
      if (_unit != null)
        extract(_unit)(c)
      else {
        System.err.println(s"call Extractor#apply without ScriptedMove_unit\ncontainer info: ${c.toString}\ntopseq info: ${blocks.runBlock.deep.toString()}")
        0
      }
    }
  }

  val ex = new Extractor(null)

  def step(t_nestLevel: Int, t_seq: Array[Op], unit: ScriptControlled, delta: Int, onInit: Boolean) {

    @tailrec
    def recur(nestLevel: Int, seq: Array[Op]) {
      def incPC() {
        unit.pc(nestLevel) += 1
      }

      if (0 <= unit.waitCount) { // wait中

        unit.waitCount -= 1
        if (unit.waitCount == -1) {
          unit.pc(unit.waitingNest) += 1
          recur(0, blocks.runBlock)
        } else {
          BasicBehavior.run(delta)(unit)
        }

      } else if (unit.pc(nestLevel) < seq.length) { // 次のOpを実行

        seq(unit.pc(nestLevel)) match {
          case Wait(_) if onInit => // init中はwaitをスルー
            incPC(); recur(nestLevel, seq)

          case VWait(_) if onInit =>
            incPC(); recur(nestLevel, seq)

          case Wait(time) =>
            unit.waitCount = if (time < 1) 1 else time
            //println("wait:" + bullet.waitCount)
            unit.waitingNest = nestLevel
            recur(nestLevel, seq)

          case VWait(t) => // なんという実装の重複...
            val time = ex(t).toInt
            unit.waitCount = if (time < 1) 1 else time
            unit.waitingNest = nestLevel
            recur(nestLevel, seq)

          case Fire(action, kind, dir, speed) => unit match {
            case c: CanProduceAll =>
              c.fire(BehaviorManager.get(action), kind, Position(unit.pos.x, unit.pos.y), ex(dir).toAngle, ex(speed))
            case _ =>
          }; incPC(); recur(nestLevel, seq)

          // genEnemy, emitは基本的に親(発射元)の速度,角度を受け継ぐ。
          // 必要なら定義先で改めて定義すればよい。
          case GenEnemy(action, kind, x, y) => unit match {
            case c: CanProduceToGlobal =>
              c.genEnemy(BehaviorManager.get(action), kind, Position(ex(x), ex(y)), Angle(unit.angle.dir), unit.speed)
            case _ =>
          }; incPC(); recur(nestLevel, seq)

          case Emit(action, x, y) => unit match {
            case c: CanProduceAll =>
              c.emit(BehaviorManager.get(action), Position(ex(x), ex(y)), Angle(unit.angle.dir), unit.speed)
            case _ =>
          }; incPC(); recur(nestLevel, seq)

          case GenEffect(action, kind, x, y) => unit match {
            case c: CanProduceToGlobal =>
              c.effect(BehaviorManager.get(action), kind, Position(ex(x), ex(y)), Angle(unit.angle.dir), unit.speed)
            case _ =>
          }; incPC(); recur(nestLevel, seq)

          case Repeat(time, childs) =>
            if ( unit.lc(nestLevel+1) == -1) {
              unit.lc(nestLevel+1) = time
              if (unit.time % 120 == 0) println("repeat:" + time +", into NestLevel:"+(nestLevel+1))
            }
            recur(nestLevel+1, childs) // 1段階ネスト

          case SetVar(idx, value) =>
            unit.vars(idx) = ex(value)
            incPC()
            recur(nestLevel, seq)

          case GetVar(idx) => throw new Exception("変数取得してどうすんの？")

          case Nop =>
            incPC()
            recur(nestLevel, seq)

          case UpdateVar(idx, value) =>
            unit.vars(idx) += ex(value)
            incPC()
            recur(nestLevel, seq)

          case SetDirection(dir, param) => param match {
            case 'absolute =>
              unit.angle.update(ex(dir))
            case 'aim =>
              unit.angle.update(Global.shipReference.aim(unit.pos))
            case 'relative =>
              unit.angle += ex(dir)

          }; incPC(); recur(nestLevel, seq)

          case SetSpeed(spd, param) => param match {
            case 'absolute =>
              unit.speed = ex(spd)
            case 'relative =>
              unit.speed += ex(spd)
          }; incPC(); recur(nestLevel, seq)

          case PlaySound(param, pitch, vol) => {
            SoundSystem.playSymbol(param, ex(pitch).toFloat, ex(vol).toFloat)
          }; incPC(); recur(nestLevel, seq)

          case MoveTo(handling, x, y, time, async, opt) =>
            unit match {
              case e: Enemy =>
                e.status.clearVar() // 変数クリア
                e.status.handling = handling
                e.status.target.x = ex(x)
                e.status.target.y = ex(y)
                e.status.restFrame = ex(time).toInt
                e.status.async = async
                // 変数を解凍して代入
                var i = opt.length-1
                while(0 <= i) {
                  e.status.vars(i) = ex(opt(i))
                  i -= 1
                }
              case _ => // スルー
            }
            incPC(); // PCインクリメントしたらwait等がなくてもそこで一旦終了。

          // SetScale, SetAlphaはEffectのみに作用するのでここに置くのはちょっと違う気もする
          case SetScale(scaleCon) => {
            unit match {
              case e: Effect => e.scale = ex(scaleCon)
              case _ =>
            }
          }; incPC(); recur(nestLevel, seq)

          case SetAlpha(alphaCon) => {
            unit match {
              case e: Effect => e.alpha = ex(alphaCon)
              case _ =>
            }
          }; incPC(); recur(nestLevel, seq)

          case MusicLoad(id) =>
            BGM.load(id)
            incPC(); recur(nestLevel, seq)

          case MusicRelease(id) =>
            BGM.release(id)
            incPC(); recur(nestLevel, seq)

          case MusicPlay(id, volCon) =>
            BGM.play(id, ex(volCon).toFloat)
            incPC(); recur(nestLevel, seq)

          case MusicStop =>
            BGM.stop()
            incPC(); recur(nestLevel, seq)

        }

      } else if (nestLevel != 0) {
        //println("owatteru")
        unit.lc(nestLevel) match {
          case 1 => // 回数分が終了していればループを終了
            unit.lc(nestLevel) = -1
            unit.pc(nestLevel) = 0
            unit.pc(nestLevel - 1) += 1
            // また頭からたどる．親も保持した方がいいのか……
            //println("[repeat end] NestLevel:"+nestLevel)
            recur(0, blocks.runBlock)
          case 0 => // 無限ループなのでpcリセットして続行
            unit.pc(nestLevel) = 0
            recur(nestLevel, seq)
          case n => // ループカウンタ減らしてpcリセットして続行
            //println("dec-LC :"+(bullet.lc(nestLevel))+"["+nestLevel+"]")
            unit.lc(nestLevel) -= 1
            unit.pc(nestLevel) = 0
            recur(nestLevel, seq)
        }
      } else { // もう全部終わってるならScriptedMove側に移譲(ただしinitブロック中では単に終了)
        if (!onInit) unit.onEndScript(delta)
        //else println(s"initblock end $unit")
      }
    }

    recur(t_nestLevel, t_seq)
  }

  // 1度目のupdateの前に実行されるブロックを呼び出す
  override def init(unit: ScriptControlled) {
    ex.set(unit)
    step(0, blocks.initBlock, unit, 0, onInit = true)
    ex.unset()
    unit.clearLcPc() // Lc, Pcは共有なのでブロックが変わる時はリセット
  }

  def run(delta: Int)(unitA: ScriptControlled) {
    ex.set(unitA)

    step(0, blocks.runBlock, unitA, delta, onInit = false) // 頭からたどる
    ex.unset()
  }

  def evaluate(seq: Array[Op], op: Op)(implicit bullet: Bullet) {
    //def incPC() = bullet.pc += 1
  }
  
}
