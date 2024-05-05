package com.hyperring.sdk.core.data
import android.nfc.NdefMessage
import android.nfc.Tag
import android.util.Log

/**
 * Default HyperRing Data Interface
 */
interface HyperRingDataInterface {
    var id: Long?
    var data : String?

    fun initData(tag: Tag?)
    fun encrypt(data: Any?) : ByteArray {
        throw OverrideException()
    }

    fun decrypt(data: String?) :Any {
        throw OverrideException()
    }
    class OverrideException: Exception("Needs Overriding")
}

/**
 * Base Data Format
 * Long type id, String type data
 */
data class IdData(val id: Long?, val data: String?) {
    init {
        Log.d("HyperRingData", "IdData: ($id, $data)")
    }
}
