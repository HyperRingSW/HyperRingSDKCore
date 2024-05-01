package com.hyperring.sdk.core.nfc
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.util.Log
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

open class HyperRingData(tag: Tag?) {
    var id: Long? = null
        get() {
            return field
        }
        set(value) {
            field = value
        }

    var data: String? = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }

    constructor(id: Long, data: String) : this(null) {
        this.id = id
        this.data = data
    }
    init {
        initData(tag)
    }

    private fun initData(tag: Tag?) {
        if(tag == null) {
            return
        }
        val ndef = HyperRingTag.getNDEF(tag)
        if (ndef != null) {
            try{
                Log.d("HyperRing", "NDEF ndef.isConnected: ${ndef.isConnected}")
                ndef.connect()
                Log.d("HyperRing", "ndef.tag: ${ndef.tag}")
                Log.d("HyperRing", "ndef.tag: ${ndef.maxSize}")
                val msg: NdefMessage = ndef.ndefMessage
                HyperRingNFC.logD("msg: ${msg.records}")
                if(msg.records != null) {
                    var hrId: Long? = null
                    var jsonData: String? = emptyJsonString()
                    msg.records?.forEach {
                        val payload = String(it.payload, StandardCharsets.UTF_8)
                        if(it.tnf == NdefRecord.TNF_UNKNOWN) {
                            val idData = fromJsonString(payload)
                            hrId = idData.id
                            jsonData = idData.data
                        }
                    }
                    id = hrId
                    data = jsonData
                } else {
                    HyperRingNFC.logD("no records")
                }
            } catch (e : Exception) {
                Log.d("HyperRing", "ndef err:${e.toString()}")
            } finally {
                ndef.close()
            }
        } else {
            HyperRingNFC.logD("ndef is null")
        }
    }

    private fun fromJsonString(payload: String): IdData {
        var id : Long? = null
        var data : String? = null
        Log.d("HyperRingData", "fromJsonString: payload: ${payload}")
        try {
            val model: HyperRingData = gson.fromJson(payload, HyperRingData::class.java)
            Log.d("HyperRingData", "fromJsonString: data: ${model.data}")
            id = model.id
            data = model.data
        }catch (e: Exception) {
            Log.e("HyperRingData", "exception: ${e}")
        }
        return IdData(id, data)
    }

    open fun ndefMessage(): NdefMessage {
        return NdefMessage(
            NdefRecord(
                NdefRecord.TNF_UNKNOWN,
                null,
                null,
                encryptData()))
    }

    open fun encryptData(): ByteArray {
        Log.d("HyperRingTag","encrypt data:[$id] ${data}")
        return encrypt(data)
    }

    open fun decryptData(): Any {
        return decrypt(data)
    }

    /**
     * Must be used by overriding
     * @param data Any type
     */
    open fun encrypt(data: Any?) : ByteArray {
        return data.toString().toByteArray()
//        throw OverrideException()
    }

    /**
     * Must be used by overriding
     * @param data Any type
     */
    open fun decrypt(data: Any?) :Any {
        // todo
        return data.toString()
//        throw OverrideException()
    }

    companion object {
        private val gson: Gson = Gson()
        fun emptyJsonString(): String {
//            return gson.toJson("{\"hyperRingTagId\":\"${null}\"}")?:""
            return "{\"id\":null, \"data\": null}"
//            Log.d("HyperRingData", "emptyJsonString: ${gson.toJson(this, BaseHyperRingData::class.java)}")
//            return gson.toJson(this, BaseHyperRingData::class.java)
        }

        fun jsonStringData(map: Map<String, Any>): String {
            return gson.toJson(map)
        }

        fun createData(id: Long, dataMap: MutableMap<String, Any>): HyperRingData {
            var data = mutableMapOf<String, Any>("id" to id)
            var jsonData = ""
            try{
                data["data"] = jsonStringData(dataMap)
                jsonData = jsonStringData(data)
            } catch (e: Exception) {
                Log.e("HyperRingData", e.toString())
            }
            return HyperRingData(id, jsonData)
        }
    }
}

data class IdData(val id: Long?, val data: String?) {
    init {
        Log.d("HyperRingData", "IdData: ($id, $data)")
    }
}
