package com.example.keystore_method_channel_explore

import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*
import javax.crypto.Cipher.*
import javax.security.auth.x500.X500Principal

class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor,"flutter/MethodChannelDemo").setMethodCallHandler{
            call,result -> 
            val keystoreService = KeyStoreService(this, "EXAMPLE_KEYSTORE")
            if (call.method == "encript") {   
                val text : String? = call.argument("text")
                val resultData : String? = keystoreService.encryptData(text)
                result.success(resultData)
            } else if(call.method == "decript"){
                val text : String? = call.argument("text")
                val resultData : String? = keystoreService.decryptData(text)
                result.success(resultData)
            } else {
                result.notImplemented()
            }
        }
    }

    
}

class KeyStoreService(private val context: Context, private val keyStoreAlias: String) {
        private val keyStore: KeyStore

    private val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private val RSA_CIPHER = "RSA/ECB/PKCS1Padding"

    init {
        keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        if (!keyStore.containsAlias(keyStoreAlias)) this.createNewKey()
    }

    fun createNewKey() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createNewKeyM()
        } else {
            createNewKeyJ()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun createNewKeyM() {
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER)
        generator.initialize(KeyGenParameterSpec.Builder(keyStoreAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build())
        generator.generateKeyPair()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun createNewKeyJ() {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 100)
        val generator = KeyPairGenerator.getInstance("RSA", KEYSTORE_PROVIDER)
        generator.initialize(KeyPairGeneratorSpec.Builder(this.context)
            .setAlias(keyStoreAlias)
            .setSubject(X500Principal("CN=Secured Preference Store, O=Android Authority"))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.time)
            .setEndDate(end.time)
            .build())
        generator.generateKeyPair()
    }

    fun encryptData(plainStr: String?): String {
        val encryptKey = keyStore.getCertificate(keyStoreAlias).publicKey

        print("ini public key " + keyStore.getCertificate(keyStoreAlias).publicKey.encoded)

        val encodedCertKey: ByteArray = keyStore.getCertificate(keyStoreAlias).encoded
        val encodedPublicKey: ByteArray = keyStore.getCertificate(keyStoreAlias).publicKey.encoded
        val b64PublicKey: String = Base64.encodeToString(encodedPublicKey, Base64.DEFAULT)
        val b64CertKey: String = Base64.encodeToString(encodedCertKey, Base64.DEFAULT)
        val publicKeyString = """
            -----BEGIN CERTIFICATE-----
            $b64PublicKey
            -----END CERTIFICATE-----
            """.trimIndent()

        val certKeyString = """
            -----BEGIN CERTIFICATE-----
            $b64CertKey
            -----END CERTIFICATE-----
            """.trimIndent()
        println(publicKeyString)
        println(certKeyString)


        val cipher = getInstance(RSA_CIPHER)
        cipher.init(ENCRYPT_MODE, encryptKey)
        val result = cipher.doFinal(plainStr!!.toByteArray())
        print(" ini base64" + Base64.encodeToString(result, Base64.DEFAULT))
        return Base64.encodeToString(result, Base64.DEFAULT)
    }

    fun decryptData(encryptedStr: String?): String {
        val decryptKey = keyStore.getKey(keyStoreAlias, null) as PrivateKey

        println("ini private key " + decryptKey.encoded)

        val cipher = getInstance(RSA_CIPHER)
        cipher.init(DECRYPT_MODE, decryptKey)
        val result = cipher.doFinal(Base64.decode(encryptedStr!!, Base64.DEFAULT))

        return String(result)
    }
}