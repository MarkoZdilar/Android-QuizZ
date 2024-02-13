package com.example.quizz_app.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quizz_app.R;
import com.example.quizz_app.viewmodel.QuestionViewModel;

import java.util.HashMap;

public class ResultFragment extends Fragment {
    private NavController navController;
    private QuestionViewModel questionViewModel;
    private TextView correctAnswerTv , wrongAnswerTv , notAnsweredTv;
    private TextView percentTv;
    private TextView totalPointsTv;
    private ProgressBar scoreProgressbar;
    private String quizId;
    private Button homeBtn;

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
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        correctAnswerTv = view.findViewById(R.id.correctAnswerTv);
        wrongAnswerTv = view.findViewById(R.id.wrongAnswerTv);
        notAnsweredTv = view.findViewById(R.id.notAnsweredTv);
        percentTv = view.findViewById(R.id.percentageTv);
        scoreProgressbar = view.findViewById(R.id.resultCountProgressBar);
        homeBtn = view.findViewById(R.id.homeBtn);
        totalPointsTv = view.findViewById(R.id.totalPointsTv);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_fragment_result_to_fragment_list);
            }
        });

        quizId = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();

        questionViewModel.setQuizId(quizId);
        questionViewModel.getResults();
        questionViewModel.getResultMutableLiveData().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Long>>() {
            @Override
            public void onChanged(HashMap<String, Long> stringLongHashMap) {
                Long correct = stringLongHashMap.get("correct");
                Long wrong = stringLongHashMap.get("wrong");
                Long notAnswered = stringLongHashMap.get("notanswered");
                Long totalPoints = stringLongHashMap.get("totalPoints");

                correctAnswerTv.setText(correct.toString());
                wrongAnswerTv.setText(wrong.toString());
                notAnsweredTv.setText(notAnswered.toString());
                totalPointsTv.setText(totalPoints.toString());

                Long total = correct + wrong + notAnswered;
                Long percent = (correct*100)/total;

                percentTv.setText(String.valueOf(percent) + "%");
                scoreProgressbar.setProgress(percent.intValue());
            }
        });
    }
}