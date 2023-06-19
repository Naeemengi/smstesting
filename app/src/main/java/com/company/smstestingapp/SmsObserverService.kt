package com.company.smstestingapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.work.*
import com.company.smstestingapp.apicall.SaveMessageWorker
import java.util.*


class SmsObserverService : Service() {
    val myObserver = SmsObserver1(Handler())
    val mmsObserver = MmsObserver1(Handler())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        onTaskRemoved(intent);

        myObserver.setmContext(this)
        mmsObserver.setmContext(this)
//        myObserver.observe()
        val contentResolver = this.baseContext.contentResolver
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, myObserver)
        contentResolver.registerContentObserver(Uri.parse("content://mms"), true, mmsObserver)

        return START_STICKY
    }

    override fun onDestroy() {
        val contentResolver = this.baseContext.contentResolver
        contentResolver.unregisterContentObserver(myObserver)
        contentResolver.unregisterContentObserver(mmsObserver)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }
}
class SmsObserver1(handler: Handler?) : ContentObserver(handler) {
    private var lastSmsId: String? = null
    private var mContext: Context? = null
    fun setmContext(mContext: Context?) {
        this.mContext = mContext
    }
//    fun observe() {
//        val resolver = mContext!!.contentResolver;
//        // set our watcher for the Uri we are concerned with
//        resolver.registerContentObserver(
//            Uri.parse("content://sms/sent"),
//            false,  // only notify that Uri of change *prob won't need true here often*
//            this
//        )
//
//    }
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        val uriSMSURI = Uri.parse("content://sms/sent")
        val cur = mContext!!.contentResolver.query(uriSMSURI, null, null, null, null)
        cur!!.moveToNext()
        val id = cur.getString(cur.getColumnIndex("_id"))
        val address = cur.getString(cur.getColumnIndex("address"))
        // Optional: Check for a specific sender
        val message = cur.getString(cur.getColumnIndex("body"))
        // Use message content for desired functionality
    if (smsChecker(id)) {

        val sh = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext)

        val myNumber = sh.getString("myNumber", "000");

        val dataToSend = Data.Builder()
            .putString(SaveMessageWorker.SENDER_PHONE_NUMBER, myNumber)
            .putString(SaveMessageWorker.RECIEVER_PHONE_NUMBER, address);
        dataToSend.putString(SaveMessageWorker.MESSAGE_CONTENT, message)
                .putString(SaveMessageWorker.FILE_PATH, null)


        dataToSend.putString(SaveMessageWorker.SENT_AT, Calendar.getInstance().time.toString())
        dataToSend.putString(
            SaveMessageWorker.RECIEVE_AT,
           Calendar.getInstance().time.toString()
        )

        val createPostConstraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val saveNumberWorkRequest = OneTimeWorkRequest.Builder(SaveMessageWorker::class.java)
            .setConstraints(createPostConstraints)
            .setInputData(dataToSend.build()).build()

        WorkManager.getInstance(mContext!!).enqueue(saveNumberWorkRequest)
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
    }

    }

    // Prevent duplicate results without overlooking legitimate duplicates
    fun smsChecker(smsId: String): Boolean {
        var flagSMS = true
        if (smsId == lastSmsId) {
            flagSMS = false
        } else {
            lastSmsId = smsId
        }
        return flagSMS
    }

}

class MmsObserver1(handler: Handler?) : ContentObserver(handler) {
    private var lastSmsId: String? = null
    private var mContext: Context? = null
    fun setmContext(mContext: Context?) {
        this.mContext = mContext
    }
    //    fun observe() {
//        val resolver = mContext!!.contentResolver;
//        // set our watcher for the Uri we are concerned with
//        resolver.registerContentObserver(
//            Uri.parse("content://sms/sent"),
//            false,  // only notify that Uri of change *prob won't need true here often*
//            this
//        )
//
//    }
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        val uriSMSURI = Uri.parse("content://mms")
        val cur = mContext!!.contentResolver.query(uriSMSURI, null, null, null, null)
        cur!!.moveToNext()
        val id = cur.getString(cur.getColumnIndex("_id"))
        val address = cur.getString(cur.getColumnIndex("address"))
        // Optional: Check for a specific sender
        val message = cur.getString(cur.getColumnIndex("body"))
        // Use message content for desired functionality
        if (smsChecker(id)) {

//            val sh = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext)
//
//            val myNumber = sh.getString("myNumber", "000");
//
//            val dataToSend = Data.Builder()
//                .putString(SaveMessageWorker.SENDER_PHONE_NUMBER, myNumber)
//                .putString(SaveMessageWorker.RECIEVER_PHONE_NUMBER, address);
//            dataToSend.putString(SaveMessageWorker.MESSAGE_CONTENT, message)
//                .putString(SaveMessageWorker.FILE_PATH, null)
//
//
//            dataToSend.putString(SaveMessageWorker.SENT_AT, Calendar.getInstance().time.toString())
//            dataToSend.putString(
//                SaveMessageWorker.RECIEVE_AT,
//                Calendar.getInstance().time.toString()
//            )
//
//            val createPostConstraints =
//                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//            val saveNumberWorkRequest = OneTimeWorkRequest.Builder(SaveMessageWorker::class.java)
//                .setConstraints(createPostConstraints)
//                .setInputData(dataToSend.build()).build()
//
//            WorkManager.getInstance(mContext!!).enqueue(saveNumberWorkRequest)
            Toast.makeText(mContext, "MMS Received", Toast.LENGTH_LONG).show()
        }

    }

    // Prevent duplicate results without overlooking legitimate duplicates
    fun smsChecker(smsId: String): Boolean {
        var flagSMS = true
        if (smsId == lastSmsId) {
            flagSMS = false
        } else {
            lastSmsId = smsId
        }
        return flagSMS
    }

}