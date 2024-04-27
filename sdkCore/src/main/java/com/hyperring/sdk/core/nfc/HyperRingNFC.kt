package com.hyperring.sdk.core.nfc
import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import android.widget.Toast

class HyperRingNFC {
    companion object {
        private var adapter: NfcAdapter? = null
        var isPolling: Boolean = false
        const val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE
//                    NfcAdapter.FLAG_READER_NFC_BARCODE or
//                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
//                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
//                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS

        /// todo header Annotation
        fun initializeHyperRingNFC(context: Context) {
            adapter = NfcAdapter.getDefaultAdapter(context)
        }

        /// todo header Annotation
        /// Check NFC Status - After initializeHyperRingNFC()
        fun getNFCStatus(): NFCStatus {
            // Logic to check NFC status
            if(adapter == null) {
                return NFCStatus.NFC_UNSUPPORTED
            }else if(adapter!!.isEnabled) {
                return NFCStatus.NFC_ENABLED
            }else if(!adapter!!.isEnabled) {
                return NFCStatus.NFC_DISABLED
            }
            return NFCStatus.NFC_UNSUPPORTED
        }

        /// todo header Annotation
        /// Start NFC scanning and call onDiscovered() when a tag is found
        fun startNFCTagPolling(activity: Activity, onDiscovered: (Context, Tag) -> Tag) {
            if(adapter == null) {
                showToast(activity.baseContext, "NFC is not available for device.")
                isPolling = false
                return
            } else {
                showToast(activity.baseContext, "Start NFC Polling.")
                isPolling = true
            }

            adapter?.enableReaderMode(activity, {
                // callback part
                showToast(activity.baseContext, it.id.toString())
                onDiscovered(activity, it)
                showToast(activity.applicationContext, it.id.toString())
            }, flags, null)
        }

        /// todo header Annotation
        fun stopNFCTagPolling(activity: Activity) {
            // Stop NFC scanning
            isPolling = false
            adapter?.disableReaderMode(activity)
        }

        /// todo header Annotation
        private fun showToast(context: Context, text: String) {
            Log.d("showTaost", "text: $text")
//            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        /// todo is dummy code
        /**
         * Encrypts and stores data on a HyperRing NFC tag.
         *
         * @param tagId Unique tag ID
         * @param encryptionKey Encryption key
         * @param data Data to be stored
         * @return Boolean indicating whether the data storage was successful.
         */
        fun write(tagId: Double, encryptionKey: String, data: String): Boolean {
            val encryptedData = encrypt(data, encryptionKey)
            // Implementation of data encryption logic needed

            // Implement logic to write encryptedData to the NFC tag using tagId
            // Assume operation is successful and return true in this example
            return true
        }

        /// todo is dummy code
        fun read(tagId: Int, decryptionKey: String): String {
            // Implement logic to read data from the NFC tag using tagId
            val data = "encryptedDataExample"  // Assume encrypted data as a placeholder

            // Implementation of decryption logic
            val decryptedData = decrypt(data, decryptionKey)

            return decryptedData
        }

        /// todo is dummy code
        /// Change Abstract, Interface or throw Exception
        fun encrypt(data: String, key: String): String? {
            throw NeedOverrideException()
        }

        /// todo is dummy code
        private fun decrypt(data: String, key: String): String {
            // Implementation of decryption logic
            // Assume decrypted data is returned as a placeholder
            return "decrypted_$data"
        }

    }

    class NeedOverrideException() : Exception("Need Override this function")
}