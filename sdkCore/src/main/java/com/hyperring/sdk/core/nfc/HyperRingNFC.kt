package com.hyperring.sdk.core.nfc
import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log

class HyperRingNFC {
    companion object {
        private var initialized = false
        private var adapter: NfcAdapter? = null
        var isPolling: Boolean = false // Polling status

        /**
         * Initialized HyperRingNFC
         */
        fun initializeHyperRingNFC(context: Context) {
            initialized = true
            adapter = NfcAdapter.getDefaultAdapter(context)
        }

        /**
         * Get current NFC status
         *
         * @return NFCStatus
         * @exception NeedInitializeException If not initialized HyperRingNFC
         */
        fun getNFCStatus(): NFCStatus {
            var status = NFCStatus.NFC_UNSUPPORTED

            // If not initialized, throw exception
            if(!initialized) {
                isPolling = false
                throw NeedInitializeException()
            }

            when {
                adapter == null -> {
                    log( "NFC is not available.")
                    status = NFCStatus.NFC_UNSUPPORTED
                }
                adapter!!.isEnabled -> {
                    log( "Start NFC Polling.")
                    status = NFCStatus.NFC_ENABLED
                }
                !adapter!!.isEnabled -> {
                    log( "NFC is not polling.")
                    status = NFCStatus.NFC_DISABLED
                }
            }

            (status == NFCStatus.NFC_ENABLED).also { isPolling = it }
            return status
        }

        /**
         * Start NFCTag Polling
         *
         * @param activity NFC adapter need Android Activity
         * @param onDiscovered When NFC tagged. return tag data
         */
        fun startNFCTagPolling(activity: Activity, onDiscovered: (HyperRingTag) -> HyperRingTag) {
            if(getNFCStatus() == NFCStatus.NFC_ENABLED) {
                log( "Start NFC Polling.")
                adapter?.enableReaderMode(activity, {
                    val scannedHyperRingTag = HyperRingTag(it)
                    onDiscovered(scannedHyperRingTag)
                }, HyperRingTag.flags, null)
            }
        }

        /**
         * Stop NFC Tag Polling
         *
         * @param activity
         */
        fun stopNFCTagPolling(activity: Activity) {
            isPolling = false
            adapter?.disableReaderMode(activity)
            log( "Stop NFC Polling.")
        }

        /**
         * Write data to HyperRingTag
         * If hyperRingTagId is null, write data to Regardless of HyperRing iD
         * else hyperRingTagId has ID, write data to HyperRing with only the same HyperRing ID
         *
         * @param hyperRingTagId HyperRing tag ID
         * @param hyperRingTag HyperRing data.
         */
        fun write(hyperRingTagId: Long?, hyperRingTag: HyperRingTag): Boolean {
            if(hyperRingTagId == null) {
                // Write data to Any HyperRing NFC Device
            } else if(hyperRingTag.id != hyperRingTagId) {
                // Not matched HyperRingTagId (Other HyperRingTag Tagged)
                log("[Write] tag id is not matched.")
                return false
            }

            if(hyperRingTag.isHyperRingTag()) {
                val ndef = hyperRingTag.getNDEF()
                if (ndef != null) {
                    try {
                        ndef.connect()
                        ndef.writeNdefMessage(hyperRingTag.ndefMessage())
                        log("[Write] success.")
                    } catch (e: Exception) {
                        log("[Write] exception: ${e.toString()}")
                    } finally {
                        ndef.close()
                    }
                    return true
                } else {
                    log("ndef is null")
                }
            }
            return false
        }

        /***
         * If HyperRingTagId is same HyperRingData`s inner hyperRingTagId return Data
         *
         * @param hyperRingTagId
         * @param hyperRingTag
         */
        fun read(hyperRingTagId: Long?, hyperRingTag: HyperRingTag): HyperRingTag? {
            if(hyperRingTagId == null) {
                return hyperRingTag
            } else if(hyperRingTag.id == hyperRingTagId) {
                return hyperRingTag
            }
            return null
        }

        /**
         * HyperRingNFC Logger
         */
        fun log(text: String) {
            Log.d("HyperRingNFC", "log: $text")
        }
    }

    class NeedInitializeException: Exception("Need HyperRing NFC Initialize")
}