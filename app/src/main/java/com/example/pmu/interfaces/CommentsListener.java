package com.example.pmu.interfaces;

import com.example.pmu.models.CommentModel;

import java.util.ArrayList;

public interface CommentsListener {
    void onSuccess(ArrayList<CommentModel> data);
    void onFailure(String message);
}
