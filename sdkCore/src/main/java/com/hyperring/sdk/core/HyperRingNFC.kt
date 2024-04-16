package com.hyperring.sdk.core

import android.content.Context
import android.nfc.NfcAdapter

class HyperRingNFC {
    companion object {
        private var adapter: NfcAdapter? = null
        fun hello(): String {
            val text = "hello HyperRing NFC"
            print(text)
            return text
        }

        /// todo
        fun init(context: Context) {
            adapter = NfcAdapter.getDefaultAdapter(context)
            print(isActive())
        }

        /// todo
        fun isActive(): Boolean {
            return adapter?.isEnabled ?: false
        }
    }
}