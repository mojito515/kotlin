// !WITH_NEW_INFERENCE
// !DIAGNOSTICS: -UNUSED_PARAMETER

import kotlin.reflect.KProperty

class A {
    val c: Int by <!OI;DELEGATE_SPECIAL_FUNCTION_NONE_APPLICABLE!><!NI;DELEGATE_SPECIAL_FUNCTION_AMBIGUITY!>Delegate()<!><!>
}

class Delegate {
    fun getValue(t: Int, p: KProperty<*>): Int {
        return 1
    }

    fun getValue(t: String, p: KProperty<*>): Int {
        return 1
    }
}
