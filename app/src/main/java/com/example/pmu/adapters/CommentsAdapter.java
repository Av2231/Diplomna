package com.example.pmu.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.models.CommentModel;
import com.example.pmu.models.User;

import java.util.ArrayList;

public class CommentsAdapter extends ArrayAdapter<CommentModel> {
    private ArrayList<CommentModel> items;

    public CommentsAdapter(Context context, ArrayList<CommentModel> items) {
        super(context, R.layout.row_comment);
        this.items = items;
    }

    @Override
    public int getCount() {
        if (items.size() > 0) {
            return items.size();
        } else {
            return 1;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ItemViewHolder holder;
        if (row == null || !(row.getTag() instanceof ItemViewHolder)) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.row_comment, parent, false);

            holder = new ItemViewHolder();
            holder.userNameTextView = row.findViewById(R.id.userNameTextView);
            holder.commentTextView = row.findViewById(R.id.commentTextView);
            holder.profilePicImageView = row.findViewById(R.id.profilePic);

            row.setTag(holder);
        }
        holder = (ItemViewHolder) row.getTag();
        CommentModel comment = items.get(position);


        holder.userNameTextView.setText(comment.getUser());
        holder.commentTextView.setText(comment.getComment());
        Bitmap bitmap = base64ToBitmap(comment.getProfilePic());
        BitmapDrawable ob = new BitmapDrawable(bitmap);
        holder.profilePicImageView.setBackground(ob);
        return row;
    }


    public void updateItems(ArrayList<CommentModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private static class ItemViewHolder {
        TextView userNameTextView;
        TextView commentTextView;
        ImageView profilePicImageView;
    }

    public Bitmap base64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}
