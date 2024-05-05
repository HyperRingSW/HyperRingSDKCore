package com.hyperring.sdk.core.data
import android.nfc.NdefMessage
import android.nfc.Tag
import android.util.Log
import com.hyperring.sdk.core.mfa.HyperRingMFA
import com.hyperring.sdk.core.mfa.MFAChallengeResponse

/**
 * Default HyperRing Data Interface
 */
interface HyperRingDataMFAInterface : HyperRingDataInterface {
    override var id: Long?
    override var data : String?

    var isSuccess: Boolean?

    override fun initData(tag: Tag?)
    override fun encrypt(data: Any?) : ByteArray {
        throw HyperRingDataInterface.OverrideException()
    }

    override fun decrypt(data: String?) :Any {
        throw HyperRingDataInterface.OverrideException()
    }
}
