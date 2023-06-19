package com.company.smstestingapp;

import android.database.ContentObserver;
import android.os.Handler;

public class MMSContentObserver extends ContentObserver {

    public MMSContentObserver(Handler h) {
        super(h);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }
}
