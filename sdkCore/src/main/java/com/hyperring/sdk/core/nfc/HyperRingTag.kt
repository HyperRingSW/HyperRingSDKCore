package com.hyperring.sdk.core.nfc
import android.nfc.NdefMessage
import android.nfc.NdefRecord
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
 *         "tagId": HyperRingTadId
 *         "data": encrypted or original jsonStringData
 *     }
 * @param tag NFC Tag
 * @param hyperRingTagId hyperRing`s Tag ID - if null, not initialized NFC Card
 */
//class HyperRingData(var hyperRingTagId: Long?, var tag: Tag) {
open class HyperRingTag(private var tag: Tag) {
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

//    var hyperRingTagId: Long?, var json: String
//    companion object {
//        const val mime: String = "text/hyperring"
//        val gson: Gson = Gson()
//        // Current NFC spec / NFC-A (ISO 14443-3A), NFC Forum Type2, NTAG216
//        // If Type of HyperRing NFC is added. Update this flags
//        const val flags = NfcAdapter.FLAG_READER_NFC_A
//        /*        private const val flags = NfcAdapter.FLAG_READER_NFC_A or
//                        NfcAdapter.FLAG_READER_NFC_B or
//                        NfcAdapter.FLAG_READER_NFC_F or
//                        NfcAdapter.FLAG_READER_NFC_V or
//                        NfcAdapter.FLAG_READER_NFC_BARCODE or
//                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
//                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS
//        */

//        /**
//         * Crate HyperRingData from Tag
//         * @param tag
//         */
//        fun read(tag: Tag): HyperRingData? {
//            if(isHyperRingTag(tag)) {
//                val ndef = getNDEF(tag)
//                if (ndef != null) {
//                    ndef.connect()
//                    val msg: NdefMessage = ndef.ndefMessage
//                    HyperRingNFC.log("msg: ${msg.records}")
//                    ndef.close()
//                    if(msg.records != null) {
//                        var hrId: Long? = null
//                        var jsonData: String = BaseHyperRingJsonModel.emptyJsonString()
//
//                        msg.records?.forEach {
//                            val payload = String(it.payload, StandardCharsets.UTF_8)
//                            if(it.tnf == NdefRecord.TNF_UNKNOWN) {
//                                jsonData = fromJsonString(payload)
//                            }
//
//                        }
//                        return HyperRingData(hrId, jsonData)
//                    } else {
//                        HyperRingNFC.log("no records")
//                    }
//                } else {
//                    HyperRingNFC.log("ndef is null")
//                }
//            }
//            return null
//        }
//
//        private fun fromJsonString(payload: String): String {
//            val model = gson.fromJson(payload, BaseHyperRingJsonModel::class.java)
//            Log.d("HyperRingData", "fromJsonString: tagId: ${model.hyperRingTagId}")
//            return ""
//        }
//
//        /***
//         * return NDEF from tag
//         */
//        fun getNDEF(tag: Tag): Ndef? {
//            try {
//                if(isNDEF(tag)) {
//                    return Ndef.get(tag)
//                }
//            }catch (e: Exception) {
//                Log.e("HyperRingData", "getNDEF"+e.toString())
//            }
//            return null
//        }
//
//        fun isHyperRingTag(tag: Tag): Boolean {
//            return isNFCA(tag) && isNDEF(tag)
//        }
//
//        /**
//         * If NFC tag contains HyperRingData, return JsonString
//         * not contains, return emptyJsonString
//         */
//
//
//        fun isNFCA(tag: Tag): Boolean {
//            return tag.techList.contains("android.nfc.tech.NfcA")
//        }
//
//        fun isNDEF(tag: Tag): Boolean {
//            return tag.techList.contains("android.nfc.tech.Ndef")
//            //todo check it
////        try {
////            Log.d("HyperRingData", "isNDEF tag id: ${tag.id}")
////            val formatableTag : NdefFormatable = NdefFormatable.get(tag)
////            formatableTag.connect()
////            formatableTag.format(emptyMessage())
////            formatableTag.close()
////        }catch (e: Exception) {
////            Log.e("HyperRingData", "isNDEF: "+e.toString())
////        }
//        }
//
//        /**
//         *  @param tagUUIDBA TagUUID From Android Tag (ByteArray)
//         */
//        @OptIn(ExperimentalStdlibApi::class)
//        fun getLongTagId(tagUUIDBA: ByteArray): Long? {
//            var tagId: Long? = null
//            try {
//                tagId = tagUUIDBA.toHexString().hexToLong()
//            }catch (e: Exception) {
//                Log.e("HyperRingData", "getLongTagId: "+e.toString())
//            }
//            Log.d("HyperRingData"," ${"tagId:: $tagId"}")
//            return tagId
//        }
//    }
//
//    private fun emptyMessage(): NdefMessage {
//        return  NdefMessage(emptyNdefRecord())
//    }
//
//    private fun emptyNdefRecord(): NdefRecord {
//        return NdefRecord.createTextRecord("en", BaseHyperRingJsonModel.emptyJsonString())
//    }
//
//    /***
//     * If hyperRingTagId is not null, isHyperRingTag
//     */
//    fun isHyperRingTag(): Boolean {
//        return hyperRingTagId != null
//    }
//
//    fun getNDEF(): Ndef? {
//        return Companion.getNDEF(tag)
//    }

//    /**
//     * Return JsonData format NdefMessage
//     * //todo 이 데이터로 저장 처리
//     */
//    fun jsonDataMessage(): NdefMessage {
////        NdefRecord.createMime(HyperRingData.mime, jsonString(tag).toByteArray())
////        return NdefMessage(NdefRecord.createTextRecord("en", emptyJsonString()))
//        return NdefMessage(
//            NdefRecord(
//                NdefRecord.TNF_UNKNOWN,
//                null,
//                null,
//                BaseHyperRingJsonModel.toJsonString(tag).toByteArray()))
//    }
    class ReadOnlyException: Exception("Is Read Only NFC")
    class OverrideException: Exception("Needs Overriding")
}