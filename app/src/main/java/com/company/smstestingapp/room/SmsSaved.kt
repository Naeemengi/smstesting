package com.company.smstestingapp.room


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "SmsSaved")
class SmsSaved : Serializable {
    /*
       * Getters and Setters
       * */
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "smsID")
    var smsID: String? = null

    @ColumnInfo(name = "message")
    var message: String? = null
}