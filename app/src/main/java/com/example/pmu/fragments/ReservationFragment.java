package com.example.pmu.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.adapters.ReservationAdapter;
import com.example.pmu.models.LocationModel;
import com.example.pmu.models.User;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.fragments_reservations)
public class ReservationFragment extends BaseFragment {
    private ArrayList<LocationModel> reservations;

    public ArrayList<LocationModel> getReservations() {
        return reservations;
    }

    public void setReservations(ArrayList<LocationModel> reservations) {
        this.reservations = reservations;
    }

    @ViewById
    TextView emptyTextView;

    @ViewById
    ListView listView;


    @Override
    public void onResume(){
        super.onResume();

        if (reservations.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        ReservationAdapter adapter = new ReservationAdapter(getActivity(), reservations);
        listView.setAdapter(adapter);
    }
}
