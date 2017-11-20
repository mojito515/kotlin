// !WITH_NEW_INFERENCE
// !DIAGNOSTICS: -UNUSED_PARAMETER

import kotlin.reflect.KProperty

val c: Int by <!OI;DELEGATE_SPECIAL_FUNCTION_RETURN_TYPE_MISMATCH!><!NI;TYPE_MISMATCH!><!NI;DELEGATE_SPECIAL_FUNCTION_NONE_APPLICABLE!>Delegate()<!><!><!>

class Delegate {
  operator fun getValue(t: Any?, p: KProperty<*>): String {
    return ""
  }
}
