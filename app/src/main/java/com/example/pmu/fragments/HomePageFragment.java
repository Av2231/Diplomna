package com.example.pmu.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.pmu.R;
import com.example.pmu.adapters.CustomInfoWindowAdapter;
import com.example.pmu.interfaces.SendDataLocation;
import com.example.pmu.models.PinMarker;
import com.example.pmu.utils.AppService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.Objects;

@EFragment(R.layout.fragment_homepage)
public class HomePageFragment extends BaseFragment implements OnMapReadyCallback, SendDataLocation {
    private ArrayList<PinMarker> simpleDataMap;
    private GoogleMap mapG;


    @Override
    public void onCreate(Bundle savedInstanecs) {
        super.onCreate(savedInstanecs);
        simpleDataMap = new ArrayList<>();
        overrideBackPressed = true;
    }

    @Override
    public void sendData(ArrayList<PinMarker> locations) {
        simpleDataMap.addAll(locations);
    }

    @Override
    public void onResume() {
        super.onResume();
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if ((ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapG = googleMap;
        mapG.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(new LatLng(41.300574, 22.226498), new LatLng(44.164594, 28.729199)), 0));
        if ((ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mapG.setMyLocationEnabled(true);
        }
        mapG.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(requireContext())));
        if (!AppService.getInstance().getSavedMap().isEmpty()) {
            mapG.setMapType(Integer.parseInt(AppService.getInstance().getSavedMap()));
        }mapG.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                for (PinMarker pinMarker : simpleDataMap) {
                    if (Objects.equals(pinMarker.getTitle(), marker.getTitle()) && Objects.equals(pinMarker.getLocation(), marker.getSnippet())) {
                        DetailsFragment_ detailsFragment = new DetailsFragment_();
                        detailsFragment.setMarker(pinMarker);
                        addFragment(detailsFragment);
                    }
                }
            }
        });
        if (!simpleDataMap.isEmpty()) {
            for (PinMarker marker : simpleDataMap) {
                LatLng exactLocation = new LatLng(marker.getY(), marker.getX());
                mapG.addMarker(new MarkerOptions().position(exactLocation).title(marker.getTitle()).snippet(marker.getLocation()));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (permissions.length > 0) {
            if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[0])) {
                if ((grantResults[0] != PackageManager.PERMISSION_GRANTED && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) || (grantResults[1] != PackageManager.PERMISSION_GRANTED && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))) {
                    showAlertFoLocationPermission();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
            Fragment fragment = fm.findFragmentByTag(fm.getBackStackEntryAt(i).getName());

            if (fragment != null) {
                if (fragment instanceof HomePageFragment_) {
                    getActivity().getSupportFragmentManager().popBackStack(HomePageFragment_.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    getActivity().getSupportFragmentManager().popBackStack(RegisterFragment_.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        }
    }

    private void showAlertFoLocationPermission() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.information);
        dialog.setCancelable(false);
        dialog.setMessage(R.string.locations_permission_deny);
        dialog.setNeutralButton(R.string.cancel, null);
        dialog.setPositiveButton(R.string.settings, (dialogInterface, i) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
            startActivity(intent);
        });
        dialog.show();
    }

    public void changeMapView(int mapViewType) {
        if (mapG != null) {
            mapG.setMapType(mapViewType);
        }
    }
}