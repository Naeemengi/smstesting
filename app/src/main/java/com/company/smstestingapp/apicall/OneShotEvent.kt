package com.company.smstestingapp.apicall

class OneShotEvent<T>(private val data: T?) {
    var isDataUsed = false

    fun getData(): T?{
        return if(!isDataUsed){
            isDataUsed = true
            data
        }else
            null
    }
}