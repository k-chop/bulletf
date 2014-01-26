package bulletf.script

import scala.util.parsing.combinator._

// 四則演算．変数は実行時に取得するのでここで計算はできない．
// 簡約できる所は出来るはずだけど，とりあえず後回しでそのままExprを返す
object CalcParser extends JavaTokenParsers {
  sealed trait Expr
  case class Variable(value: GetVar) extends Expr
  case class Number(value: Double) extends Expr
  case class UnaryOp(operator: String, arg: Expr) extends Expr
  case class BinaryOp(operator: String, left: Expr, right: Expr) extends Expr

  def expr: Parser[Expr] = {
    (term ~ "+" ~ term) ^^ { case lhs ~ plus ~ rhs => BinaryOp("+", lhs, rhs) } |
    (term ~ "-" ~ term) ^^ { case lhs ~ minus ~ rhs => BinaryOp("-", lhs, rhs) } |
    term
  }

  def term: Parser[Expr] = {
    (factor ~ "*" ~ factor) ^^ { case lhs ~ times ~ rhs => BinaryOp("*", lhs, rhs) } |
    (factor ~ "/" ~ factor) ^^ { case lhs ~ div ~ rhs => BinaryOp("/", lhs, rhs) } |
    (factor ~ "^" ~ factor) ^^ { case lhs ~ exp ~ rhs => BinaryOp("^", lhs, rhs) } |
    factor
  }
    
  def factor : Parser[Expr] = {
    "(" ~> expr <~ ")" |
    floatingPointNumber ^^ {x => Number(x.toDouble) } |
    "$" ~> decimalNumber ^^ {x => Variable(GetVar(x.toInt)) }
  }

  def calc(text: String) = parseAll(expr, text) match {
    case Success(result, _) => result
    case e => throw new Exception
  }

  def eval(expr: Expr) = {}
}
