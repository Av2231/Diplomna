package com.example.pmu.fragments;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.pmu.R;
import com.example.pmu.interfaces.LoginAndRegisterListener;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_login)
public class LoginFragment extends BaseFragment {

    @ViewById
    TextView registerTextView;
    @ViewById
    EditText emailEditText;
    @ViewById
    EditText passwordEditText;
    @ViewById
    Button loginButton;
    @ViewById
    TextView errorWhileLogging;
    String email, password;
    @ViewById
    ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();
        setTextValue();

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                addFragment(new RegisterFragment_());
            }
        });
        emailEditText.setOnFocusChangeListener(onFocusChange());
        passwordEditText.setOnFocusChangeListener(onFocusChange());
    }

    @Click
    void loginButton() {
        emailEditText.setOnFocusChangeListener(onFocusChange());
        passwordEditText.setOnFocusChangeListener(onFocusChange());

        progressBar.setVisibility(View.VISIBLE);
        errorWhileLogging.setVisibility(View.VISIBLE);
        email = String.valueOf(emailEditText.getText());
        password = String.valueOf(passwordEditText.getText());

        RequestBuilder.login(email, password, new LoginAndRegisterListener() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.INVISIBLE);
                errorWhileLogging.setVisibility(View.INVISIBLE);
                addFragment((((MainActivity) getActivity()).homePageFragment));
            }

            @Override
            public void onFailure(String message) {
                progressBar.setVisibility(View.INVISIBLE);
                errorWhileLogging.setVisibility(View.VISIBLE);
                errorWhileLogging.setText(message);
            }

            @Override
            public void onErrorResponse(String message) {
                progressBar.setVisibility(View.INVISIBLE);
                errorWhileLogging.setVisibility(View.VISIBLE);
                errorWhileLogging.setText(message);
            }
        });


    }

    @Click
    void contentLayout() {
        clearFocus();
    }

    public void setTextValue() {
        SpannableString string = new SpannableString(getString(R.string.register));
        string.setSpan(new URLSpan(""), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerTextView.setText(string);
    }
}
