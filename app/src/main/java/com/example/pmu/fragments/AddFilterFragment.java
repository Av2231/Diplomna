package com.example.pmu.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.interfaces.DataParserListener;
import com.example.pmu.interfaces.SendDataLocation;
import com.example.pmu.models.PinMarker;
import com.example.pmu.utils.ServerCommunication;
import com.example.pmu.utils.TDate;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@EFragment(com.example.pmu.R.layout.fragment_add_filter)
public class AddFilterFragment extends BaseFragment {

    @ViewById
    Spinner categorySpinner, regionSpinner;
    @ViewById
    TextView dayTextView, fromHoursTextView, toHoursTextView;
    @ViewById
    ProgressBar progressBar;
    @ViewById
    TextView errorWhileSearching;
    private String category, region;
    private Date day;
    private SendDataLocation sendDataLocation;
    private DatePickerDialog datePickerDialog;

    @Click
    void contentLayout() {
        clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] categories = {getString(R.string.category), "Футбол", "Баскетбол", "Волейбол", "Тенис", "Хандбал", "Лека атлетика", "Бокс", "Борба", "Карате", "Плуване", "Гимнастика", "Колездене", "Тенис на маса"};
        String[] regions = {getString(R.string.region), "Лозенец", "Младост", "Люлин", "Студентски град", "Дружба", "Надежда", "Оборище", "Красна поляна", "Овча купел",
                "Слатина", "Сердика", "Витоша", "Илинден", "Подуяне", "Красно село", "Триадица", "Искър", "Средец", "Връбница", "Банкя", "Кремиковци", "Панчарево", "Възраждане"};


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(TDate.getTimeZoneStringForBuildType()));

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                dayTextView.setText(TDate.convertToStringForCurrentLocale(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.done), datePickerDialog);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapterCategory);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterRegion = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, regions) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        adapterRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(adapterRegion);

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void findLocations(String category, String region, String fromDate, String toDate) {
        ServerCommunication.findLocations(category, region, fromDate, toDate, new DataParserListener() {
            @Override
            public void onSuccess(ArrayList<PinMarker> data) {
                if (data.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    errorWhileSearching.setVisibility(View.VISIBLE);
                    errorWhileSearching.setText("There is no data. Please fix your request!");
                } else {
                    sendDataLocation.sendData(data);
                    ((MainActivity) getActivity()).popToHomePageFragment();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(String message) {
                showErrorAlertDialog(message);
            }
        });


    }

    @Click
    void dayTextView() {
        clearFocus();
        datePickerDialog.show();
    }

    @Click
    void fromHoursTextView() {
        clearFocus();
        showTimePickerDialog(fromHoursTextView);
    }

    @Click
    void toHoursTextView() {
        clearFocus();
        showTimePickerDialog(toHoursTextView);
    }

    public void setSendDataLocation(SendDataLocation sendDataLocation) {
        this.sendDataLocation = sendDataLocation;
    }

    private void showTimePickerDialog(TextView targetEditText) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    targetEditText.setText(formattedTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE, getString(R.string.done), timePickerDialog);
        timePickerDialog.show();
    }


    @Click
    void addFilterButton() {

        if (!validateForm()) {
            showValidationDialog(getString(R.string.validation_fail));
            return;
        }

        category = categorySpinner.getSelectedItem().toString();
        region = regionSpinner.getSelectedItem().toString();
        String dayStr = dayTextView.getText().toString();
        String fromHourStr = fromHoursTextView.getText().toString();
        String toHourStr = toHoursTextView.getText().toString();
        String fromDateTimeStr = dayStr + " " + fromHourStr;
        String toDateTimeStr = dayStr + " " + toHourStr;
        progressBar.setVisibility(View.VISIBLE);
        findLocations(category, region, fromDateTimeStr, toDateTimeStr);
    }

    private void showValidationDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }


    private boolean validateForm() {
        String fromTimeStr = fromHoursTextView.getText().toString();
        String toTimeStr = toHoursTextView.getText().toString();

        if (fromTimeStr.isEmpty() || toTimeStr.isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeFormat.setLenient(false);

            Date fromTime = timeFormat.parse(fromTimeStr);
            Date toTime = timeFormat.parse(toTimeStr);

            if (fromTime != null && toTime != null) {
                return fromTime.before(toTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (categorySpinner.getSelectedItem().toString().equals(getString(R.string.category)) ||
                regionSpinner.getSelectedItem().toString().equals(getString(R.string.region))) {
            return false;
        }

        return false;
    }
}