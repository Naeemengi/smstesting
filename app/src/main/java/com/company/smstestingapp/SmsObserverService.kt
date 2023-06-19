package com.company.smstestingapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.provider.Telephony
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.work.*
import com.company.smstestingapp.apicall.SaveMessageWorker
import java.io.File
import java.io.FileOutputStream
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
        mContext?.contentResolver?.query(
            Telephony.Mms.CONTENT_URI,
            null,
            null,
            null,
            null
        ) ?.apply {
            if (moveToFirst()) {
                val idColumn = getColumnIndex("_id")
                val dateColumn = getColumnIndex("date")
                val textColumn = getColumnIndex("text_only")
                val typeColumn = getColumnIndex("msg_box")
                if (smsChecker(idColumn.toString())) {
                do {
                    val id = getString(idColumn)
                    val isMms = getString(textColumn) == "0"
                    val date = getString(dateColumn).toLong() * 1000
                    val type = getString(typeColumn).toInt()
                    if (isMms) {
                        val selectionPart = "mid=$id"
                        val partUri = Uri.parse("content://mms/part")
                        val cursor = mContext?.contentResolver?.query(
                            partUri, null,
                            selectionPart, null, null
                        )!!
                        var body = ""
                        var file: String? = null
                        if (cursor.moveToFirst()) {
                            do {
                                val partId: String = cursor.getString(cursor.getColumnIndex("_id"))
                                val typeString = cursor.getString(cursor.getColumnIndex("ct"))
                                if (file == null &&
                                    (typeString.startsWith("video") ||
                                            typeString.startsWith("image") ||
                                            typeString.startsWith("audio"))
                                ) {
                                    file = saveFile(partId, typeString, date)
                                }
                                if (file != null && body.isNotEmpty()) break
                            } while (cursor.moveToNext())
                        }
                        cursor.close()


                        if (file == null && body.isBlank()) return

                        Toast.makeText(mContext, "MMS Received", Toast.LENGTH_LONG).show()

                    }
                } while (moveToNext())
            }
            }
            close()
        }

        // Use message content for desired functionality
//        if (smsChecker(id)) {

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

    private fun saveFile(_id: String, typeString: String, date: Long): String {
        val partURI = Uri.parse("content://mms/part/$_id")
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(typeString)
        val name = "$date.$ext"
        val destination = File(mContext.filesDir, name)
        val output = FileOutputStream(destination)
        val input = mContext.contentResolver.openInputStream(partURI) ?: return ""
        val buffer = ByteArray(4 * 1024)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
        }
        output.flush()
        return destination.absolutePath
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