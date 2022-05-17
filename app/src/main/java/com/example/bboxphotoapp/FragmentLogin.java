package com.example.bboxphotoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentLogin extends Fragment {
    
    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;
    private Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        
        editEmail = view.findViewById(R.id.loginFragEmail);
        editPassword = view.findViewById(R.id.loginFragPassword);
        btnLogin = view.findViewById(R.id.loginFragLoginBtn);
        btnCancel = view.findViewById(R.id.loginFragCancelBtn);
        
        // login button
        btnLogin.setOnClickListener(v -> {
            // login to google
        });
        
        // cancel button; go to MainActivity without logging into google
        btnCancel.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        
        return view;
    }
}