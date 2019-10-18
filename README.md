# KCiphertext

*Kotlin string obfuscation via annotations and property delegates.*



There are two different components of KCiphertext: `@Ciphertext(String)` and `@CiphertextProperty(ByteArray)`.

The `@Ciphertext()` annotation is processed at compile-time and kotlin code is generated via Kotlin Poet. Simply put the unencrypted string in the value parameter of the annotation like so:

```kotlin
@Ciphertext("YOUR_SECRET_STRING")
lateinit var myAPIKey: String

// Before using you must call ciphertext() in the class that contains the property.
// eg. onCreate()
override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
  		// This will handle decryption of both static and instance properties
      ciphertext()
  		// Now do something with myAPIKey
    	Log.i("Example", myAPIKey)
}
```

The second component of KCiphertext is a runtime annotation `@CiphertextProperty`. It takes a ByteArray that should be the created from an encrypted string. You can then use property delegates to automatically decrypt and convert the by extending the `CiphertextDelegate` class or provide your own decryptionBlock lambda.

```kotlin
// Provide the encrypted byte array
@CiphertextProperty([74, 74, 75])
val mySecretProperty: String by CiphertextDelegate({ byteArray ->
  	// Handle decryption of byte array
  	val list = it.asList()
    // Shift each byte by 1 to reverse our encryption of subtracting by 1
    val shiftedList = list.map { byte -> (byte + 1).toByte() }

  	return String(shiftedList.toByteArray())
})


override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // No need to call any other methods just use the property normally
  			Log.i("Example", mySecretProperty)
}
```

The bytecode contains encrypted byte arrays instead of plain strings. This makes it more difficult for others to reverse-engineer your software.

### Not using KCiphertext:

```java
const-string v0, "this_API_KEY_is_not_encrypted_1234asdf"
```

### Using KCiphertext:

```java
.array-data 1
        0x42t
        0x51t
        0x4at
        0x60t
        0x4ct
        0x46t
        0x5at
        0x60t
        0x49t
        0x46t
        0x53t
        0x46t
        0x60t
        0x32t
        0x33t
        0x34t
        0x35t
        0x36t
        0x37t
        0x38t
        0x39t
        0x3at
        0x60t
        0x62t
        0x64t
        0x63t
        0x65t
        0x66t
        0x67t
        0x68t
        0x69t
        0x6at
        0x6bt
        0x6ct
        0x6dt
        0x6et
        0x6ft
        0x70t
        0x71t
    .end array-data
```

