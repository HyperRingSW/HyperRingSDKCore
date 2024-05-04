package com.hyperring.sdk.core.mfa

import android.content.Context
import com.hyperring.sdk.core.nfc.HyperRingDataInterface

/**
 * jwt base demo
 */
class HyperRingMFA {
    companion object {
        private var data: MutableMap<Long, HyperRingDataInterface> = mutableMapOf()

        /**
         * Init MFA MFAData (When veryfyHyperRingMFA, Using this MFA Data)
         */
        fun initializeHyperRingMFA(hyperRingId: Long, mfaData: HyperRingDataInterface): Boolean {
            try {
                data[hyperRingId] = mfaData
                return true
            } catch (e: Exception) {
                //todo If throw exception, return always true.
//                throw MFAInitializationFailure("${e.message}")
            }
            return false
        }

        /**
         * Clear MFA data
         * if @param hyperRingId is null, clear every MFA data
         *
         * @param hyperRingId
         * todo New Function. talk to manager
         */
        fun clearMFAData(hyperRingId: Long?) {
            if(hyperRingId == null) {
                data.clear()
            }
            if(data.containsKey(hyperRingId)) {
                data.remove(hyperRingId)
            }
        }

        /**
         * Compare HyperRingTag`s MFA Data and Saved MFA(from initializeHyperRingMFA function)
         */
        fun verifyHyperRingMFAAuthentication(response: MFAChallengeResponse): Boolean {
            try {
                if(data.containsKey(response.id)) {
                    return isValidResponse(response)
                }
            } catch (e: Exception) {
                when (e) {
                    is SecurityException -> throw VerificationFailure("Verification process failed")
                    // todo replay attack check rules(like counting during 1min)
                    else -> throw ReplayAttackDetected("Replay attack detected")
                }
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
            return true
        }

        /**
         * Open MFA UserInterface.
         * If idList is empty. Every scanned tag event listening
         * else idList is exist. Check only that ids with saved MFA data
         *
         * @param context
         * @param idList
         */
        fun requestHyperRingMFAAuthentication(context: Context, idList: List<Long>): MFAChallengeResponse {
            //todo fix it(dummy)
            return MFAChallengeResponse(null, null)
        }
    }

    class MFAInitializationFailure(message: String) : Exception(message)
    class VerificationFailure(message: String) : Exception(message)
    class ReplayAttackDetected(message: String) : Exception(message)
}