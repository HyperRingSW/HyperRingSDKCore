package com.hyperring.sdk.core.nfc
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets


/**
 * Default Data class
 * HyperRingDataInterface contains default functions and valiables
 */
class HyperRingData(tag: Tag?) : HyperRingDataInterface {
    override var id: Long? = null
    override var data: String? = ""
    constructor(id: Long, data: String) : this(null) {
        this.id = id
        this.data = data
    }
    init {
        this.initData(tag)
    }

    /**
     * If tag data exist. init id, data
     */
    override fun initData(tag: Tag?) {
        if(tag == null) {
            return
        }
        val ndef = HyperRingTag.getNDEF(tag)
        if (ndef != null) {
            try{
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
                Log.d("HyperRing", "ndef err:${e}")
            } finally {
                ndef.close()
            }
        } else {
            HyperRingNFC.logD("ndef is null")
        }
    }

    override fun fromJsonString(payload: String): IdData {
        var id : Long? = null
        var data : String? = null
        Log.d("HyperRingData", "fromJsonString: payload: ${payload}")

        val jsonObject = JSONObject(payload)
        id = jsonObject.getLong("id")
        var dataJson = jsonObject.getString("data")
        Log.d("HyperRingData", "data json:  ${dataJson}")
        try{
            val model: HyperRingData = gson.fromJson(payload, HyperRingData::class.java)
            data = model.data
        } catch (e: Exception) {
            Log.e("HyperRingData", "Not matched data type")
        }
        return IdData(id, data)
    }

    /**
     * Must be used by overriding
     * @param data Any type
     */
    override
    fun encrypt(data: Any?) : ByteArray {
        return data.toString().toByteArray()
    }

    /**
     * Must be used by overriding
     * @param data Any type
     */
    override fun decrypt(data: String?): Any {
        return data.toString()
    }

    override fun ndefMessageBody(): NdefMessage {
        Log.d("HyperRingData", "HRData ndefMessage")
        return NdefMessage(
            NdefRecord(
                NdefRecord.TNF_UNKNOWN,
                null,
                null,
                encrypt(data))
        )
    }

    companion object {
        private val gson: Gson = Gson()
        fun emptyJsonString(): String {
            return "{\"id\":null, \"data\": null}"
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