package com.example.cmps297nmedicationreminder.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.cmps297nmedicationreminder.MainActivity;
import com.example.cmps297nmedicationreminder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;


public class accountFragment extends Fragment {

    //Initialization of Views and Button

    private NotificationsViewModel notificationsViewModel;
    TextView userName, userEmail;
    Button logout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Loading Views Content
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        logout = root.findViewById(R.id.logout_btn);
        userName = root.findViewById(R.id.userName);
        userEmail = root.findViewById(R.id.userEmail);

        //Add Username and Email if Account successfully logged in
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        if(signInAccount != null){
            userName.setText(signInAccount.getDisplayName());
            userEmail.setText("Email: "+signInAccount.getEmail());

        }

        //Logout button Event Listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });


        return root;
    }

}