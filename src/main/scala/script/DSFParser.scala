package com.github.whelmaze.bulletf
package script

import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.combinator._

object DSFParser extends StandardTokenParsers {
  import DefinitionFinder._

  lexical.delimiters ++= List("(",")","{","}","+","-","*","/","=","$",".",",","@")
  lexical.reserved += ("repeat","action","bullet","enemy","effect")

  // (type, name, ops)
//  lazy val top: Parser[Seq[(Symbol, Symbol, Array[Op])]] = rep1(definition)
  lazy val top: Parser[Seq[(Symbol, Array[Op])]] = rep1(definition)
  
  lazy val types: Parser[Symbol] = ("action" | "bullet" | "enemy" | "effect") ^^ { case s => Symbol(s) }
  
//  lazy val definition: Parser[(Symbol, Symbol, Array[Op])] =
lazy val definition: Parser[(Symbol, Array[Op])] =
//    types ~ ident ~ "(" ~ repsep(syms, ",") ~ ")" ~ "{" ~ opsec <~ "}" ^^ {
//    ident ~ "(" ~ repsep(syms, ",") ~ ")" ~ "{" ~ opsec <~ "}" ^^ {
    ident ~ "{" ~ opsec <~ "}" ^^ {
//      case t ~ s ~ _ ~ args  ~ opsec => (t, Symbol(s), opsec.toArray)
      case s ~ _ ~ opsec => (Symbol(s), opsec.toArray)
    }

  lazy val syms: Parser[Symbol] = ident ^^ { case s => Symbol(s) }
  
  // lazy val action: Parser[(Symbol, Array[Op])] = 
  //   ident ~ "{" ~ opsec <~ "}" ^^ { case s ~ _ ~ opsec => (Symbol(s), opsec.toArray) }

  lazy val opsec: Parser[Seq[Op]] = rep1(op)

  lazy val op: Parser[Op] =  bind | func | repeat | update

  lazy val repeat: Parser[Op] = "repeat" ~> numericLit ~ "{" ~ opsec <~ "}" ^^ {
    case time ~ _ ~ opsec => Repeat(time.toInt, opsec.toArray)
  }

  lazy val func: Parser[Op] = ident ~ "(" ~ repsep(args, ",") <~ ")" ^^ {
    case name ~ _ ~ args => findFunction(name, args)
  }

  lazy val args: Parser[Container] = value | ident ^^ { s => StrVar(s) }// | calculable
  
//  lazy val calculable: Parser[Expr] =
//    "[" ~> rep1(elem("all", true)) <~ "]" ^^ { case e => CalcParser.calc(e) }
    
  lazy val bind: Parser[Op] = "$" ~> numericLit ~ "=" ~ value ^^ {
    case idx ~ _ ~ value => SetVar(idx.toInt, value)
  }

  lazy val update: Parser[Op] = "$" ~> numericLit ~ ("+" | "-") ~ "=" ~ value ^^ {
    case idx ~ s ~ d ~ value => UpdateVar(idx.toInt, if (s == "+") value else Negate(value))
  }
  
  lazy val value: Parser[Container] = {
    double ^^ { d => DVar(d) } |
    int ^^ { i => DVar(i.toDouble) } |
    variable |
    spvalue
  }

  lazy val spvalue: Parser[Container] =
    "@" ~> ident ~ "(" ~ repsep(args, ",") <~ ")" ^^ {
      case name ~ _ ~ args => findSpecialValue(name, args)
    } |
    "@" ~> ident ^^ {
      case name => findSpecialValue(name, Seq[Container]())
    }

  lazy val int: Parser[Int] = numericLit ^^ { case i => i.toInt }

  lazy val double: Parser[Double] = numericLit ~ "." ~ numericLit ^^ {
    case bef ~ _ ~ aft => (bef+"."+aft).toDouble
  }
  
  lazy val variable: Parser[Container] =
    "$" ~> numericLit ^^ { case i => EVar(GetVar(i.toInt)) }
  
  def parse(source: String): Seq[(Symbol, Array[Op])] = {
    top(new lexical.Scanner(source)) match {
      case Success(behaivors, _) =>
        behaivors foreach { case (n, ops) => println(n+":"+ops.deep.toString) }
        behaivors
      case Failure(msg, d) => println(msg); println(d.pos.longString); sys.error("")
      case Error(msg, _) => println(msg); sys.error("")
    }
  }

  // def main(args: Array[String]) = {    
  //   parse( scala.io.Source.fromFile("script/loadtest.dsf").mkString("") )
  //   parse( "@rnd(23,34)" )
  //   ()
  // }
  
}
