package com.demo.warehouseinventoryapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SMSReceiver internal constructor(var smsHandler: ReceiverCallbacks) : BroadcastReceiver() {
    interface ReceiverCallbacks {
        fun onSMSReceived(msg: String)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (msg in smsMessages) smsHandler.onSMSReceived(msg.displayMessageBody)
    }
}
