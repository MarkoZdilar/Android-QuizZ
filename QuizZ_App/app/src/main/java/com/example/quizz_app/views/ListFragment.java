package com.example.quizz_app.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quizz_app.R;
import com.example.quizz_app.adapter.QuizListAdapter;
import com.example.quizz_app.model.QuizListModel;
import com.example.quizz_app.viewmodel.AuthViewModel;
import com.example.quizz_app.viewmodel.QuizListViewModel;

import java.util.List;

public class ListFragment extends Fragment implements QuizListAdapter.OnItemClickedListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NavController navController;
    private QuizListViewModel quizListViewModel;
    private QuizListAdapter adapter;
    private AuthViewModel viewModel;
    private Button signOutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizListViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication())).get(QuizListViewModel.class);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication())).get(AuthViewModel.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.listQuizRecyclerView);
        progressBar = view.findViewById(R.id.quizListProgressBar);
        navController = Navigation.findNavController(view);
        signOutBtn = view.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.signOut();
                Toast.makeText(getContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_fragment_list_to_fragment_sign_in);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new QuizListAdapter(this);

        recyclerView.setAdapter(adapter);

        quizListViewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                progressBar.setVisibility(View.GONE);
                adapter.setQuizListModels(quizListModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        ListFragmentDirections.ActionFragmentListToFragmentDetails action = ListFragmentDirections.actionFragmentListToFragmentDetails(); //Passing position with safe args (check nav_args screenshot)

        action.setPosition(position);
        navController.navigate(action);
    }
}