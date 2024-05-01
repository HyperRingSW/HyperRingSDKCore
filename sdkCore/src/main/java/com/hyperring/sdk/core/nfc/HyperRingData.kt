package com.hyperring.sdk.core.nfc
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import com.google.gson.Gson

/**
 *  HyperRingData
 &
 * @param tag NFC Tag
 * @param hyperRingTagId hyperRing`s Tag ID - if null, not initialized NFC Card
    data structure
    NDEFRecord

    - Json
    {
        "data": encrypted or original jsonStringData
    }
 */
class HyperRingData(var tag: Tag) {
    var hyperRingTagId: Long? = getLongTagId(tag.id)
    /**
     * If tag data contains HyperRingData, return JsonString
     * not contains, return emptyJsonString
     */
    private fun jsonString(tag: Tag): String {
        if(!isHyperRingTag(tag)) {
            return emptyJsonString()
        }
        if(isNDEF()) {
            val ndef = getNDEF() ?: return emptyJsonString()
            if(!ndef.isWritable) {
                throw  ReadOnlyException()
            }
        }
        return emptyJsonString()
    }

    //    ISO14443A, NFC Forum Type2, NTAG216
    private fun isHyperRingTag(tag: Tag): Boolean {
        return isNFCA() && isNDEF()
    }

    companion object {
        const val mime: String = "text/hyperring"
        val gson: Gson = Gson()

        // Current NFC spec / NFC-A (ISO 14443-3A)
        // If Type of HyperRing NFC is added. Update this flags
        const val flags = NfcAdapter.FLAG_READER_NFC_A
        /*        private const val flags = NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE
                            NfcAdapter.FLAG_READER_NFC_BARCODE or
                            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS*/

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

        @OptIn(ExperimentalStdlibApi::class)
        fun getLongTagId(br: ByteArray): Long? {
            var tagId: Long? = null
            try {
                tagId = br.toHexString().hexToLong()
            }catch (e: Exception) {
                Log.e("HyperRingData", "getLongTagId: "+e.toString())
            }
            Log.d("HyperRingData"," ${"tagId:: $tagId"}")
            return tagId
        }
    }
    private fun emptyJsonString(): String {
        return  gson.toJson("{\"hyperRingTagId\":\"${hyperRingTagId}\"}")?:""
    }

    private fun emptyMessage(): NdefMessage {
        return  NdefMessage(emptyNdefRecord())
    }

    private fun emptyNdefRecord(): NdefRecord {
        // todo
        //Params:
        // tnf – a 3-bit TNF constant
        // type – byte array, containing zero to 255 bytes, or null
        // id – byte array, containing zero to 255 bytes, or null
        // payload – byte array, containing zero to (2 ** 32 - 1) bytes, or null
    //        NdefRecord(NdefRecord.TNF_UNKNOWN, emptyJsonData(), id, payload)

        return NdefRecord.createTextRecord("en", emptyJsonString())
    }

    fun isNFCA(): Boolean {
        return tag.techList.contains("android.nfc.tech.NfcA")
    }

    fun isNDEF(): Boolean {
        return tag.techList.contains("android.nfc.tech.Ndef")
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

    fun getNDEF(): Ndef? {
        try {
            if(isNDEF()) {
                val ndef = Ndef.get(tag)
                return ndef
            }
        }catch (e: Exception) {
            Log.e("HyperRingData", "getNDEF"+e.toString())
        }
        return null
    }

    fun isHyperRingTag(): Boolean {
        return isHyperRingTag(tag)
    }

    /**
     * Return JsonData format NdefMessage
     */
    fun jsonDataMessage(): NdefMessage {
//        NdefRecord.createMime(HyperRingData.mime, jsonString(tag).toByteArray())
//        return NdefMessage(NdefRecord.createTextRecord("en", emptyJsonString()))
        return NdefMessage(
            NdefRecord(
                NdefRecord.TNF_UNKNOWN,
                null,
                null,
                jsonString(tag).toByteArray()))
    }

    class ReadOnlyException: Exception("Is Read Only NFC")
}