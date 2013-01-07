package com.github.whelmaze.bulletf
package script

object DefinitionFinder {

  final val spdparams = Set("absolute", "relative")
  final val dirparams = Set("absolute", "aim", "relative")
  final val stateparams = Set("x", "y", "aim", "speed", "angle")
  
  def findSpecialValue(name: String, args: Seq[Container]): Container = (name, args) match {
    case ("rnd", Seq( DVar(fst), DVar(snd) )) =>
      if (fst < snd) RandomVar(fst, snd) else RandomVar(snd, fst)
    case ("rnd", Seq( DVar(fst)) ) =>
      RandomVar(0, fst)
    case (n, Seq()) if stateparams(n) =>
      StateVar( Symbol(n) )
    case _ => DVar(0.0)
  }

  def findFunction(name: String, args: Seq[Container]): Op = (name, args) match {
    case ("fire", Seq( StrVar(action), StrVar(kind), c: Container, d: Container )) =>
      Fire(Symbol(action), Symbol(kind), c, d)
    
    case ("wait", Seq( DVar(value) )) =>
      Wait(value.toInt)
    
    case ("spd", Seq( cont: Container, StrVar(param) )) =>
      if ( spdparams(param) ) {
        SetSpeed(cont, Symbol(param) )
      } else {
        println("%s is undefined param with `spd`, fixed to absolute." format param)
        SetSpeed(cont, 'absolute)
      }
    case ("spd", Seq( cont: Container )) =>
      SetSpeed(cont, 'absolute)
    
    case ("dir", Seq( cont: Container, StrVar(param) )) =>
      if ( dirparams(param) ) {
        SetDirection(cont, Symbol(param) )
      } else {
        println("%s is undefined param with `spd`, fixed to absolute." format param)
        SetDirection(cont, 'absolute )
      }
    case ("dir", Seq( cont: Container )) =>
      SetDirection(cont, 'absolute)
    
    case _ => Nop
  }

}
