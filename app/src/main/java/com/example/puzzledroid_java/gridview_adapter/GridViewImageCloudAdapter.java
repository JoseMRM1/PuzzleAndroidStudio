package com.example.puzzledroid_java.gridview_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.puzzledroid_java.R;
import com.example.puzzledroid_java.cloud_entities.CloudImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewImageCloudAdapter extends ArrayAdapter<CloudImage> {

    // constructor for our list view adapter.
    public GridViewImageCloudAdapter(@NonNull Context context, ArrayList<CloudImage> cloudImageArrayList) {
        super(context, 0, cloudImageArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // below line is use to inflate the 
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_puzzle_selection_cloud_item, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside 
        // our modal class.
        CloudImage cloudImage = getItem(position);

        // initializing our UI components of list view item.
        TextView textView = listitemView.findViewById(R.id.textItemSelectionCloudView);
        ImageView imageView = listitemView.findViewById(R.id.imageItemSelectionCloudView);

        // after initializing our items we are
        // setting data to our view.

        // below line is use to set data to our text view.
        textView.setText(cloudImage.getNivel());

        // in below line we are using Picasso to load image
        // from URL in our Image VIew.
        Picasso.get().load(cloudImage.getImgUrl()).into(imageView);

        // below line is use to add item 
        // click listener for our item of list view.
        return listitemView;
    }



}