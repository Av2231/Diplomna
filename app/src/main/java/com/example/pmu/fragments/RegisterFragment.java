package com.example.pmu.fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.interfaces.LoginAndRegisterListener;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import com.example.pmu.utils.TDate;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EFragment(R.layout.fragment_register)
public class RegisterFragment extends BaseFragment {

    @ViewById
    EditText firstNameEditText;
    @ViewById
    EditText lastNameEditText;
    @ViewById
    EditText emailEditText;
    @ViewById
    EditText confirmPasswordEditText;
    @ViewById
    EditText passwordEditText;
    @ViewById
    TextView dobDatePicker;
    @ViewById
    Button registerButton;
    @ViewById
    TextView errorWhileRegistering;
    @ViewById
    ProgressBar progressBar;

    private DatePickerDialog datePickerDialog;
    private String firstName, lastName, email, confirmPassword, password, dob;

    @Click
    void contentLayout() {
        clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();

        firstNameEditText.setOnFocusChangeListener(onFocusChange());
        lastNameEditText.setOnFocusChangeListener(onFocusChange());
        emailEditText.setOnFocusChangeListener(onFocusChange());
        passwordEditText.setOnFocusChangeListener(onFocusChange());
        confirmPasswordEditText.setOnFocusChangeListener(onFocusChange());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(TDate.getTimeZoneStringForBuildType()));
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                dobDatePicker.setText(TDate.convertToStringForCurrentLocale(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.done), datePickerDialog);
    }

    @Click
    void registerButton() {
        progressBar.setVisibility(View.VISIBLE);
        firstName = String.valueOf(firstNameEditText.getText());
        lastName = String.valueOf(lastNameEditText.getText());
        email = String.valueOf(emailEditText.getText());
        confirmPassword = String.valueOf(confirmPasswordEditText.getText());
        password = String.valueOf(passwordEditText.getText());
        dob = String.valueOf(dobDatePicker.getText());
        String isDeleted = "false";
        String isConfirmed = "false";
        String role = "User";
        Bitmap imageBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.big_user_alt);
        String img = bitmapToBase64(imageBitmap);
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setDob(dob);
        user.setIsConfirmed(isConfirmed);
        user.setIsDeleted(isDeleted);
        user.setRole(role);
        user.setImage(img);
        if (password.equals(confirmPassword)) {
            if (isValid()) {
                RequestBuilder.register(user, new LoginAndRegisterListener() {
                    @Override
                    public void onSuccess() {
                        RequestBuilder.login(user.getEmail(), user.getPassword(), new LoginAndRegisterListener() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                                addFragment(((MainActivity) requireActivity()).homePageFragment);
                            }

                            @Override
                            public void onFailure(String message) {
                                progressBar.setVisibility(View.GONE);
                                errorWhileRegistering.setVisibility(View.VISIBLE);
                                errorWhileRegistering.setText(message);
                            }

                            @Override
                            public void onErrorResponse(String message) {
                                progressBar.setVisibility(View.GONE);
                                errorWhileRegistering.setVisibility(View.VISIBLE);
                                errorWhileRegistering.setText(message);
                            }
                        });

                    }


                    @Override
                    public void onFailure(String message) {
                        progressBar.setVisibility(View.GONE);
                        errorWhileRegistering.setVisibility(View.VISIBLE);
                        errorWhileRegistering.setText(message);
                    }

                    @Override
                    public void onErrorResponse(String message) {
                        progressBar.setVisibility(View.GONE);
                        errorWhileRegistering.setVisibility(View.VISIBLE);
                        errorWhileRegistering.setText(message);
                    }
                });
            }

        } else {
            errorWhileRegistering.setText(R.string.register_error);
            errorWhileRegistering.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Click
    void dobDatePicker() {
        clearFocus();
        datePickerDialog.show();
    }

    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Boolean isValid() {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (firstName.length() <= 6 || lastName.length() <= 6 || !matcher.matches()) {
            showErrorAlertDialog(getString(R.string.invalid_registration));
            return false;
        }

        return true;
    }
}

