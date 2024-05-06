package com.hyperring.core
import android.nfc.Tag
import android.util.Base64
import android.util.Log
import com.hyperring.sdk.core.data.HyperRingDataInterface
import com.hyperring.sdk.core.data.HyperRingDataMFAInterface
import com.hyperring.sdk.core.data.HyperRingDataNFCInterface
import com.hyperring.sdk.core.data.MFAChallengeResponse
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class DemoMFAData(tag: Tag?) : HyperRingDataMFAInterface, HyperRingDataNFCInterface {
    override var id: Long? = null
    override var data: String? = ""
    override var isSuccess: Boolean? = null
    constructor(id: Long, data: String, isSuccess: Boolean?) : this(null) {
        this.id = id
        this.data = data
        this.isSuccess = isSuccess
    }

    override fun encrypt(source: Any?): ByteArray {
        if(source is String) {
            val iv = IvParameterSpec((DemoMFAData.DEMO_KEY).toByteArray())
            val keySpec = SecretKeySpec(DemoMFAData.DEMO_KEY.toByteArray(), "AES")    /// 키
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")     //싸이퍼
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)       // 암호화/복호화 모드
            val crypted = cipher.doFinal(source.toByteArray())
            val encodedByte = Base64.encode(crypted, Base64.DEFAULT)
            Log.d("DemoData","encrypted: ${crypted}, ${encodedByte}")
            data = String(encodedByte)
            return encodedByte
        }
        return "".toByteArray()
    }

    override fun decrypt(source: String?): String {
        if(source == null) {
            throw DemoNFCData.DecryptFailure()
        }
        var decodedByte: ByteArray = Base64.decode(source, Base64.DEFAULT)
        val iv = IvParameterSpec(DemoMFAData.DEMO_KEY.toByteArray())
        val keySpec = SecretKeySpec(DemoMFAData.DEMO_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val output = cipher.doFinal(decodedByte)
        return String(output)
    }

    override fun challenge(targetData: HyperRingDataInterface): MFAChallengeResponse {
        return if(decrypt(data) == decrypt(targetData.data)) {
            MFAChallengeResponse(targetData.id, targetData.data, true)
        } else {
            MFAChallengeResponse(targetData.id, targetData.data, false)
        }
    }

    companion object {
        val DEMO_KEY = "DEMODEMODEMODEMO"

        fun createData(id: Long, name: String): DemoMFAData {
            var data = "{\"id\":$id,\"data\":\"{\\\"name\\\":\\\"$name\\\"}\"}"
            return DemoMFAData(id, data, null)
        }

    }
}

