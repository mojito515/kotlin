// !WITH_NEW_INFERENCE
//EA-35646
package a

class MyClass1 {
    public operator fun unaryPlus() {}
}

fun main(arg: MyClass1) {
    arg<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!><!NI;DEBUG_INFO_UNRESOLVED_WITH_TARGET!>+<!><!><!SYNTAX!><!>
}
