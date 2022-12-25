package com.example.puzzledroid_java;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Vector;

public class PuntuacionActivityCloud extends ListActivity {
    private static final String TAG = "PuntuacionActivityCloud";
    private static final Vector<String> puntuacionesToListAdapter = new Vector<String>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int num_cloud_levels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLevels();
        setContentView(R.layout.activity_puntuaciones);

    }

    private void getLevels(){
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
                            getScoresFromLevels(list.size());
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(PuntuacionActivityCloud.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(PuntuacionActivityCloud.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getScoresFromLevels(int num_cloud_levels) {
        for ( int i = 1; i < num_cloud_levels+1; i++) {
            String nivel_string_to_adapter = "                                LEVEL " + i + " TOP 10";

            int finalI = i;
            db.collection("puntuaciones")
                    .whereEqualTo("level", i).orderBy("secs", Query.Direction.ASCENDING).limit(10)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                puntuacionesToListAdapter.add(nivel_string_to_adapter);
                                int positionRank = 1;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    puntuacionesToListAdapter.add(String.valueOf(positionRank)+ ". " + document.getData().get("player").toString() + ", "
                                            + document.getData().get("date").toString() + ", tiempo: " + document.getData().get("secs").toString() + " segundos");
                                    positionRank++;
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                            if (task.isSuccessful() && finalI == num_cloud_levels){
                                loadListAdapter();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // we are displaying a toast message
                            // when we get any error from Firebase.
                            Toast.makeText(PuntuacionActivityCloud.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                        }
                    });


        }


    }

    public void loadListAdapter() {
        setListAdapter(new ArrayAdapter(this,
                        android.R.layout.simple_list_item_1,
                        puntuacionesToListAdapter
                )
        );
    }



    @Override
    public void onBackPressed() {
        puntuacionesToListAdapter.clear();
        finish();
    }
}
