package com.example.quizz_app.model;

import com.example.quizz_app.views.ResultFragmentArgs;
import com.google.firebase.firestore.DocumentId;

public class ResultModel {
    @DocumentId
    private String resultId;

    private String totalPoints, correct, wrong, notanswered;

    public ResultModel(){}

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getWrong() {
        return wrong;
    }

    public void setWrong(String wrong) {
        this.wrong = wrong;
    }

    public String getNotanswered() {
        return notanswered;
    }

    public void setNotanswered(String notanswered) {
        this.notanswered = notanswered;
    }

    public ResultModel(String resultId, String totalPoints, String correct, String wrong, String notanswered) {
        this.resultId = resultId;
        this.totalPoints = totalPoints;
        this.correct = correct;
        this.wrong = wrong;
        this.notanswered = notanswered;
    }
}
