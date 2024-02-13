package com.example.quizz_app.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.quizz_app.model.QuestionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class QuestionRepository {

    private FirebaseFirestore firebaseFirestore;
    private String quizId;
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private HashMap<String, Long> resultMap = new HashMap<>();
    private OnQuestionLoad onQuestionLoad;
    private OnResultAdded onResultAdded;
    private OnResultLoad onResultLoad;

    public void addResults(HashMap<String, Object> resultMap) {
        firebaseFirestore.collection("Quiz").document(quizId)
                .collection("results").document(currentUserId)
                .set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    onResultAdded.onSubmit();
                } else {
                    onResultAdded.onError(task.getException());
                }
            }
        });//Dodaje collection kao sto se i rucno dodavalo
    }

    public void getResults() {
        firebaseFirestore.collection("Quiz").document(quizId)
                .collection("results").document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    resultMap.put("correct", task.getResult().getLong("correct"));
                    resultMap.put("wrong", task.getResult().getLong("wrong"));
                    resultMap.put("notanswered", task.getResult().getLong("notanswered"));
                    resultMap.put("totalPoints", task.getResult().getLong("totalPoints"));
                    onResultLoad.onResultLoad(resultMap);
                } else {
                    onResultLoad.onError(task.getException());
                }
            }
        });
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public QuestionRepository(OnQuestionLoad onQuestionLoad, OnResultAdded onResultAdded, OnResultLoad onResultLoad){
        firebaseFirestore = FirebaseFirestore.getInstance();
        this.onQuestionLoad = onQuestionLoad;
        this.onResultAdded = onResultAdded;
        this.onResultLoad = onResultLoad;
    }

    public void getQuestions(){
        firebaseFirestore.collection("Quiz").document(quizId)
                .collection("questions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    onQuestionLoad.onLoad(task.getResult().toObjects(QuestionModel.class));
                } else{
                    onQuestionLoad.onError(task.getException());
                }
            }
        });
    }

    public interface OnQuestionLoad{ //Metode iz ovog interface-a su implementirane unutar ViewModel-a
        void onLoad(List<QuestionModel> questionModels);
        void onError(Exception e);
    }

    public interface OnResultAdded{
        boolean onSubmit();
        void onError(Exception e);
    }

    public interface OnResultLoad{
        void onResultLoad(HashMap<String, Long> resultMap);
        void onError(Exception e);
    }
}
