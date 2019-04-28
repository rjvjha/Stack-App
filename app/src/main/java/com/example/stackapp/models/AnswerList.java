package com.example.stackapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnswerList {


    @SerializedName("items")
    List<Answer> answerList;

    public AnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }
}
