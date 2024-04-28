package com.hyperring.sdk.core.nfc
import android.app.Activity
import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import java.nio.charset.StandardCharsets


class HyperRingNFC {
    companion object {
        var isInited = false
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
            isInited = true
            adapter = NfcAdapter.getDefaultAdapter(context)
        }

        /// todo header Annotation
        /// Check NFC Status - After initializeHyperRingNFC()
        fun getNFCStatus(): NFCStatus {
            // If not initialized,
            if(!isInited) throw NeedOverrideException()
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

        @OptIn(ExperimentalStdlibApi::class)
        private fun getTagIdFromByteArray(br: ByteArray): Long {
            var tagId : Long = 0
            try {
                tagId = br.toHexString().hexToLong()
            }catch (e: Exception) {
                Log.e("TAGGED", e.toString())
            }
            Log.d("TAGGED"," ${"tagId:: ${tagId}"}")
            return tagId
        }

        /// todo header Annotation
        /// Start NFC scanning and call onDiscovered() when a tag is found
        fun startNFCTagPolling(activity: Activity, onDiscovered: (Activity, Tag) -> Tag) {
            if(adapter == null) {
                logging( "NFC is not available.")
                isPolling = false
                return
            } else if(!adapter!!.isEnabled) {
                logging( "NFC is not polling.")
                isPolling = false
                return
            } else {
                logging( "Start NFC Polling.")
                isPolling = true
            }

            adapter?.enableReaderMode(activity, {
                // callback part
                onDiscovered(activity, it)
                Log.d("TAGGED"," ${convertBAToTagUUID(it.id)}")
                logging( it.id.toString())
            }, flags, null)
        }

        /// todo header Annotation
        fun stopNFCTagPolling(activity: Activity) {
            // Stop NFC scanning
            isPolling = false
            adapter?.disableReaderMode(activity)
            logging( "Stop NFC Polling.")
        }

        /// todo header Annotation
        private fun logging(text: String) {
            Log.d("HyperRingNFC", "text: $text")
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
        fun write(tag: Tag, encryptionKey: String?, data: String): Boolean {
            val encryptedData = encrypt(data, encryptionKey)
            // Implementation of data encryption logic needed

//            if(nfca.maxTransceiveLength > data.length) {
//                ///todo Exception or Msg
////                return null
//            }

            val message = NdefMessage(NdefRecord.createTextRecord("en",encryptedData))
            val size = message.toByteArray().size
            try {
                val ndef = Ndef.get(tag)
                if (ndef != null) {
                    ndef.connect()
                    if (!ndef.isWritable) {
                        logging("can not write NFC tag")
                        return false
                    }
                    if (ndef.maxSize < size) {
                        logging("NFC tag size too large")
                        return false
                    }
                    ndef.writeNdefMessage(message)
                    logging("NFC tag is writted")
                }
            } catch (e: Exception) {
                e.message?.let { logging(it) }
            }

            // Implement logic to write encryptedData to the NFC tag using tagId
            // Assume operation is successful and return true in this example
            return true
        }

        /// todo is dummy code
        @OptIn(ExperimentalStdlibApi::class)
        fun read(tag: Tag, decryptionKey: String?): String {
            var tagId : Long = getTagIdFromByteArray(tag.id)
            var decryptedData = ""

            logging("tagId: $tagId")
//          https://developer.android.com/reference/android/nfc/tech/Ndef
//          https://developer.android.com/reference/android/nfc/tech/NfcA
//          NFC-A (ISO 14443-3A)
            var isNDEF = false
            if(tag.techList.contains("android.nfc.tech.Ndef")) {
                isNDEF = true
            }

            if(isNDEF) {
                val ndef = Ndef.get(tag)
                if (ndef != null) {
                    ndef.connect()
                    if(ndef.ndefMessage.records != null) {
                        logging("record base: ${ndef.ndefMessage.toByteArray()}")
                        ndef.ndefMessage.records?.forEach {
//                            val decryptedData = decrypt(data, decryptionKey)
                            val payload = String(it.payload, StandardCharsets.UTF_8)
                            logging("records: ${it.toMimeType()} | ${decrypt(it.payload.toHexString(), decryptionKey)} | ${payload}")
                        }

                        logging("ndef.maxSize: ${ndef.maxSize}")
                        logging("${ndef.ndefMessage.toByteArray()}")
                        logging("tag read")
                    } else {
                        logging("no records")
                    }
                } else {
                    logging("ndef is null")
                }
            }
            return decryptedData
        }

        /// todo is dummy code
        /// Change Abstract, Interface or throw Exception
        fun encrypt(data: String, key: String?): String {
//            throw NeedOverrideException()
            return "encrypt"
        }

        /// todo is dummy code
        private fun decrypt(data: String, key: String?): String {
            // Implementation of decryption logic
            // Assume decrypted data is returned as a placeholder
            return "decrypted_$data"
        }

        /**
         * Convert Tag`s ByteArray ID to String UUID
         */
        fun convertBAToTagUUID(b: ByteArray?): String {
            return if (b != null) {
                val s = StringBuilder(2 * b.size)
                for (i in b.indices) {
                    val t = Integer.toHexString(b[i].toInt())
                    val l = t.length
                    if (l > 2) {
                        s.append(t.substring(l - 2))
                    } else {
                        if (l == 1) {
                            s.append("0")
                        }
                        s.append(t)
                    }
                }
                s.toString()
            } else {
                ""
            }
        }
    }

    class NeedOverrideException() : Exception("Need Override this function")
}