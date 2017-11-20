// !WITH_NEW_INFERENCE
class ExplicitAccessorForAnnotation {
    val tt: String? = "good"
        get

    fun foo(): String {
        if (tt is String) {
            return <!OI;SMARTCAST_IMPOSSIBLE!><!NI;TYPE_MISMATCH!><!NI;SMARTCAST_IMPOSSIBLE!>tt<!><!><!>
        }
        return ""
    }
}