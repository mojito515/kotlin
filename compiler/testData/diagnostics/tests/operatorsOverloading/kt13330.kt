// !WITH_NEW_INFERENCE
//KT-13330 AssertionError: Illegal resolved call to variable with invoke

fun foo(exec: (String.() -> Unit)?) = ""<!NI;UNSAFE_CALL!>.<!><!OI;UNSAFE_IMPLICIT_INVOKE_CALL!><!NI;WRONG_NUMBER_OF_TYPE_ARGUMENTS!>exec<!><!><!OI;WRONG_NUMBER_OF_TYPE_ARGUMENTS!><<!UNRESOLVED_REFERENCE!>caret<!>><!>() // <caret> is test data tag here