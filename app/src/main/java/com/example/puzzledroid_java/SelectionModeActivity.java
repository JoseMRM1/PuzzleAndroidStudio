package com.example.puzzledroid_java;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionModeActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1234;
    private static final int CAPTURE_CODE = 1001;
    Uri image_uri;
    int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_mode);

        Button btnNormal = findViewById(R.id.btnNormal);
        Button btnGaleria = findViewById(R.id.btnGaleria);
        Button btnCamara = findViewById(R.id.btnCamara);
        Button btnCloud = findViewById(R.id.btnSelectCloudImages);

        //En desarrollo...
        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PuzzleSelectionCloud.class);
                startActivity(intent);
            }
        });

        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PuzzleSelection.class);
                startActivity(intent);
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                Intent intent = new Intent(getApplicationContext(), PuzzleSelectionCamera.class);
                startActivity(intent);

            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), R.string.Permisos,
                        Toast.LENGTH_SHORT).show();
                    getPermissions();
                }
                else{
                    openCamera();
                }

            }
        });
    }

    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "new image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Device Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camera_intent, CAPTURE_CODE);

    }
    private void openGallery() {
        Intent gallery_intent = new Intent();
        gallery_intent.setType("image/");
        gallery_intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery_intent, "Selecciona las imágenes"), PICK_IMAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.Permisos,
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getPermissions(){
        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permission, PERMISSION_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_CODE && resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), R.string.Fotografia,
                    Toast.LENGTH_SHORT).show();
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            Intent intent = new Intent(getApplicationContext(), PuzzleSelectionCamera.class);
            startActivity(intent);

        }
    }


}