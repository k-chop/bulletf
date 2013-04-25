package com.github.whelmaze.bulletf
package script

import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.combinator._

object DSFParser extends StandardTokenParsers {
  import DefinitionFinder._

  lexical.delimiters ++= List("(",")","{","}","+","-","*","/","=","$",".",",","@")
  lexical.reserved += ("repeat","action","bullet","enemy","effect", "emitter")

  // (type, name, ops)
//  lazy val top: Parser[Seq[(Symbol, Symbol, Array[Op])]] = rep1(definition)
  lazy val top: Parser[Seq[(Symbol, Array[Op])]] = rep1(definition)
  
  lazy val types: Parser[Symbol] = ("action" | "bullet" | "enemy" | "effect" | "emitter") ^^ { case s => Symbol(s) }
  
//  lazy val definition: Parser[(Symbol, Symbol, Array[Op])] =
  lazy val definition: Parser[(Symbol, Array[Op])] =
//    types ~ ident ~ "(" ~ repsep(syms, ",") ~ ")" ~ "{" ~ opsec <~ "}" ^^ {
//    ident ~ "(" ~ repsep(syms, ",") ~ ")" ~ "{" ~ opsec <~ "}" ^^ {
    types ~> ident ~ "{" ~ opseq <~ "}" ^^ {
//      case t ~ s ~ _ ~ args  ~ opsec => (t, Symbol(s), opsec.toArray)
      case s ~ _ ~ ops => (Symbol(s), ops.toArray)
    }

  lazy val syms: Parser[Symbol] = ident ^^ { case s => Symbol(s) }
  
  // lazy val action: Parser[(Symbol, Array[Op])] = 
  //   ident ~ "{" ~ opsec <~ "}" ^^ { case s ~ _ ~ opsec => (Symbol(s), opsec.toArray) }

  lazy val opseq: Parser[Seq[Op]] = rep1(op)

  lazy val op: Parser[Op] =  bind | func | repeat | update

  lazy val repeat: Parser[Op] = "repeat" ~> numericLit ~ "{" ~ opseq <~ "}" ^^ {
    case time ~ _ ~ opsec => Repeat(time.toInt, opsec.toArray)
  }

  lazy val func: Parser[Op] = ident ~ "(" ~ repsep(args, ",") <~ ")" ^^ {
    case name ~ _ ~ as => findFunction(name, as)
  }

  lazy val args: Parser[Container] = value | ident ^^ { s => StrVar(s) }// | calculable
  
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
    "-" ~> numericLit ^^ { case i => -(i.toInt) } |
    numericLit ^^ { case i => i.toInt }

  lazy val double: Parser[Double] =
    "-" ~> numericLit ~ "." ~ numericLit ^^ {
      case bef ~ _ ~ aft => -((bef+"."+aft).toDouble)
    } | numericLit ~ "." ~ numericLit ^^ {
    case bef ~ _ ~ aft => (bef+"."+aft).toDouble
  }
  
  lazy val variable: Parser[Container] =
    "$" ~> numericLit ^^ { case i => EVar(GetVar(i.toInt)) }

  def parse(_source: String): Seq[(Symbol, Array[Op])] = {
    val commentReg = """//.*""".r
    val source = commentReg.replaceAllIn(_source, "") // 一行コメントを行末まで削除

    println(source)
    top(new lexical.Scanner(source)) match {
      case Success(behaivors, _) =>
        behaivors foreach { case (n, ops) => println(n+":"+ops.deep.toString) }
        behaivors
      case Failure(msg, d) => {
        println("parse failure.")
        println(msg)
        println(d.pos.longString)
        Seq(('main, Array(Nop)))
      }
      case Error(msg, _) => {
        println("parse error.")
        println(msg)
        Seq(('main, Array(Nop)))
      }
    }
  }

  // def main(args: Array[String]) = {    
  //   parse( scala.io.Source.fromFile("script/loadtest.dsf").mkString("") )
  //   parse( "@rnd(23,34)" )
  //   ()
  // }
  
}
