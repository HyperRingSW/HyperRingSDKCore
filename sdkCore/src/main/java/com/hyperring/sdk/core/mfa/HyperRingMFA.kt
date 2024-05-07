package com.hyperring.sdk.core.mfa
import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.hyperring.sdk.core.R
import com.hyperring.sdk.core.data.HyperRingDataInterface
import com.hyperring.sdk.core.data.HyperRingMFAChallengeInterface
import com.hyperring.sdk.core.data.MFAChallengeResponse
import com.hyperring.sdk.core.nfc.HyperRingNFC
import com.hyperring.sdk.core.nfc.HyperRingTag
import com.hyperring.sdk.core.nfc.NFCStatus
import kotlinx.coroutines.runBlocking

/**
 * jwt base demo
 */
class HyperRingMFA {
    companion object {
        //  MFA Data (key: HyperRingTagId, value: data)
        private var mfaData: MutableMap<Long, HyperRingMFAChallengeInterface> = mutableMapOf()

        /**
         * Init MFA MFAData (When veryfyHyperRingMFA, Using this MFA Data)
         */
        fun initializeHyperRingMFA(mfaData: List<HyperRingMFAChallengeInterface>): Boolean {
            try {
                mfaData.forEach {
                    if(it.id != null) {
                        this.mfaData[it.id!!] = it
                    }
                }
                if(this.mfaData.isEmpty()) {
                    throw MFAInitializationFailure("mfaData is Empty.")
                }
                return true
            } catch (e: Exception) {
                //todo If throw exception, return always true.
                throw MFAInitializationFailure("${e.message}")
            }
        }

        /**
         * Clear MFA data (if hyperRingIds item is exist)
         * If @param hyperRingId is null, clear every MFA data
         * todo New Function. talk to manager
         *
         * @param hyperRingId
         */
        fun clearMFAData(hyperRingIds: List<Long>?) {
            if (hyperRingIds == null) {
                mfaData.clear()
            }
            hyperRingIds!!.forEach {
                if (mfaData.containsKey(it)) {
                    mfaData.remove(it)
                }
            }
        }

        /**
         * Update MFA Data
         */
        fun updateMFAData(mfaDataList: List<HyperRingMFAChallengeInterface>) {
            mfaDataList.forEach {
                if(it.id != null) {
                    mfaData[it.id!!] = it
                }
            }
        }

        /**
         * Compare HyperRingTag`s MFA Data and Saved MFA(from initializeHyperRingMFA function)
         */
        fun verifyHyperRingMFAAuthentication(response: MFAChallengeResponse): Boolean {
            try {
                if (mfaData.containsKey(response.id)) {
                    return isValidResponse(response)
                }
            } catch (e: Exception) {
                throw VerificationFailure("Verification process failed")
            }
            return false
        }

        /**
         * Implement the logic to verify the MFA response here
         * Example: Checking the timeliness and integrity of the response
         */
        private fun isValidResponse(response: MFAChallengeResponse): Boolean {
            // Implement the actual response verification logic
            // Assume always returning true as a placeholder
            val result = processMFAChallenge(response)
            return result.isSuccess?:false
        }

        /**
         * Open MFA UserInterface.
         * If idList is empty or null. Listening every scanned tag event
         * else idList is exist. Check only that ids with saved MFA data
         *
         * @param activity
         */
        fun requestHyperRingMFAAuthentication(activity: Activity): MFAChallengeResponse {
            val mfaChallengeResponse: MFAChallengeResponse? = showMFADialog(activity)
            return mfaChallengeResponse?: MFAChallengeResponse(null, null, null)
        }

        private fun showMFADialog(activity: Activity): MFAChallengeResponse? {
            var mfaChallengeResponse : MFAChallengeResponse? = null

            HyperRingNFC.initializeHyperRingNFC(activity)
            if(HyperRingNFC.getNFCStatus() == NFCStatus.NFC_UNSUPPORTED) {
                throw HyperRingNFC.UnsupportedNFCException()
            }

            if(HyperRingNFC.getNFCStatus() == NFCStatus.NFC_DISABLED) {
                Toast.makeText(activity, "Please enable NFC", Toast.LENGTH_SHORT).show()
                return null
            }

            runBlocking {
                val dialog = Dialog(activity)
                fun onDiscovered(tag: HyperRingTag): HyperRingTag {
                    activity.runOnUiThread {
                        val mfaData = processMFAChallenge(tag.data)

                        val image: ImageView = dialog.findViewById(R.id.image)
                        if(mfaData.isSuccess == true) {
                            mfaChallengeResponse = mfaData
                            image.setImageResource(R.drawable.img_success)
                            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                activity.runOnUiThread {
                                    if(dialog.isShowing) {
                                        dialog.dismiss()
                                    }
                                }
                            }, 1000)
                        } else {
                            // Failed
                            image.setImageResource(R.drawable.img_failed)
                            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                activity.runOnUiThread {
                                    image.setImageResource(R.drawable.img_ready)
                                }
                            }, 1000)
                        }
                    }
                    return tag
                }

                HyperRingNFC.startNFCTagPolling(activity, onDiscovered= :: onDiscovered)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.custom_layout)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window?.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                dialog.show()
                dialog.window?.setAttributes(lp)
                dialog.setOnDismissListener {
                    HyperRingNFC.stopNFCTagPolling(activity)
                }
            }.let {
                return mfaChallengeResponse
            }
        }

        /**
         * Return challenge hyperRingData with mfaData[hyperRingData.id]
         */
        private fun processMFAChallenge(hyperRingData: HyperRingDataInterface?): MFAChallengeResponse {
            if(hyperRingData?.id != null && mfaData.containsKey(hyperRingData.id)) {
                try {
                    return mfaData[hyperRingData.id]!!.challenge(hyperRingData)
                } catch (e: Exception) {
                    Log.e("HyperRingMFA", "$e")
                }
            }
            return MFAChallengeResponse(hyperRingData!!.id, hyperRingData.data, isSuccess = false)
        }

    }

    class MFAInitializationFailure(message: String) : Exception(message)
    class VerificationFailure(message: String) : Exception(message)
}