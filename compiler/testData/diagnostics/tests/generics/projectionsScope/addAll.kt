// !WITH_NEW_INFERENCE
// !DIAGNOSTICS: -UNUSED_PARAMETER

interface C<out T>
interface MC<T> : C<T> {
    fun addAll(x: C<T>): Boolean
    fun addAllMC(x: MC<out T>): Boolean
    fun addAllInv(x: MC<T>): Boolean
}

interface Open
class Derived : Open

fun <T> mc(): MC<T> = null!!
fun <T> c(): C<T> = null!!

fun foo(x: MC<out Open>) {
    x.addAll(<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS(C<Nothing>; MC<out Open>; MC<out Open>; public abstract fun addAll\(x: C<T>\): Boolean defined in MC)!><!NI;TYPE_MISMATCH!>x<!><!>)
    x.addAllMC(<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS(MC<Nothing>; MC<out Open>; MC<out Open>; public abstract fun addAllMC\(x: MC<out T>\): Boolean defined in MC)!><!NI;TYPE_MISMATCH!>x<!><!>)

    x.addAll(<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS(C<Nothing>; MC<Open>; MC<out Open>; public abstract fun addAll\(x: C<T>\): Boolean defined in MC)!><!NI;TYPE_MISMATCH!>mc<Open>()<!><!>)
    x.addAllMC(<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS(MC<Nothing>; MC<Open>; MC<out Open>; public abstract fun addAllMC\(x: MC<out T>\): Boolean defined in MC)!><!NI;TYPE_MISMATCH!>mc<Open>()<!><!>)

    x.addAll(<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS(C<Nothing>; MC<Derived>; MC<out Open>; public abstract fun addAll\(x: C<T>\): Boolean defined in MC)!><!NI;TYPE_MISMATCH!>mc<Derived>()<!><!>)
    x.addAllMC(<!OI;TYPE_MISMATCH_DUE_TO_TYPE_PROJECTIONS(MC<Nothing>; MC<Derived>; MC<out Open>; public abstract fun addAllMC\(x: MC<out T>\): Boolean defined in MC)!><!NI;TYPE_MISMATCH!>mc<Derived>()<!><!>)

    x.addAll(c())
    x.addAll(c<Nothing>())

    x.<!OI;MEMBER_PROJECTED_OUT!>addAllInv<!>(<!NI;TYPE_MISMATCH!>mc<Open>()<!>)
    x.addAll(<!CONSTANT_EXPECTED_TYPE_MISMATCH!>1<!>)
}
