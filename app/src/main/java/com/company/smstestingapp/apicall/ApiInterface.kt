package com.company.smstestingapp.apicall

import com.company.smstestingapp.apicall.NetworkBaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.http.*

interface ApiInterface {

    @Multipart
    @POST("savemsg")
    suspend fun saveMessage(@Part("sender_id") sender_id: RequestBody? ,@Part("recipient_id") userId: RequestBody?,
                            @Part("message_content") message_content: RequestBody?, @Part("sent_at") sent_at: RequestBody?,
                            @Part("received_at") received_at: RequestBody?, @Part  file: MultipartBody.Part?): NetworkBaseResponse


}