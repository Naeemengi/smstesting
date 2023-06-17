package com.company.smstestingapp;


import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class smsObserver extends ContentObserver {

    private String lastSmsId;
    private Context mContext;

    public smsObserver(Handler handler) {
        super(handler);
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Uri uriSMSURI = Uri.parse("content://sms/sent");
        Cursor cur = mContext.getContentResolver().query(uriSMSURI, null, null, null, null);
        cur.moveToNext();
        String id = cur.getString(cur.getColumnIndex("_id"));
            String address = cur.getString(cur.getColumnIndex("address"));
            // Optional: Check for a specific sender
                String message = cur.getString(cur.getColumnIndex("body"));
                // Use message content for desired functionality

        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}