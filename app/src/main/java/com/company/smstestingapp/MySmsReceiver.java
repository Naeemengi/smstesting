package com.company.smstestingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.company.smstestingapp.apicall.SaveMessageWorker;

import java.util.Calendar;

import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class MySmsReceiver extends BroadcastReceiver {
    private static final String TAG =
            MySmsReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM =
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Build the message to show.
                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                strMessage += " :" + msgs[i].getMessageBody() + "\n";
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);

                String myNumber = sh.getString("myNumber", "000");

                Data.Builder dataToSend =new Data.Builder()
                        .putString(SaveMessageWorker.SENDER_PHONE_NUMBER, msgs[i].getOriginatingAddress())
                        .putString(SaveMessageWorker.RECIEVER_PHONE_NUMBER,myNumber );
                    dataToSend.putString(SaveMessageWorker.MESSAGE_CONTENT, msgs[i].getMessageBody())
                            .putString(SaveMessageWorker.FILE_PATH, null);


                dataToSend.putString(SaveMessageWorker.SENT_AT, Calendar.getInstance().getTime().toString());
                dataToSend.putString(
                        SaveMessageWorker.RECIEVE_AT,
                        Calendar.getInstance().getTime().toString()
                );

                Constraints createPostConstraints =
                       new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
                WorkRequest saveNumberWorkRequest =new OneTimeWorkRequest.Builder(SaveMessageWorker.class).setConstraints(createPostConstraints).setInputData(dataToSend.build()).build();

                WorkManager.getInstance(context).enqueue(saveNumberWorkRequest);

                // Log and display the SMS message.
                Log.d(TAG, "onReceive: " + strMessage);
//                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}