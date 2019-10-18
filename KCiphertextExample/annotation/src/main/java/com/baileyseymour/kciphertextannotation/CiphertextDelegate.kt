package com.baileyseymour.kciphertextannotation

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class CiphertextDelegate(protected val decryptionBlock: (ByteArray) -> String) :
    ReadOnlyProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        var ciphertextProperty: CiphertextProperty? = null

        property.annotations.forEach {
            val item = it as? CiphertextProperty
            item?.let { ciphertextProperty = item }
        }

        requireNotNull(ciphertextProperty) { "To use CiphertextDelegate you must annotate the property with @CiphertextProperty." }

        return decryptionBlock(ciphertextProperty!!.encryptedValue)
    }
}

class ShiftCiphertextDelegate(shift: Int) :
    CiphertextDelegate({
        val list = it.asList()
        val shiftedList = list.map { byte -> (byte + shift).toByte() }
        String(shiftedList.toByteArray())
    })

