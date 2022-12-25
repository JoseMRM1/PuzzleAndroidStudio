package com.example.puzzledroid_java;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puzzledroid_java.cloud_entities.CloudImage;
import com.example.puzzledroid_java.gridview_adapter.GridViewImageCloudAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzleSelectionCloud extends AppCompatActivity {

    // creating a variable for our
    // grid view, arraylist and
    // firebase Firestore.
    GridView gvImageCloudAdapter;
    ArrayList<CloudImage> cloudImageArrayList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_selection_cloud);

        // below line is use to initialize our variables.
        gvImageCloudAdapter = findViewById(R.id.gridViewCloud);
        cloudImageArrayList = new ArrayList<>();

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // here we are calling a method
        // to load data in our list view.
        loadDatainGridView();

        gvImageCloudAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
                intent.putExtra("mode", 3);
                intent.putExtra("assetName", cloudImageArrayList.get(i % cloudImageArrayList.size()).getNivel());
                intent.putExtra("assetUrl", cloudImageArrayList.get(i % cloudImageArrayList.size()).getImgUrl());
                intent.putExtra("level", i + 1);
                startActivity(intent);
            }
        });
    }

    private void loadDatainGridView() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        db.collection("niveles").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding our
                            // progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                // after getting this list we are passing
                                // that list to our object class.
                                CloudImage cloudImage = d.toObject(CloudImage.class);

                                // after getting data from Firebase
                                // we are storing that data in our array list
                                cloudImageArrayList.add(cloudImage);
                            }
                            //Sorting cloud images ArrayList
                            Collections.sort(cloudImageArrayList);
                            // after that we are passing our array list to our adapter class.
                            GridViewImageCloudAdapter adapter = new GridViewImageCloudAdapter(PuzzleSelectionCloud.this, cloudImageArrayList);

                            // after passing this array list
                            // to our adapter class we are setting
                            // our adapter to our list view.
                            gvImageCloudAdapter.setAdapter(adapter);

                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(PuzzleSelectionCloud.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(PuzzleSelectionCloud.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}