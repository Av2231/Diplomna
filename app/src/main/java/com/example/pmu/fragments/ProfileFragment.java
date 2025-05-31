package com.example.pmu.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.interfaces.AddNewProfilePictureListener;
import com.example.pmu.interfaces.UserCommentScoreListener;
import com.example.pmu.interfaces.UserRatingScoreListener;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;

@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends BaseFragment {

    @ViewById
    TextView userFullName;
    @ViewById
    TextView userDob;
    @ViewById
    TextView userEmail;
    @ViewById
    TextView ratedLocations;
    @ViewById
    TextView commentScore;
    @ViewById
    TextView avgRating;
    @ViewById
    TextView signOutText;
    @ViewById
    RelativeLayout signOutLayout;
    @ViewById
    ImageView userProfileImage;

    @Override
    public void onResume() {
        super.onResume();

        userFullName.setText(String.format("%s %s", User.getInstance().getFirstName(), User.getInstance().getLastName()));
        userDob.setText(User.getInstance().getDob());
        userEmail.setText(User.getInstance().getEmail());
        getUserRatingScore(User.getInstance().getId());
        getUserCommentScore(User.getInstance().getId());

        Bitmap bitmap = base64ToBitmap(User.getInstance().getImage());
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
        userProfileImage.setBackground(ob);
    }


    @Click
    void userProfileImage() {
        dispatchTakePictureIntent();
    }

    private void getUserRatingScore(String userId) {
        RequestBuilder.getUserRatingScore(userId, new UserRatingScoreListener() {
            @Override
            public void onSuccess(String average_rate, String rate_score) {
                double rating = Double.parseDouble(average_rate);
                double result = Math.floor(rating * 2) / 2.0;
                avgRating.setText(getString(R.string.avgRating) + ": " + result);
                ratedLocations.setText(getString(R.string.ratedLocations) + ": " + rate_score);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void getUserCommentScore(String userId) {
        RequestBuilder.getUserCommentScore(userId, new UserCommentScoreListener() {
            @Override
            public void onSuccess(String score) {
                commentScore.setText(getString(R.string.comments) + ": " + score);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    @Click
    void signOutLayout() {
        User.getInstance().clearInstance();
        ((MainActivity) getActivity()).resetApplication();
    }


    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startCameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraIntent();
            }
        } else {
            showErrorAlertDialog(getString(R.string.camera_forbidden));
        }
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != 0) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String convertedImageBase64 = bitmapToBase64(imageBitmap);
            User.getInstance().setImage(convertedImageBase64);
            RequestBuilder.newProfilePicture(convertedImageBase64, User.getInstance().getId(), new AddNewProfilePictureListener() {
                @Override
                public void onSuccess() {
                    User.getInstance().setImage(convertedImageBase64);
                }

                @Override
                public void onFailure(String message) {
                    showErrorAlertDialog(message);
                }
            });

        } else {
            showErrorAlertDialog(getString(R.string.camera_failed));
        }
    }
    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public Bitmap base64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}