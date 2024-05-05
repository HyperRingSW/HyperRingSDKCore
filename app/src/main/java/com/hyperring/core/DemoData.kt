package com.hyperring.core
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.util.Log
import com.hyperring.sdk.core.data.HyperRingDataNFCInterface
import com.hyperring.sdk.core.nfc.HyperRingNFC
import com.hyperring.sdk.core.nfc.HyperRingTag
import com.hyperring.sdk.core.data.IdData
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class DemoData(tag: Tag?) : HyperRingDataNFCInterface {
    override var id: Long? = null
    override var data: String? = ""
    constructor(id: Long, data: String) : this(null) {
        this.id = id
        this.data = data
    }

    init {
        this.initData(tag)
    }

    override fun initData(tag: Tag?) {
        if(tag == null) {
            return
        }
        val ndef = HyperRingTag.getNDEF(tag)
        if (ndef != null) {
            try{
                ndef.connect()
                Log.d("HyperRing", "Demo ndef.tag: ${ndef.tag}")
                Log.d("HyperRing", "Demo ndef.tag: ${ndef.maxSize}")
                val msg: NdefMessage = ndef.ndefMessage
                HyperRingNFC.logD("Demo msg: ${msg.records}")
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
                    HyperRingNFC.logD("Demo no records")
                }
            } catch (e : Exception) {
                Log.d("HyperRing", "Demo ndef err:${e}")
            } finally {
                ndef.close()
            }
        } else {
            HyperRingNFC.logD("Demo ndef is null")
        }
    }

    override fun encrypt(data: Any?): ByteArray {
        // todo update it
        return data.toString().toByteArray()
    }

    override fun decrypt(data: String?): Any {
        // todo update it
        return data.toString()
    }

    override fun ndefMessageBody(): NdefMessage {
        Log.d("HyperRingData", "Demo ndefMessage")
        return NdefMessage(
            NdefRecord(
                NdefRecord.TNF_UNKNOWN,
                null,
                null,
                encryptData())
        )
    }

    private fun encryptData(): ByteArray {
        return encrypt(data)
    }

    override fun fromJsonString(payload: String): IdData {
        var id : Long? = null
        var name : String? = null
        Log.d("HyperRingData", "Demo fromJsonString: payload: ${payload}")
        val jsonObject = JSONObject(payload)
        id = jsonObject.getLong("id")
        var dataJson = jsonObject.getString("data")
        val dataJsonObject = JSONObject(dataJson)
        name = dataJsonObject.getString("name")

        return IdData(id, name)
    }

    companion object {
        fun createData(id: Long, name: String): DemoData {
            var data = "{\"id\":$id,\"data\":\"{\\\"name\\\":\\\"$name\\\"}\"}"
            return DemoData(id, data)
        }

        fun emptyJsonString(): String {
            return "{\"id\":10,\"data\":\"{\\\"name\\\":\\\"John Doe\\\"}\"}"
        }
    }

}

