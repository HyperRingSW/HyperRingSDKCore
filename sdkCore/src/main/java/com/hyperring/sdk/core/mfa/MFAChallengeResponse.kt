package com.hyperring.sdk.core.mfa

import android.nfc.Tag
import com.hyperring.sdk.core.data.HyperRingDataMFAInterface

class MFAChallengeResponse(override var id: Long?, override var data: String?, override var isSuccess: Boolean?) : HyperRingDataMFAInterface {
    override fun encrypt(source: Any?): ByteArray {
        TODO("JWT")
    }

    override fun decrypt(source: String?): Any {
        TODO("JWT")
    }
}