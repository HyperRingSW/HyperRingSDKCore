package com.hyperring.sdk.core.data
import android.nfc.NdefMessage
import android.nfc.Tag
import android.util.Log

/**
 * Default HyperRing Data Interface
 */
interface HyperRingDataNFCInterface : HyperRingDataInterface{
    override var id: Long?
    override var data : String?

    override fun initData(tag: Tag?)
    override fun encrypt(data: Any?) : ByteArray {
        throw HyperRingDataInterface.OverrideException()
    }

    override fun decrypt(data: String?) :Any {
        throw HyperRingDataInterface.OverrideException()
    }

    fun ndefMessageBody(): NdefMessage

    fun fromJsonString(payload: String): IdData
}
