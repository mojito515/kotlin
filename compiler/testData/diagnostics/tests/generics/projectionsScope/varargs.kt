// !WITH_NEW_INFERENCE
// !DIAGNOSTICS: -UNUSED_PARAMETER

class A<T> {
    fun foo(vararg x: T) {}
}

fun test(a: A<out CharSequence>, y: Array<out CharSequence>) {
    a.<!OI;MEMBER_PROJECTED_OUT!>foo<!>(<!NI;TYPE_MISMATCH!>""<!>, <!NI;TYPE_MISMATCH!>""<!>, <!NI;TYPE_MISMATCH!>""<!>)
    a.foo(*<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS!><!NI;TYPE_MISMATCH!>y<!><!>)
    // TODO: TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS probably redundant
    a.<!OI;MEMBER_PROJECTED_OUT!>foo<!>(*<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS!><!NI;TYPE_MISMATCH!>y<!><!>, <!NI;TYPE_MISMATCH!>""<!>)
}
