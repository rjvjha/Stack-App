package com.example.stackapp.network;

import com.example.stackapp.models.Question;
import com.example.stackapp.models.QuestionList;
import com.example.stackapp.models.Tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RemoteCallsInterface {


    // API Endpoint for questions
    @GET("/questions?order=desc")
    Call<QuestionList> getQuestions(@Query("sort") String sort,
                                    @Query("tagged") String tag,
                                    @Query("site") String site
    );
}
