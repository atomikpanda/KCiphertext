package com.baileyseymour.kciphertextexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.baileyseymour.kciphertextannotation.Ciphertext
import com.baileyseymour.kciphertextannotation.CiphertextDelegate
import com.baileyseymour.kciphertextannotation.CiphertextProperty
import com.baileyseymour.kciphertextannotation.ShiftCiphertextDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.jvm.internal.ReflectionFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class MainActivity : AppCompatActivity() {

//    @Ciphertext("test")
//    lateinit var someHiddenSecret: String

//    @Ciphertext("API_KEY_HERE_123456789_acbdefghijklmnop")
//    lateinit var myAPIKeyUsingCiphertext: String

    val myAPIKeyNotUsingCiphertext: String = "this_API_KEY_is_not_encrypted_1234asdf"

//    private var someNormalSecret: String = "normal_secret"
//
//    @CiphertextProperty([68, 68, 69])
//    val runtimeTest: String by ShiftCiphertextDelegate(0)

//    companion object {
//        @CiphertextProperty([74, 74, 75])
//        val STATIC_SECRET: String by ShiftCiphertextDelegate(1)
//
//        @Ciphertext("API_KEY_EXPOSED_STATIC")
//        lateinit var myStaticAPIKey: String
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.text = myAPIKeyNotUsingCiphertext
        //textView.text = myAPIKeyNotUsingCiphertext
//        textView.text = someNormalSecret
//        textView.text = someHiddenSecret
//        textView.text = STATIC_SECRET
//        textView.text = runtimeTest
    }
}

