package com.example.pmu.fragments;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_welcome)
public class WelcomeFragment extends BaseFragment {
    @Click
    void getStartedButton(){
        addFragment(new LoginFragment_());
    }

    @Override
    public void onBackPressed(){
        getActivity().finish();
    }
}
