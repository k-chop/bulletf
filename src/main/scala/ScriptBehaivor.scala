package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._
import scala.collection.mutable.ListBuffer

import scala.annotation.tailrec

class ScriptBehaivor(val runner: ScriptRunner, val pool: ListBuffer[Sprite] ,val topseq: Array[Op]) extends Behaivor {
  import implicits.angle2double

  def extract(runner: ScriptRunner, bullet: Bullet)(c: Container): Double = c match {

    case StateVar(p) => p match {
      case 'x => bullet.pos.x
      case 'y => bullet.pos.y
      case 'aim => runner.getAimToShip(bullet)
      case 'speed => bullet.speed
      case 'angle => bullet.angle
    }

    // StrVarは解析時点でSymbolに変換するからこの時点では存在しない
    case EVar(getvar) => bullet.vars(getvar.idx)
    case DVar(value) => value
    case RandomVar(begin, end) => scala.util.Random.nextDouble()*(end-begin)+begin
    case Negate(in) => extract(runner, bullet)(in) * -1
    
  }
  
  def run(delta: Int)(implicit bullet: Bullet) = {
    lazy val ex = extract(runner, bullet) _
    
    @tailrec
    def recur(nestLevel: Int, seq: Array[Op]): Unit = {
      def incPC() = bullet.pc(nestLevel) += 1

      //println("w:"+bullet.waitCount+",nest:"+nestLevel+",pc:"+bullet.pc(nestLevel))
      
      if (0 <= bullet.waitCount) { // wait中
        
        bullet.waitCount -= 1
        if (bullet.waitCount == -1) {
          bullet.pc(bullet.waitingNest) += 1
          recur(0, topseq)
        } else {
          BasicBehaivor.run(delta)(bullet)
        }
        
      } else if (bullet.pc(nestLevel) < seq.length) { // 次のOpを実行
        
        seq(bullet.pc(nestLevel)) match {
          case Wait(time) =>
            bullet.waitCount = if (time < 1) 1 else time
            //println("wait:" + bullet.waitCount)
            bullet.waitingNest = nestLevel
            recur(nestLevel, seq)

          case Fire(action, kind, dir, speed) =>
            pool += STGObjectFactory.newBullet(runner.get(action), kind, bullet.pos, ex(speed), Angle( ex(dir) ))
            //println("fire")
            incPC()
            recur(nestLevel, seq)

          case Repeat(time, childs) =>
            if ( bullet.lc(nestLevel+1) == -1) {
              bullet.lc(nestLevel+1) = time
              //println("repeat:" + time +", into NestLevel:"+(nestLevel+1))
            }
            recur(nestLevel+1, childs) // 1段階ネスト

          case SetVar(idx, value) =>
            bullet.vars(idx) = ex(value)
            incPC()
            recur(nestLevel, seq)

          case GetVar(idx) => throw new Exception("変数取得してどうすんの？")

          case Nop =>
            incPC()
            recur(nestLevel, seq)

          case UpdateVar(idx, value) =>
            bullet.vars(idx) += ex(value)
            incPC()
            recur(nestLevel, seq)

          case SetDirection(dir, param) => param match {
            case 'absolute =>
              bullet.angle = Angle( ex(dir) )
            case 'aim =>
              bullet.angle = Angle( runner.getAimToShip(bullet) )
            case 'relative =>
              bullet.angle = Angle( ex(dir) )
          }; incPC(); recur(nestLevel, seq)

          case SetSpeed(spd, param) => param match {
            case 'absolute =>
              bullet.speed = ex(spd)
            case 'relative =>
              bullet.speed += ex(spd)
          }; incPC(); recur(nestLevel, seq)

        }

      } else if (nestLevel != 0) {
        //println("owatteru")
        bullet.lc(nestLevel) match {
          case 1 => // 回数分が終了していればループを終了
            bullet.lc(nestLevel) = -1
            bullet.pc(nestLevel) = 0
            bullet.pc(nestLevel - 1) += 1
            // また頭からたどる．親も保持した方がいいのか……
            //println("[repeat end] NestLevel:"+nestLevel)
            recur(0, topseq)
          case 0 => // 無限ループなのでpcリセットして続行
            bullet.pc(nestLevel) = 0
            recur(nestLevel, seq)
          case n => // ループカウンタ減らしてpcリセットして続行
            //println("dec-LC :"+(bullet.lc(nestLevel))+"["+nestLevel+"]")
            bullet.lc(nestLevel) -= 1
            bullet.pc(nestLevel) = 0
            recur(nestLevel, seq)
        }
      } else {
        BasicBehaivor.run(delta)(bullet)
      }
    }
    
    recur(0, topseq) // 頭からたどる
  }

  def evaluate(seq: Array[Op], op: Op)(implicit bullet: Bullet) = {
    //def incPC() = bullet.pc += 1
  }
  
}
