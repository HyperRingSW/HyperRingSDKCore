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

    fun encrypt(source: Any?) : ByteArray

    fun decrypt(source: String?) :Any

    companion object {
        fun emptyJsonString(): String {
            return "{\"id\":null, \"data\": null}"
        }
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
