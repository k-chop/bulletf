package bulletf.script

// こいつらの振る舞いはScriptBehaivor#extractに追加しろよな
trait Container

case class EVar(value: GetVar) extends Container

case class DVar(value: Double) extends Container

case class StrVar(value: String) extends Container

case class RandomVar(begin: Double, end: Double) extends Container

case class Negate(in: Container) extends Container

case class StateVar(param: Symbol) extends Container

