// !WITH_NEW_INFERENCE
package bar

fun main(args : Array<String>) {
    class Some

    <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!><!NO_COMPANION_OBJECT!>Some<!>[<!SYNTAX!><!>]<!> <!OI;DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!><!NI;UNRESOLVED_REFERENCE!>names<!><!> <!DEBUG_INFO_MISSING_UNRESOLVED!><!SYNTAX!>=<!> ["ads"]<!>
}