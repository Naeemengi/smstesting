package com.company.smstestingapp

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX
import android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT
import android.webkit.MimeTypeMap
import androidx.work.*
import com.company.smstestingapp.apicall.SaveMessageWorker
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.MmsReceivedReceiver
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*

class MMSReceiver: MmsReceivedReceiver() {

    override fun onMessageReceived(context: Context, uri: Uri) {
        val mmsId = uri.lastPathSegment!!.toInt()
        val type = MESSAGE_TYPE_INBOX
        val date = System.currentTimeMillis()
        val selectionPart = "mid=$mmsId"
        val partUri = Uri.parse("content://mms/part")
        val cursor = context.contentResolver.query(
            partUri, null,
            selectionPart, null, null
        )!!
        var body = ""
        var file: String? = null
        if (cursor.moveToFirst()) {
            do {
                val partId: String = cursor.getString(cursor.getColumnIndex("_id"))
                val typeString = cursor.getString(cursor.getColumnIndex("ct"))
             if (file==null &&
                    (typeString.startsWith("video") ||
                            typeString.startsWith("image") ||
                            typeString.startsWith("audio"))
                ) {
                    file = saveFile(context,partId, typeString, date )
                }
                if (file!=null && body.isNotEmpty()) break
            } while (cursor.moveToNext())
        }
        cursor.close()


        if (file==null && body.isBlank()) return ;

        val sender = getAddressNumber(context,mmsId)
        val mType = if (type in 0..2) {
            if (sender.first) MESSAGE_TYPE_SENT else MESSAGE_TYPE_INBOX
        } else {
            type
        }
        val rawNumber = sender.second

//        val message = Message(
//            body, mType, date, path = file, id = mmsId
//        )

            val sh = android.preference.PreferenceManager.getDefaultSharedPreferences(context)

            val myNumber = sh.getString("myNumber", "000");
            val dataToSend = Data.Builder()
                .putString(SaveMessageWorker.SENDER_PHONE_NUMBER,rawNumber )
                .putString(SaveMessageWorker.RECIEVER_PHONE_NUMBER,myNumber )
                .putString(SaveMessageWorker.STATUS,"incoming" )
                .putString(SaveMessageWorker.MESSAGE_CONTENT,"empty")
                .putString(SaveMessageWorker.SENT_AT, Calendar.getInstance().time.toString())
                .putString(SaveMessageWorker.RECIEVE_AT, Calendar.getInstance().time.toString())
                .putString(SaveMessageWorker.FILE_PATH, file)
                .build()
            val createPostConstraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val saveNumberWorkRequest = OneTimeWorkRequest.Builder(SaveMessageWorker::class.java)
                .setConstraints(createPostConstraints)
                .setInputData(dataToSend).build()

            context.let { WorkManager.getInstance(it).enqueue(saveNumberWorkRequest) }

    }
    private fun getAddressNumber(mContext: Context,id: Int): Pair<Boolean, String> {
        var threadId = -1L
        mContext.contentResolver.query(
            Uri.parse("content://mms/${id}"),
            arrayOf("thread_id"),
            null,
            null,
            null
        )?.apply {
            if (moveToFirst()) {
                threadId = getString(0).toLong()
            }
        }
        var selection = "type=137 AND msg_id=$id"
        val uriAddress = Uri.parse("content://mms/${id}/addr")
        var cursor = mContext.contentResolver.query(
            uriAddress, arrayOf("address"), selection, null, null
        )!!
        var address = ""
        if (cursor.moveToFirst()) {
            do {
                address = cursor.getString(cursor.getColumnIndex("address"))
                if (address != null) break
            } while (cursor.moveToNext())
        }
        cursor.close()
        try {
            if (Telephony.Threads.getOrCreateThreadId(mContext, address) == threadId) {
                return false to address
            }
        } catch (e: Exception) { }

        selection = "type=151 AND msg_id=$id"
        cursor = mContext.contentResolver.query(
            uriAddress, null, selection, null, null
        )!!
        if (cursor.moveToFirst()) {
            do {
                address = cursor.getString(cursor.getColumnIndex("address"))
                if (address != null) break
            } while (cursor.moveToNext())
        }
        cursor.close()
        return true to address
    }
    private fun saveFile(mContext : Context,_id: String, typeString: String, date: Long): String {
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

    override fun onError(p0: Context?, p1: String?) {}
}