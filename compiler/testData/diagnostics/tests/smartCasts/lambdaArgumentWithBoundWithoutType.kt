// !WITH_NEW_INFERENCE
interface Foo
interface Bar : Foo

fun foo(): Foo? = null
fun bar(): Bar? = null

fun <T : Bar> run(f: () -> T): T = f()

val foo: Foo = run {
    val x = bar()
    if (x == null) throw Exception()
    <!DEBUG_INFO_SMARTCAST!>x<!>
}

val foofoo: Foo = <!OI;TYPE_INFERENCE_UPPER_BOUND_VIOLATED!>run<!> {
    val x = foo()
    if (x == null) throw Exception()
    <!OI;DEBUG_INFO_SMARTCAST!><!NI;TYPE_MISMATCH!><!NI;DEBUG_INFO_SMARTCAST!>x<!><!><!>
}

val bar: Bar = <!NI;TYPE_MISMATCH!><!OI;TYPE_INFERENCE_CONFLICTING_SUBSTITUTIONS!>run<!> {
    val x = foo()
    if (x == null) throw Exception()
    <!TYPE_MISMATCH!><!NI;DEBUG_INFO_SMARTCAST!>x<!><!>
}<!>
