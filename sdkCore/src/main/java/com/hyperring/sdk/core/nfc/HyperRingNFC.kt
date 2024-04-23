package com.hyperring.sdk.core.nfc

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.widget.Toast
import java.util.Objects

class HyperRingNFC {
    companion object {
        private var adapter: NfcAdapter? = null
        /// todo
        fun hello(): String {
            val text = "hello HyperRing NFC"
            print(text)
            return text
        }

        fun initializeHyperRingNFC(context: Context) {
            adapter = NfcAdapter.getDefaultAdapter(context)
        }

        fun getNFCStatus(): NFCStatus {
            // Logic to check NFC status
            if(adapter == null) {
                /// todo Check this code
                return NFCStatus.NFC_UNSUPPORTED
            }else if(adapter!!.isEnabled) {
                return NFCStatus.NFC_ENABLED
            }else if(!adapter!!.isEnabled) {
                return NFCStatus.NFC_DISABLED
            }
            return NFCStatus.NFC_UNSUPPORTED
        }

        fun startNFCTagPolling(activity: Activity, onDiscovered: Function<Objects>) {
            // Start NFC scanning and call onDiscovered() when a tag is found

            val adapter = adapter ?: run {
                showToast(activity.baseContext, "NFC is not available for device.")
                return
            }
            val flags = NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NFC_BARCODE or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS

            adapter.enableReaderMode(activity, {
                showToast(activity.baseContext, it.toString())
            }, flags, null)
        }

        private fun showToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

        }

        /// todo
        fun stopNFCTagPolling() {
            // Stop NFC scanning
        }

        /**
         * Encrypts and stores data on a HyperRing NFC tag.
         *
         * @param tagId Unique tag ID
         * @param encryptionKey Encryption key
         * @param data Data to be stored
         * @return Boolean indicating whether the data storage was successful.
         */
        /// todo
        fun write(tagId: Double, encryptionKey: String, data: String): Boolean {
            val encryptedData = encrypt(data, encryptionKey)
            // Implementation of data encryption logic needed

            // Implement logic to write encryptedData to the NFC tag using tagId
            // Assume operation is successful and return true in this example
            return true
        }

        /// todo
        /// Change Abstract, Interface or throw Exception
        fun encrypt(data: String, key: String): String? {
            throw NeedOverrideException()
        }
    }

    class NeedOverrideException() : Exception("Need Override this function")
}