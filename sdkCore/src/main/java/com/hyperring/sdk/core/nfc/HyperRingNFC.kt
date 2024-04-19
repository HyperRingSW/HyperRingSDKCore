package com.hyperring.sdk.core.nfc

import android.content.Context
import android.nfc.NfcAdapter
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

        /// todo
        fun initializeHyperRingNFC(context: Context) {
            adapter = NfcAdapter.getDefaultAdapter(context)
        }

        /// todo
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

        /// todo
        fun startNFCTagPolling(onDiscovered: Function<Objects>) {
            // Start NFC scanning and call onDiscovered() when a tag is found
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