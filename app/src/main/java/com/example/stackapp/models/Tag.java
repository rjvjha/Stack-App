package com.example.stackapp.models;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "tags")
public class Tag {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private int tagId;


    @ColumnInfo(name="name")
    @SerializedName("name")
    private String name;

    public Tag(int tagId, String name) {
        this.tagId = tagId;
        this.name = name;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
