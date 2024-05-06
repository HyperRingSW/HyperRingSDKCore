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
    override fun encrypt(source: Any?) : ByteArray

    override fun decrypt(source: String?) :Any
}
