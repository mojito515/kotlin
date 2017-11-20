// !WITH_NEW_INFERENCE
package f

fun <T> f(<!UNUSED_PARAMETER!>i<!>: Int, <!UNUSED_PARAMETER!>c<!>: Collection<T>): List<T> {throw Exception()}
fun <T> f(<!UNUSED_PARAMETER!>a<!>: Any, <!UNUSED_PARAMETER!>l<!>: List<T>): Collection<T> {throw Exception()}

fun <T> test(<!UNUSED_PARAMETER!>l<!>: List<T>) {
    <!OI;CANNOT_COMPLETE_RESOLVE!><!NI;OVERLOAD_RESOLUTION_AMBIGUITY!>f<!><!>(1, <!OI;TYPE_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>emptyList<!>())
}

fun <T> emptyList(): List<T> {throw Exception()}
