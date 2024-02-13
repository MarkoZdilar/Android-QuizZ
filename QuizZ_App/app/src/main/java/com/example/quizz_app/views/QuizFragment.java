package com.example.quizz_app.views;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quizz_app.R;
import com.example.quizz_app.model.QuestionModel;
import com.example.quizz_app.viewmodel.QuestionViewModel;

import java.util.HashMap;
import java.util.List;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private QuestionViewModel questionViewModel;
    private NavController navController;
    private ProgressBar progressBar;
    private Button option1Btn, option2Btn, option3Btn, nextQuestionBtn;
    private TextView questionTv, answerResponseTv, questionNumberTv, timerCounTv;
    private ImageView closeQuizBtn;
    private String quizId;
    private long numberOfQuestions;
    private int currentQuestionNumber = 0;
    private boolean canAnswer = false;
    private long timer;
    private CountDownTimer countDownTimer;
    private int correctlyAnswered = 0;
    private int wrongAnswered = 0;
    private int notAnswered = 0;
    private String answer = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication())).get(QuestionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        progressBar = view.findViewById(R.id.quizCountProgressBar);
        option1Btn = view.findViewById(R.id.option1btn);
        option2Btn = view.findViewById(R.id.option2btn);
        option3Btn = view.findViewById(R.id.option3btn);
        nextQuestionBtn = view.findViewById(R.id.nextQueBtn);
        questionTv = view.findViewById(R.id.questionTv);
        answerResponseTv = view.findViewById(R.id.ansFeedbackTv);
        questionNumberTv = view.findViewById(R.id.quizQuestionsCount);
        timerCounTv = view.findViewById(R.id.percentageTv);
        closeQuizBtn = view.findViewById(R.id.close_quiz_btn);

        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId(); //Argument manualno dodan
        numberOfQuestions = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestionsCount();
        questionViewModel.setQuizId(quizId);

        questionViewModel.getQuestions();
        questionViewModel.getResults();

        //totalPoints = loadTotalResult(new QuestionRepository());

        option1Btn.setOnClickListener(this);
        option2Btn.setOnClickListener(this);
        option3Btn.setOnClickListener(this);
        nextQuestionBtn.setOnClickListener(this);
        closeQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_fragment_quiz_to_fragment_list);
            }
        });

        loadData();
    }

    private void loadData() {
        enableOptions();
        loadQuestions(1);
    }

    private void enableOptions() {
        option1Btn.setVisibility(View.VISIBLE);
        option2Btn.setVisibility(View.VISIBLE);
        option3Btn.setVisibility(View.VISIBLE);

        //Ne zelimo da je stalno textView za feedback aktivan, iako moze i biti, ali prazan
        option1Btn.setEnabled(true);
        option2Btn.setEnabled(true);
        option3Btn.setEnabled(true);

        answerResponseTv.setVisibility(View.INVISIBLE);
        nextQuestionBtn.setVisibility(View.INVISIBLE);
    }

    private void loadQuestions(int i){
        currentQuestionNumber = i;
        questionViewModel.getQuestionMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuestionModel>>() {
            @Override
            public void onChanged(List<QuestionModel> questionModels) {
                questionTv.setText(String.valueOf(currentQuestionNumber) + ") " + questionModels.get(i-1).getQuestion());
                option1Btn.setText(questionModels.get(i-1).getOption_a());
                option2Btn.setText(questionModels.get(i-1).getOption_b());
                option3Btn.setText(questionModels.get(i-1).getOption_c());
                timer = questionModels.get(i-1).getTimer();

                answer = questionModels.get(i-1).getAnswer();

                questionNumberTv.setText(String.valueOf(currentQuestionNumber));
                startTimer();
            }
        });
        canAnswer = true;
    }

    private void startTimer() {
        timerCounTv.setText(String.valueOf(timer));
        progressBar.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(timer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Updateamo vrime
                timerCounTv.setText(millisUntilFinished / 1000 + "");

                Long percent = millisUntilFinished/(timer*10);
                progressBar.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                canAnswer = false;
                answerResponseTv.setText("Times up!");
                notAnswered++; //Radi ispisa statistike na kraju samo

                showNextBtn();
            }
        }.start();
    }

    private void showNextBtn() {
        if(currentQuestionNumber == numberOfQuestions){
            nextQuestionBtn.setText("FINISH THE QUIZ!");
            nextQuestionBtn.setEnabled(true);
            nextQuestionBtn.setVisibility(View.VISIBLE);
        } else {
            nextQuestionBtn.setVisibility(View.VISIBLE);
            nextQuestionBtn.setEnabled(true);
            answerResponseTv.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.option1btn:
                verifyAnswer(option1Btn);
                break;
            case R.id.option2btn:
                verifyAnswer(option2Btn);
                break;
            case R.id.option3btn:
                verifyAnswer(option3Btn);
                break;
            case R.id.nextQueBtn:
                if(currentQuestionNumber == numberOfQuestions){
                    submitResults();
                } else {
                    currentQuestionNumber++;
                    //Svaki put upit na bazu i loadanje iduceg pitanja
                    loadQuestions(currentQuestionNumber);
                    resetOptions();
                }
                break;
        }
    }

    private void resetOptions() {
        answerResponseTv.setVisibility(View.INVISIBLE);
        nextQuestionBtn.setVisibility(View.INVISIBLE);
        nextQuestionBtn.setEnabled(false);
        option1Btn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_bg));
        option2Btn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_bg));
        option3Btn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_bg));
    }

    private void submitResults() {
        HashMap<String, Object> resultMap = new HashMap<>();

        questionViewModel.setQuizId(quizId);
        questionViewModel.getResults();
        questionViewModel.getResultMutableLiveData().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Long>>() {
            @Override
            public void onChanged(HashMap<String, Long> stringLongHashMap) {

                Long totalPointsFromDatabase = stringLongHashMap.get("totalPoints");
                int newTotalPoints = (correctlyAnswered * 3) - (wrongAnswered * 2);
                if(totalPointsFromDatabase == null || totalPointsFromDatabase < newTotalPoints) {
                    resultMap.put("correct", correctlyAnswered);
                    resultMap.put("wrong", wrongAnswered);
                    resultMap.put("notanswered", notAnswered);

                    resultMap.put("totalPoints", newTotalPoints);

                    questionViewModel.addResults(resultMap);
                }
            }
        });
        //resultMap.put("correct", correctlyAnswered);
        //resultMap.put("wrong", wrongAnswered);
        //resultMap.put("notanswered", notAnswered);

        //resultMap.put("totalPoints", (correctlyAnswered * 3) - (wrongAnswered * 2));

       // questionViewModel.addResults(resultMap);

        QuizFragmentDirections.ActionFragmentQuizToFragmentResult action = QuizFragmentDirections.actionFragmentQuizToFragmentResult();
        action.setQuizId(quizId); //Saljemo ga u Fragment Result
        navController.navigate(action);
    }

    private void verifyAnswer(Button button) {
        if(canAnswer){
            if (answer.equals(button.getText())) {
                button.setBackground(ContextCompat.getDrawable(getContext(), R.color.green));
                correctlyAnswered++;
                answerResponseTv.setText("Correct!");
            } else {
                button.setBackground(ContextCompat.getDrawable(getContext(), R.color.red));
                wrongAnswered++;
                answerResponseTv.setText("Wrong Answer! It's: " + answer);
            }
        }
        canAnswer = false;
        countDownTimer.cancel();
        showNextBtn();
    }
}