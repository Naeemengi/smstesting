package com.company.smstestingapp.apicall

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import com.company.smstestingapp.apicall.RetrofitClientManager.provideApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class SaveMessageWorker (
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val networkRepository = NetworkRepository(provideApiClient())

        val senderPhoneNumber = inputData.getString(SENDER_PHONE_NUMBER)

        val reciverPhoneNumber = inputData.getString(RECIEVER_PHONE_NUMBER)

        val status = inputData.getString(STATUS)

        val message = inputData.getString(MESSAGE_CONTENT)

        val sent_at = inputData.getString(SENT_AT)

        val recieve_at = inputData.getString(RECIEVE_AT)

        val file_Path = inputData.getString(FILE_PATH)


        withContext(Dispatchers.IO) {
            val result = async {

//                val saveMessageResponse = networkRepository.saveMessage(senderPhoneNumber,reciverPhoneNumber,status,message,sent_at,recieve_at,file_Path)

            }
            val postResult = result.await()
        }


        Log.e("Worker", "Saving Message $senderPhoneNumber $file_Path $message")

//        return if (saveNumberResponse.status == 200) {
        return  Result.success()
//        } else  {
//            Result.retry()
//        }
    }

    companion object {
        const val SENDER_PHONE_NUMBER = "SENDER_ID"
        const val RECIEVER_PHONE_NUMBER = "RECIEVER_ID"
        const val STATUS = "status"
        const val MESSAGE_CONTENT = "MESSAGE"
        const val SENT_AT = "SENT_AT"
        const val RECIEVE_AT = "RECIEVE_AT"
        const val FILE_PATH = "FILE_PATH"
    }
}