package com.hyperring.sdk.core.nfc
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log

/**
 * HyperRingData
 * data structure
 *     NDEFRecord
 *     - Json
 *     {
 *         "id": HyperRingTadId
 *         "data": encrypted or original jsonStringData
 *     }
 * @param tag NFC Tag
 * @param hyperRingTagId hyperRing`s Tag ID - if null, not initialized NFC Card
 */
open class HyperRingTag(var tag: Tag) {
    var data: HyperRingData = HyperRingData(tag)
    val id: Long?
        get() {
            return data.id
        }

    fun isHyperRingTag(): Boolean {
            return isNFCA() && isNDEF()
        }

        private fun isNFCA(): Boolean {
            return tag.techList.contains("android.nfc.tech.NfcA")
        }

        private fun isNDEF(): Boolean {
            return isNDEF(tag)
        }

        /***
         * return NDEF from tag
         */
        fun getNDEF(): Ndef? {
            return Companion.getNDEF(tag)
        }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "tag" to nfcTagToMap(tag),          // Assuming 'Tag' can be part of the map
            "id" to (id ?: "null") // Handle null case if necessary
        )
    }

    fun nfcTagToMap(tag: Tag): Map<String, Any> {
        val tagId = tag.id?.let { byteArrayToHexString(it) } ?: "Unknown" // Convert ID to hex or return "Unknown"
        val techList = tag.techList.joinToString(", ") // Convert techList to comma-separated string

        return mapOf(
            "id" to tagId,          // Hex string or Base64 of the tag's ID
            "techList" to techList  // Supported NFC technologies
        )
    }

    // Helper function to convert a byte array to a hexadecimal string
    fun byteArrayToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) } // Convert each byte to hex
    }

    companion object {
        fun getNDEF(tag: Tag): Ndef? {
            try {
                if(isNDEF(tag)) {
                    return Ndef.get(tag)
                }
            }catch (e: Exception) {
                Log.e("HyperRingData", "getNDEF"+e.toString())
            }
            return null
        }

        fun isNDEF(tag: Tag): Boolean {
            return tag.techList.contains("android.nfc.tech.Ndef")
            //todo check it
//        try {
//            Log.d("HyperRingData", "isNDEF tag id: ${tag.id}")
//            val formatableTag : NdefFormatable = NdefFormatable.get(tag)
//            formatableTag.connect()
//            formatableTag.format(emptyMessage())
//            formatableTag.close()
//        }catch (e: Exception) {
//            Log.e("HyperRingData", "isNDEF: "+e.toString())
//        }
        }

        const val flags = NfcAdapter.FLAG_READER_NFC_A
        /*        private const val flags = NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS
        */
    }
}