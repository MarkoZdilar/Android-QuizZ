package com.example.quizz_app.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quizz_app.R;
import com.example.quizz_app.model.QuizListModel;
import com.example.quizz_app.viewmodel.QuestionViewModel;
import com.example.quizz_app.viewmodel.QuizListViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class DetailFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ImageView detailsImage;
    private TextView title, difficulty, questions, bestTotalPoints;
    private Button startQuizBtn;
    private NavController navController;
    private QuizListViewModel quizListViewModel;
    private QuestionViewModel questionViewModel;
    private int position;
    private ProgressBar progressBar;
    private String quizId;
    private long totalQuestionsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizListViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication())).get(QuizListViewModel.class);

        questionViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication())).get(QuestionViewModel.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailsImage = view.findViewById(R.id.detailFragmentImage);
        title = view.findViewById(R.id.detailFragmentTitle);
        difficulty = view.findViewById(R.id.detailFragmentDifficulty);
        questions = view.findViewById(R.id.detailFragmentTotalQuestions);
        startQuizBtn = view.findViewById(R.id.startQuizBtn);
        progressBar = view.findViewById(R.id.detailProgressBar);
        bestTotalPoints = view.findViewById(R.id.bestScoreTv);

        navController = Navigation.findNavController(view);

        position = DetailFragmentArgs.fromBundle(getArguments()).getPosition();

        quizListViewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                //Here we have all quizListModels data, but we are interested in data from position that is clicked on
                QuizListModel quiz = quizListModels.get(position);
                title.setText(quiz.getTitle());
                difficulty.setText(quiz.getDifficulty());
                questions.setText(String.valueOf(quiz.getQuestions()));
                Glide.with(view).load(quiz.getImage()).into(detailsImage);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);//Progress bar will be updated every 2 seconds (But first time is important)

                totalQuestionsCount = quiz.getQuestions();
                quizId = quiz.getQuizId();

                //New
                questionViewModel.setQuizId(quizId);
                questionViewModel.getResults();
                questionViewModel.getResultMutableLiveData().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Long>>() {
                    @Override
                    public void onChanged(HashMap<String, Long> stringLongHashMap) {

                        Long totalPoints = stringLongHashMap.get("totalPoints");
                        if(totalPoints != null) {
                            bestTotalPoints.setText(totalPoints.toString());
                        } else {
                            bestTotalPoints.setText("Not played yet!");
                        }
                    }
                });

                //To here
                difficulty = view.findViewById(R.id.detailFragmentDifficulty);
            }
        });

        startQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Just navigate from details fragment to fragment_quiz
                //Value that will be passed to next fragment is manually added inside nav_graph as parameter
                //Values are - quizId, totalQuestionsCount
                DetailFragmentDirections.ActionFragmentDetailsToFragmentQuiz action = DetailFragmentDirections.actionFragmentDetailsToFragmentQuiz();

                action.setQuizId(quizId);
                action.setTotalQuestionsCount(totalQuestionsCount);
                navController.navigate(action);
            }
        });
    }
}