package com.hyperring.sdk.core.data

/**
 * Default HyperRing Data Interface
 */
interface HyperRingDataMFAInterface : HyperRingDataInterface {
    var isSuccess: Boolean?

    override var id: Long?
    override var data : String?

    override fun encrypt(source: Any?) : ByteArray

    override fun decrypt(source: String?) :Any

    fun challenge(targetData: HyperRingDataInterface): MFAChallengeResponse
}