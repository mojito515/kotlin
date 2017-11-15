// !LANGUAGE: +CallsInPlaceEffect
// WITH_RUNTIME

fun box(): String {
    val x: Int
    run {
        x = 42
    }
    return if (x.inc() == 43) "OK" else "Fail: ${x.inc()}"
}