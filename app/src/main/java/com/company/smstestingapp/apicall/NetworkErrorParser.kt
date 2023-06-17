package com.company.smstestingapp.apicall





import retrofit2.HttpException
import java.io.IOException

object NetworkErrorParser {
    fun parseNetworkError(exception: Throwable): NetworkBaseResponse {
        var networkBaseResponse = NetworkBaseResponse()
        when(exception){
            is IOException ->{
                networkBaseResponse.apply {
                    statusCode = "404"
                    message = "Please check your internet connection."
                }
            }
            is HttpException ->{
                if (exception.code() == 401)
                {
                    networkBaseResponse.apply {
                        statusCode = "401"
                        message = "Unauthorized."
                    }

                }else {
                    networkBaseResponse.apply {
                        statusCode = "500"
                        message = "Internal Server error."
                    }
                }

            }else ->{
            networkBaseResponse.apply {
                statusCode = "0"
                message =exception.message
            }
        }
        }
        return networkBaseResponse
    }
}