package com.baileyseymour.kciphertextannotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class CiphertextProperty(val encryptedValue: ByteArray)