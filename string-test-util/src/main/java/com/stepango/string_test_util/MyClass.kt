package com.stepango.string_test_util

fun String.validateLength(length: Long) {
    if (this.length > length) throw IllegalArgumentException("String is too long, maximum length is $length")
}