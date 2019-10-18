package com.baileyseymour.kciphertextannotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Ciphertext(val value: String)