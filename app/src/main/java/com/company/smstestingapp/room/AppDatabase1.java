package com.company.smstestingapp.room;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {SmsSaved.class}, version = 1, exportSchema = false)
public abstract class AppDatabase1 extends RoomDatabase {

    public abstract SmsDao smsDao();

    private static volatile AppDatabase1 INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase1 getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase1.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase1.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}