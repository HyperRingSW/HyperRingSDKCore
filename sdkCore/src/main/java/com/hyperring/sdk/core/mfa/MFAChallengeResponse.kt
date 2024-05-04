package com.hyperring.sdk.core.mfa

import android.nfc.NdefMessage
import android.nfc.Tag
import com.hyperring.sdk.core.nfc.HyperRingDataInterface
import com.hyperring.sdk.core.nfc.IdData

class MFAChallengeResponse(override var id: Long?, override var data: String?) : HyperRingDataInterface {
    override fun initData(tag: Tag?) {
        TODO("Not yet implemented")
    }

    override fun ndefMessageBody(): NdefMessage {
        TODO("Not yet implemented")
    }

    override fun fromJsonString(payload: String): IdData {
        TODO("Not yet implemented")
    }

}