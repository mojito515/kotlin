// !WITH_NEW_INFERENCE
fun foo(<!UNUSED_PARAMETER!>f<!>: String.() -> Int) {}
val test = foo(<!OI;TYPE_MISMATCH(String.\(\) -> Int; \(\) -> [ERROR : Error function type])!>fun () = <!UNRESOLVED_REFERENCE!>length<!><!>)