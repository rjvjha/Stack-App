package com.example.stackapp.network;

import com.example.stackapp.models.AnswerList;
import com.example.stackapp.models.QuestionList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RemoteCallsInterface {


    // API Endpoint for questions
    @GET("/questions?order=desc")
    Call<QuestionList> getQuestions(@Query("sort") String sort,
                                    @Query("tagged") String tag,
                                    @Query("site") String site
    );

    // API Endpoint for Answers
    @GET("/questions/{id}/answers?order=desc&sort=votes&site=stackoverflow")
    Call<AnswerList> getAnswers(@Path("id") int id);
}
