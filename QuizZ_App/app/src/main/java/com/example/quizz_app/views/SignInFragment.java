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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizz_app.R;
import com.example.quizz_app.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

public class SignInFragment extends Fragment {
    private AuthViewModel viewModel;
    private NavController navController;

    private EditText editEmail, editPassword;
    private TextView signInText;
    private Button signInBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        navController = Navigation.findNavController(view);
        editEmail = view.findViewById(R.id.editEmailSignIn);
        editPassword = view.findViewById(R.id.editPasswordSignIn);
        signInText = view.findViewById(R.id.signInText);
        signInBtn = view.findViewById(R.id.signInButton);

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_fragment_sign_in_to_fragment_sign_up);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if(!password.isEmpty() && !email.isEmpty()){
                    viewModel.signIn(email,password);
                    viewModel.getFirebaseUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
                        @Override
                        public void onChanged(FirebaseUser firebaseUser) { //Ako je user registriran i sada postoje podaci unutar firebase-a, onda idi na sign-in page
                            if(firebaseUser != null){
                                navController.navigate(R.id.action_fragment_sign_in_to_fragment_list);
                            }
                        }
                    });
                } else{
                    Toast.makeText(getContext(), "Please enter both, Email and Password.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                getActivity().getApplication())).get(AuthViewModel.class);


    }
}