package com.hyperring.sdk.core.nfc
import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.util.Log
import java.nio.charset.StandardCharsets


class HyperRingNFC {
    companion object {
        private var isinitialized = false
        private var adapter: NfcAdapter? = null
        var isPolling: Boolean = false

        // Current NFC spec / NFC-A (ISO 14443-3A)
        // If Type of HyperRing NFC is added. Fix this flags
        private const val flags = NfcAdapter.FLAG_READER_NFC_A
/*        private const val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE
                    NfcAdapter.FLAG_READER_NFC_BARCODE or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS*/

        /// todo header Annotation
        fun initializeHyperRingNFC(context: Context) {
            isinitialized = true
            adapter = NfcAdapter.getDefaultAdapter(context)
        }

        /// todo header Annotation
        fun getNFCStatus(): NFCStatus {
            // If not initialized,
            if(!isinitialized) {
                throw NeedInitializeException()
            }

            // Logic to check NFC status
            if(adapter == null) {
                return NFCStatus.NFC_UNSUPPORTED
            } else if(adapter!!.isEnabled) {
                return NFCStatus.NFC_ENABLED
            } else if(!adapter!!.isEnabled) {
                return NFCStatus.NFC_DISABLED
            }
            return NFCStatus.NFC_UNSUPPORTED
        }

        /// todo header Annotation
        /// Start NFC scanning and call onDiscovered() when a tag is found
        fun startNFCTagPolling(activity: Activity, onDiscovered: (Activity, HyperRingData) -> HyperRingData) {
            if(adapter == null) {
                log( "NFC is not available.")
                isPolling = false
                return
            } else if(!adapter!!.isEnabled) {
                log( "NFC is not polling.")
                isPolling = false
                return
            } else {
                log( "Start NFC Polling.")
                isPolling = true
            }

            adapter?.enableReaderMode(activity, {
                // callback part
                onDiscovered(activity, HyperRingData(it))
                log(it.id.toString())
            }, flags, null)
        }

        /// todo header Annotation
        fun stopNFCTagPolling(activity: Activity) {
            isPolling = false
            adapter?.disableReaderMode(activity)
            log( "Stop NFC Polling.")
        }

        private fun log(text: String) {
            Log.d("HyperRingNFC", "text: $text")
        }

        /**
         * Encrypts and stores data on a HyperRing NFC tag.
         *
         * @param hyperRingTagId HyperRing tag ID
         * @param encryptionKey Encryption key
         * @param data Data to be stored
         */
        fun write(hyperRingTagId: Long?, hyperRingData: HyperRingData): Boolean {
            if(hyperRingTagId == null) {
                // Write data to Any HyperRing NFC
            } else if(hyperRingData.hyperRingTagId != hyperRingTagId) {
                // hyperRingTagId != data.hyperRingTagId (Different)
                log("[Write] tag id is not matched.")
                return false
            }

            if(hyperRingData.isHyperRingTag() && hyperRingData.isNDEF()) {
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

        /// todo
        fun read(hyperRingData: HyperRingData): HyperRingData? {
            if(hyperRingData.isNDEF()) {
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
    }

    class NeedInitializeException: Exception("Need HyperRing NFC Initialize")
}