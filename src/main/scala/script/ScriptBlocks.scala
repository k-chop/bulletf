package com.github.whelmaze.bulletf.script

// enum
sealed trait ScriptType
case object TypeEnemy extends ScriptType
case object TypeEmitter extends ScriptType
case object TypeBullet extends ScriptType
case object TypeEffect extends ScriptType
case object TypeMove extends ScriptType
case object TypeStage extends ScriptType

object ScriptBlocks {
  def empty: ScriptBlocks = new ScriptBlocks('main, TypeStage, null, Map.empty[Symbol, Array[Op]])
}

class ScriptBlocks(val name: Symbol, val types: ScriptType, val dataBlock: Any, otherBlocks: Map[Symbol, Array[Op]]) {

  val initBlock: Array[Op] = otherBlocks.get('init).getOrElse(Array(Nop))
  val runBlock: Array[Op] = otherBlocks.get('run).getOrElse(Array(Nop))
  val dieBlock: Array[Op] = otherBlocks.get('die).getOrElse(Array(Nop))

  override def toString: String = {
    s"${types.toString} $name:\ndata: $dataBlock\ninit: ${initBlock.deep.toString()}\nrun: ${runBlock.deep.toString()}\ndie: ${dieBlock.deep.toString()}"
  }
}
