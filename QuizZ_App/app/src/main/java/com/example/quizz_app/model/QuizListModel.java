package com.example.quizz_app.model;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel {

    @DocumentId
    private String quizId;
    private String Title, Image, Difficulty;
    private long Questions;

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
    }

    public long getQuestions() {
        return Questions;
    }

    public void setQuestions(long questions) {
        Questions = questions;
    }

    public QuizListModel(){}

    public QuizListModel(String quizId, String title, String image, String difficulty, long questions) {
        this.quizId = quizId;
        Title = title;
        Image = image;
        Difficulty = difficulty;
        Questions = questions;
    }
}
