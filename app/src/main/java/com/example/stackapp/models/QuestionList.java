package com.example.stackapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionList {

    @SerializedName("items")
    private List<Question> questionList;

    public QuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }
}
