package com.hyperring.sdk.core.mfa

import android.nfc.Tag
import com.hyperring.sdk.core.data.HyperRingDataMFAInterface

class MFAChallengeResponse(override var id: Long?, override var data: String?, override var isSuccess: Boolean?) :
    HyperRingDataMFAInterface {
    override fun initData(tag: Tag?) {
//        TODO("Not yet implemented")
    }
}