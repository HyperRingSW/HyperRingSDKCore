package com.hyperring.core
import android.nfc.Tag
import com.hyperring.sdk.core.data.HyperRingDataMFAInterface
import com.hyperring.sdk.core.data.HyperRingDataNFCInterface

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
        // todo update it
        return data.toString().toByteArray()
    }

    override fun decrypt(source: String?): Any {
        // todo update it
        return data!!
    }

//    override fun fromJsonString(payload: String): IdData {
//        var id : Long? = null
//        var name : String? = null
//        Log.d("HyperRingData", "Demo fromJsonString: payload: ${payload}")
//        val jsonObject = JSONObject(payload)
//        id = jsonObject.getLong("id")
//        var dataJson = jsonObject.getString("data")
//        val dataJsonObject = JSONObject(dataJson)
//        name = dataJsonObject.getString("name")
//        return IdData(id, name)
//    }

    companion object {
        fun createData(id: Long, name: String): DemoMFAData {
            var data = "{\"id\":$id,\"data\":\"{\\\"name\\\":\\\"$name\\\"}\"}"
            return DemoMFAData(id, data, null)
        }

//        fun emptyJsonString(): String {
//            return "{\"id\":10,\"data\":\"{\\\"name\\\":\\\"John Doe\\\"}\"}"
//        }
    }

}

