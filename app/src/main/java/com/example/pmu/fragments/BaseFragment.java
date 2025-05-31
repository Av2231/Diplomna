package com.example.pmu.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;

public class BaseFragment extends Fragment {

    protected boolean overrideBackPressed;

    public boolean isOverrideBackPressed() {
        return overrideBackPressed;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (this instanceof WelcomeFragment || this instanceof RegisterFragment || this instanceof LoginFragment || this instanceof CommentsFragment) {
            if (!(this instanceof CommentsFragment)) {
                ((MainActivity) getActivity()).getSupportActionBar().hide();
            }
            ((MainActivity) getActivity()).navBar.setVisibility(View.GONE);
        } else {
            ((MainActivity) getActivity()).navBar.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).getSupportActionBar().show();
        }
    }

    public void onBackPressed() {
        //used for back button in children classes
    }

    protected void addFragment(Fragment fragment, boolean animation) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.addFragment(fragment, animation);
        }
    }

    protected void clearFocus() {
        View view = getActivity().getWindow().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    protected View.OnFocusChangeListener onFocusChange() {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                ((EditText) v).setText(((EditText) v).getText().toString().trim());
            }
        };
    }

    protected void showErrorAlertDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.error);
        dialog.setMessage(message);
        dialog.setNeutralButton(R.string.close, null);
        dialog.show();
    }

    protected void setActionBarTitle(String string) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setActionBarTitle(string);
        }
    }

    protected void addFragment(Fragment fragment) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.addFragment(fragment);
        }
    }

}
