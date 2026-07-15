package tools.forma.examples.android.testutils.common.testutil

fun assertPositive(value: Int) {
    check(value > 0) { "expected positive, got $value" }
}
