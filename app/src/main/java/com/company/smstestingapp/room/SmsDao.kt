package com.company.smstestingapp.room

import androidx.room.*


@Dao
public interface SmsDao {
    @get:Query("SELECT * FROM SmsSaved")
    val all: List<SmsSaved?>?

    @Insert
    fun insert(task: SmsSaved?)

    @Delete
    fun delete(task: SmsSaved?)

    @Update
    fun update(task: SmsSaved?)

    @Query("SELECT EXISTS(SELECT * FROM SmsSaved WHERE smsID = :id) ")
    fun isExists(id : String): Boolean
}
