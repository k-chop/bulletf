package bulletf



trait Behavior {

  // 通常毎フレーム実行されるブロック
  def run(unit: ScriptControlled)
  // 1度目のupdateの前に実行される初期化ブロック
  def init(unit: ScriptControlled) {}
  // 消える/死ぬ時に実行されるブロック
  def die(unit: ScriptControlled) {}

}
