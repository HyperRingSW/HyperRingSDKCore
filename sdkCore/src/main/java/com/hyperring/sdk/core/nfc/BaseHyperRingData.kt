package com.hyperring.sdk.core.nfc
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.util.Log
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class BaseHyperRingData(tag: Tag) {
    var id: Long? = null
        get() {
            return field
        }
        set(value) {
            field = value
        }

    /// todo JsonStringData
    var data: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }


    init {
        initData(tag)
    }

    private fun initData(tag: Tag) {
        val ndef = HyperRingTag.getNDEF(tag)
        if (ndef != null) {
            try{
                Log.d("HyperRing", "NDEF ndef.isConnected: ${ndef.isConnected}")
                ndef.connect()
                Log.d("HyperRing", "ndef.tag: ${ndef.tag}")
                Log.d("HyperRing", "ndef.tag: ${ndef.maxSize}")
                val msg: NdefMessage = ndef.ndefMessage
                HyperRingNFC.log("msg: ${msg.records}")
                if(msg.records != null) {
                    var hrId: Long? = null
                    var jsonData: String = emptyJsonString()

                    msg.records?.forEach {
                        val payload = String(it.payload, StandardCharsets.UTF_8)
                        if(it.tnf == NdefRecord.TNF_UNKNOWN) {
                            jsonData = fromJsonString(payload)
                        }

                    }
                    //todo hrId demo
                    hrId = 99
                    id = hrId
                    data = jsonData
                } else {
                    HyperRingNFC.log("no records")
                }
            } catch (e : Exception) {
                Log.d("HyperRing", "ndef err:${e.toString()}")
            } finally {
                ndef.close()
            }
        } else {
            HyperRingNFC.log("ndef is null")
        }
    }

    private fun fromJsonString(payload: String): String {
        Log.d("HyperRingData", "fromJsonString: payload: ${payload}")
        val model: BaseHyperRingData = gson.fromJson(payload, BaseHyperRingData::class.java)
        Log.d("HyperRingData", "fromJsonString: data: ${model.data}")
        return model.data
    }

    companion object {
        private val gson: Gson = Gson()

        fun toJsonString(): String {
//            // If is not HyperRingTag, return Empty JsonString
//            if(!HyperRingData.isHyperRingTag()) {
//                return emptyJsonString()
//            }
            // todo
//        gson.toJson("{\"hyperRingTagId\":\"${null}\"}")?:""
//        return hyperRingData.emptyJsonString()
            return emptyJsonString()
        }

        fun emptyJsonString(): String {
//            return gson.toJson("{\"hyperRingTagId\":\"${null}\"}")?:""
            return "{\"id\":null, \"data\": null}"
//            Log.d("HyperRingData", "emptyJsonString: ${gson.toJson(this, BaseHyperRingData::class.java)}")
//            return gson.toJson(this, BaseHyperRingData::class.java)
        }
    }
}
