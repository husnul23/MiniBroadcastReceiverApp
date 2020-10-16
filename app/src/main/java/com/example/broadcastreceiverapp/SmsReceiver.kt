package com.example.broadcastreceiverapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import java.lang.Exception
import java.util.*

class SmsReceiver : BroadcastReceiver() {

    private val TAG = SmsReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val bundle = intent.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<Any>
                for (aPdusObj in pdusObj) {
                    val currentMessage = getIncomingMessage(aPdusObj, bundle)
                    val senderNum = currentMessage.displayOriginatingAddress
                    val message = currentMessage.displayMessageBody
                    Log.d(TAG, "senderNum:, $senderNum; message: $message")
                    val showSmsIntent = Intent(context, SmsReceiverActivity::class.java)
                    showSmsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    showSmsIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_NO, senderNum)
                    showSmsIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_MESSAGE, message)
                    context.startActivity(showSmsIntent)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception smsReceiver$e")
        }
    }

    private fun getIncomingMessage(aObjects: Any, bundle: Bundle): SmsMessage {
        val currentSMS: SmsMessage
        if (Build.VERSION.SDK_INT >= 23) {
            val format = bundle.getString("format")
            currentSMS = SmsMessage.createFromPdu(aObjects as ByteArray, format)
        } else currentSMS = SmsMessage.createFromPdu(aObjects as ByteArray)
        return currentSMS
    }
}
