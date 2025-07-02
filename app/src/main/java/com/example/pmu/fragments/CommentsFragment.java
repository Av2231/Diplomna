package com.example.pmu.fragments;

import static java.lang.Integer.parseInt;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.adapters.CommentsAdapter;
import com.example.pmu.interfaces.CommentsListener;
import com.example.pmu.interfaces.NewCommentListener;
import com.example.pmu.models.CommentModel;
import com.example.pmu.models.User;
import com.example.pmu.utils.ServerCommunication;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.fragment_comments)
public class CommentsFragment extends BaseFragment {


    private String locationId;
    private ArrayList<CommentModel> result = new ArrayList<>();
    @ViewById
    ListView listView;
    @ViewById
    ImageView sendComment;
    @ViewById
    EditText writeCommentEdit;

    @Override
    public void onResume(){
        super.onResume();
       getComments(locationId);
        sendComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment = writeCommentEdit.getText().toString();
                addNewComment(locationId,String.valueOf(User.getInstance().getId()),comment);
                ((MainActivity) requireActivity()).hideSoftKeyboard();
                writeCommentEdit.setText("");
            }
        });
    }

    private void addNewComment(String location_id,String userId,String comment){
        ServerCommunication.addNewComment(location_id, userId, comment, new NewCommentListener() {
            @Override
            public void onSuccess() {
                getComments(location_id);
            }
            @Override
            public void onFailure(String message) {

            }
        });
    }
    private void getComments(String location_id){
        ServerCommunication.getAllComments(location_id, new CommentsListener() {
            @Override
            public void onSuccess(ArrayList<CommentModel> data) {
                result = data;
                listView.setAdapter(new CommentsAdapter(getActivity(), result));
            }
            @Override
            public void onFailure(String message) {
                showErrorAlertDialog(message);
            }
        });
    }
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }



}
