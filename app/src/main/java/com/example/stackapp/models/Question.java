package com.example.stackapp.models;

import com.example.stackapp.utils.Converters;

import androidx.room.TypeConverters;

// POJO Class for Question
@TypeConverters(Converters.class)
public class Question {

    private Owner owner;

    private String link;

    private String last_activity_date;

    private String creation_date;

    private String answer_count;

    private String title;

    private String question_id;

    private String score;

    private String is_answered;

    private String view_count;

    private String last_edit_date;

    public Question(Owner owner, String link, String last_activity_date, String creation_date,
                    String answer_count, String title, String question_id, String score,
                    String is_answered, String view_count, String last_edit_date) {
        this.owner = owner;
        this.link = link;
        this.last_activity_date = last_activity_date;
        this.creation_date = creation_date;
        this.answer_count = answer_count;
        this.title = title;
        this.question_id = question_id;
        this.score = score;
        this.is_answered = is_answered;
        this.view_count = view_count;
        this.last_edit_date = last_edit_date;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLast_activity_date() {
        return last_activity_date;
    }

    public void setLast_activity_date(String last_activity_date) {
        this.last_activity_date = last_activity_date;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getAnswer_count() {
        return answer_count;
    }

    public void setAnswer_count(String answer_count) {
        this.answer_count = answer_count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }


    public String getIs_answered() {
        return is_answered;
    }

    public void setIs_answered(String is_answered) {
        this.is_answered = is_answered;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getLast_edit_date() {
        return last_edit_date;
    }

    public void setLast_edit_date(String last_edit_date) {
        this.last_edit_date = last_edit_date;
    }

    @Override
    public String toString() {
        return "ClassPojo [owner = " + owner + ", link = " + link + ", last_activity_date = " + last_activity_date + ", creation_date = " + creation_date + ", answer_count = " + answer_count + ", title = " + title + ", question_id = " + question_id + ", score = " + score + ", is_answered = " + is_answered + ", view_count = " + view_count + ", last_edit_date = " + last_edit_date + "]";
    }
}
