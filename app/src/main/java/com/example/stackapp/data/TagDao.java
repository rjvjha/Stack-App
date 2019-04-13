package com.example.stackapp.data;


import com.example.stackapp.models.Tag;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TagDao {


    @Query(" Select * From tags")
    List<Tag> getAllTags();

    @Query("Delete From tags")
    void deleteAll();

    // inserts a row
    @Insert(onConflict = REPLACE)
    void insert(Tag tag);


    // deletes a row
    @Delete
    void delete(Tag tag);

    //inserts tags
    @Insert(onConflict = REPLACE)
    void insertAll(List<Tag> tagList);


}
