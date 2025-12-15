package com.example.focusflowperson2;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Session.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SessionDao sessionDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "focusflow_database")
                            .allowMainThreadQueries() // For simplicity in this project. Ideally use Async.
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}