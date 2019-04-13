package com.example.stackapp.data;

import android.content.Context;

import com.example.stackapp.models.Tag;
import com.example.stackapp.worker.SeedTagsDbWorker;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


@Database(entities = {Tag.class}, version = 1, exportSchema = false)
public abstract class TagDatabase extends RoomDatabase {

    public abstract TagDao tagDao();

    private static TagDatabase instance = null;

    public static TagDatabase getInstance(Context context) {

        if (instance == null) {
            synchronized (TagDatabase.class){
                if(instance == null){
                    instance = buildDatabase(context);
                }

            }

        }
        return instance;
    }

    private static TagDatabase buildDatabase(Context context) {

        return Room.databaseBuilder(context, TagDatabase.class, "tag-suggestions-db")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SeedTagsDbWorker.class).build();
                        WorkManager.getInstance().enqueue(workRequest);
                    }
                }).build();

    }

    public static void destroyDatabase() {
        instance = null;
    }


}
