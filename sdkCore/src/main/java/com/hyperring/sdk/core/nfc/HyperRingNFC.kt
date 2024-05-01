package com.hyperring.sdk.core.nfc
import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.util.Log
import java.nio.charset.StandardCharsets

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
        fun startNFCTagPolling(activity: Activity, onDiscovered: (HyperRingData) -> HyperRingData) {
            if(getNFCStatus() == NFCStatus.NFC_ENABLED) {
                log( "Start NFC Polling.")
                adapter?.enableReaderMode(activity, {
                    // callback part
                    onDiscovered(HyperRingData(it))
                    log(it.id.toString())
                }, HyperRingData.flags, null)
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
         * @param hyperRingData HyperRing data.
         */
        fun write(hyperRingTagId: Long?, hyperRingData: HyperRingData): Boolean {
            if(hyperRingTagId == null) {
                // Write data to Any HyperRing NFC Device
            } else if(hyperRingData.hyperRingTagId != hyperRingTagId) {
                // Not matched HyperRingTagId (Other HyperRingTag Tagged)
                log("[Write] tag id is not matched.")
                return false
            }

            if(hyperRingData.isHyperRingTag()) {
                val ndef = hyperRingData.getNDEF()
                if (ndef != null) {
                    ndef.connect()
                    ndef.writeNdefMessage(hyperRingData.jsonDataMessage())
                    log("[Write] success.")
                    return true
                } else {
                    log("ndef is null")
                }
            }
            return false
        }

        /***
         * @param hyperRingData HyperRingData
         * todo @throws
         */
        fun read(hyperRingData: HyperRingData): HyperRingData? {
            if(hyperRingData.isHyperRingTag()) {
                val ndef = hyperRingData.getNDEF()
                if (ndef != null) {
                    ndef.connect()
                    if(ndef.ndefMessage.records != null) {
                        ndef.ndefMessage.records?.forEach {
                            val payload = String(it.payload, StandardCharsets.UTF_8)
                            log("records1: ${it.toMimeType()} | ${payload}")
                            log("records2: ${it.tnf} | ${it.type} | ${it.describeContents()}")
                        }
                    } else {
                        log("no records")
                    }
                } else {
                    log("ndef is null")
                }
            }
            return null
        }

        /**
         * HyperRingNFC Logger
         */
        private fun log(text: String) {
            Log.d("HyperRingNFC", "log: $text")
        }
    }

    class NeedInitializeException: Exception("Need HyperRing NFC Initialize")
}