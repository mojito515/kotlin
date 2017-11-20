// !WITH_NEW_INFERENCE
fun <E : String?, T : ((CharSequence) -> Unit)?> foo(x: E, y: T) {
    if (x != null) {
        <!OI;UNSAFE_IMPLICIT_INVOKE_CALL!><!NI;UNSAFE_CALL!>y<!><!>(<!OI;DEBUG_INFO_SMARTCAST!><!NI;TYPE_MISMATCH!><!NI;DEBUG_INFO_SMARTCAST!>x<!><!><!>)
    }

    if (y != null) {
        <!OI;DEBUG_INFO_SMARTCAST!><!NI;UNSAFE_CALL!>y<!><!>(<!TYPE_MISMATCH!>x<!>)
    }

    if (x != null && y != null) {
        <!OI;DEBUG_INFO_SMARTCAST!><!NI;UNSAFE_CALL!>y<!><!>(<!OI;DEBUG_INFO_SMARTCAST!><!NI;TYPE_MISMATCH!><!NI;DEBUG_INFO_SMARTCAST!>x<!><!><!>)
    }
}