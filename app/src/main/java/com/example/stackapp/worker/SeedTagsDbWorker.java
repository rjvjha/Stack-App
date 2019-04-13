package com.example.stackapp.worker;

import android.content.Context;

import com.example.stackapp.data.TagDatabase;
import com.example.stackapp.models.Tag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SeedTagsDbWorker extends Worker {


    public SeedTagsDbWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            InputStream inputStream = getApplicationContext().getAssets().open("tags.json");
            int size = inputStream.available();

            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            List<Tag> tagList = new Gson().fromJson(json, new TypeToken<List<Tag>>() {
            }.getType());
            TagDatabase tagDatabase = TagDatabase.getInstance(getApplicationContext());
            tagDatabase.tagDao().insertAll(tagList);
            return Result.success();


        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();

        }
    }
}
