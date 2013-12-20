package bulletf.script

import scala.util.parsing.combinator.syntactical._
import scala.collection.mutable

object DSFParser extends StandardTokenParsers {
  import DefinitionFinder._

  lexical.delimiters ++= List("(",")","{","}","+","-","*","/","=","$",".",",","@",":=","|")
  lexical.reserved += (
    "repeat",
    "move","stage","bullet","enemy","effect","emitter",
    "data","init","run","die",
    "nop"
    )

  // (type, name, ops)
//  lazy val top: Parser[Seq[(Symbol, Symbol, Array[Op])]] = rep1(definition)
  lazy val topLevel: Parser[Seq[(Symbol, String, Map[Symbol, Array[Op]])]] = rep1(definition)
  
  lazy val types: Parser[Symbol] =
    ("stage" | "move" | "bullet" | "enemy" | "effect" | "emitter") ^^ {
      case s => Symbol(s)
    }
  
  lazy val definition: Parser[(Symbol, String, Map[Symbol, Array[Op]])] = {
/*    types ~> ident ~ "{" ~ blocks <~ "}" ^^ { // 新定義
      case s ~ _ ~ bl => (Symbol(s), bl.find(_._1 == 'run).map(_._2).getOrElse(Seq.empty[Op]).toArray)
    } | types ~> ident ~ "{" ~ opseq <~ "}" ^^ { // 旧定義
      case s ~ _ ~ ops => (Symbol(s), ops.toArray)
    }*/
    newdef
  }

  lazy val newdef: Parser[(Symbol, String, Map[Symbol, Array[Op]])] = types ~ ident ~ "{" ~ blocks <~ "}" ^^ {
    case ty ~ name ~ _ ~ bls => (ty, name, bls)
  }

/*  lazy val olddef: Parser[(Symbol, Array[Op])] = types ~> ident ~ "{" ~ opseq <~ "}" ^^ {
    case s ~ _ ~ ops => (Symbol(s), ops.toArray)
  } */

  lazy val blocks: Parser[Map[Symbol, Array[Op]]] = rep1(block) ^^ {
    case bls => bls.toMap
  }

  lazy val block: Parser[(Symbol, Array[Op])] =
    blockName ~ "{" ~ opseq <~ "}" ^^ {
      case bn ~ _ ~ ops => (bn, ops.toArray)
    }

  lazy val blockName = ("data"| "init" | "run" | "die") ^^ { case s => Symbol(s) }

  lazy val syms: Parser[Symbol] = ident ^^ { case s => Symbol(s) }
  
  lazy val opseq: Parser[Seq[Op]] = rep1(op)

  lazy val op: Parser[Op] =  bind | func | repeat | update | nop | dataset

  lazy val dataset: Parser[Op] = ident ~ ":=" ~ (stringLit | int | double) ^^ {
    case i ~ _ ~ str => DataSet(Symbol(i), str.toString)
  }

  lazy val nop: Parser[Op] = "nop" ^^ { case _ => Nop }

  lazy val repeat: Parser[Op] = "repeat" ~> numericLit ~ "{" ~ opseq <~ "}" ^^ {
    case time ~ _ ~ opsec => Repeat(time.toInt, opsec.toArray)
  }

  lazy val func: Parser[Op] = ident ~ "(" ~ repsep(args, "," | "|") <~ ")" ^^ {
    case name ~ _ ~ as => findFunction(name, as)
  }

  lazy val args: Parser[Container] = value | ident ^^ { s => StrVar(s) } | stringLit ^^ { s => StrVar(s) }// | calculable

//  lazy val calculable: Parser[Expr] =
//    "[" ~> rep1(elem("all", true)) <~ "]" ^^ { case e => CalcParser.calc(e) }
    
  lazy val bind: Parser[Op] = "$" ~> numericLit ~ "=" ~ value ^^ {
    case idx ~ _ ~ v => SetVar(idx.toInt, v)
  }

  lazy val update: Parser[Op] = "$" ~> numericLit ~ ("+" | "-") ~ "=" ~ value ^^ {
    case idx ~ s ~ d ~ v => UpdateVar(idx.toInt, if (s == "+") v else Negate(v))
  }
  
  lazy val value: Parser[Container] = {
    double ^^ { d => DVar(d) } |
    int ^^ { i => DVar(i.toDouble) } |
    variable |
    spvalue
  }

  lazy val spvalue: Parser[Container] =
    "@" ~> ident ~ "(" ~ repsep(args, ",") <~ ")" ^^ {
      case name ~ _ ~ as => findSpecialValue(name, as)
    } |
    "@" ~> ident ^^ {
      case name => findSpecialValue(name, Seq[Container]())
    }

  lazy val int: Parser[Int] =
    "-" ~> numericLit ^^ { case i => -i.toInt } |
    numericLit ^^ { case i => i.toInt }

  lazy val double: Parser[Double] =
    "-" ~> numericLit ~ "." ~ numericLit ^^ {
      case bef ~ _ ~ aft => -(bef + "." + aft).toDouble
    } | numericLit ~ "." ~ numericLit ^^ {
    case bef ~ _ ~ aft => (bef+"."+aft).toDouble
  }
  
  lazy val variable: Parser[Container] =
    "$" ~> numericLit ^^ { case i => EVar(GetVar(i.toInt)) }

  def parse(_source: String): Seq[(Symbol, ScriptBlocks)] = {
    val commentReg = """//.*""".r
    val source = commentReg.replaceAllIn(_source, "") // 一行コメントを行末まで削除

    //println(source)
    topLevel(new lexical.Scanner(source)) match {

      case Success(blockSet, _) =>
        blockSet map { case (typeSym, name, blockMap) =>
          // datablockがnullなのは仮
          val datablock = extractDataBlock(blockMap.get('data).getOrElse(Array(Nop)))
          val sblocks = new ScriptBlocks(Symbol(name), symToType(typeSym), datablock, blockMap)
          println(sblocks)
          (Symbol(name), sblocks)
        }

      case Failure(msg, d) =>
        println("parse failure.")
        println(msg)
        println(d.pos.longString)
        Seq(('main, ScriptBlocks.empty))

      case Error(msg, _) =>
        println("parse error.")
        println(msg)
        Seq(('main, ScriptBlocks.empty))
    }
  }

  def symToType(s: Symbol): ScriptType = s match {
    case 'effect => TypeEffect
    case 'enemy => TypeEnemy
    case 'bullet => TypeBullet
    case 'emitter => TypeEmitter
    case 'move => TypeMove
    case 'stage => TypeStage
  }

  def extractDataBlock(ops: Array[Op]): Map[Symbol, String] = {
    val acc = mutable.HashMap.empty[Symbol, String]
    ops foreach {
      case DataSet(sym, str) => acc += (sym -> str)
      case _ => // 他は無視
    }
    acc.toMap
  }

  // def main(args: Array[String]) = {    
  //   parse( scala.io.Source.fromFile("script/loadtest.dsf").mkString("") )
  //   parse( "@rnd(23,34)" )
  //   ()
  // }
  
}
