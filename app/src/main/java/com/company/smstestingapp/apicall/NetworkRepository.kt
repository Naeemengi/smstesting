package com.company.smstestingapp.apicall

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class NetworkRepository (
    private val apiInterface: ApiInterface
) {
    private val dispatcher = Dispatchers.IO


//    suspend fun saveMessage(senderNumber : String?,recieverNumber : String?,MessageContent : String?,sent_at : String?,recieve_at : String?)
////    : NetworkBaseResponse
//    {
//        return withContext(dispatcher) {
//            try {
//                val requestBody: RequestBody = MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("sender_id", senderNumber.toString())
//                    .addFormDataPart("recipient_id", recieverNumber.toString())
//                    .addFormDataPart("message_content", MessageContent.toString())
//                    .addFormDataPart("sent_at", sent_at.toString())
//                    .addFormDataPart("received_at", recieve_at.toString())
//                    .build()
//                apiInterface.saveMessage(requestBody)
//            } catch (exception: Throwable) {
//                NetworkErrorParser.parseNetworkError(exception)
//            }
//        }
//    }

    suspend fun saveMessage(senderNumber : String?,recieverNumber : String?,status : String?,MessageContent : String?,sent_at : String?,recieve_at : String?,filePath : String?): NetworkBaseResponse {
        return withContext(dispatcher) {
            try {
                apiInterface.saveMessage(
                    getStringRequestBody(senderNumber),
                    getStringRequestBody(recieverNumber),
                    getStringRequestBody(status),
                    getStringRequestBody(MessageContent),
                    getStringRequestBody(sent_at),
                    getStringRequestBody(recieve_at),
                    getFile(filePath))
            } catch (exception: Throwable) {
                NetworkErrorParser.parseNetworkError(exception)
            }
        }
    }

    private fun getStringRequestBody(phone: String?): RequestBody? {
        return if (phone == null)
            null
        else
            RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
    }

    private fun getFile(filepath : String?): MultipartBody.Part? {
        var files: MultipartBody.Part? = null

        try {
            var file = File(filepath)
            if (file.exists()) {
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                files =  MultipartBody.Part.createFormData("media", file.name, requestBody)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
        return files
    }

}