package com.example.puzzledroid_java;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PuzzleSelectionCamera extends AppCompatActivity {
    Handler handler = new Handler();

    protected int counter = 0;
    private ImageView mImageView;
    private Bitmap currentBitmap = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_selection_camera);

        String[] projection = new String[]{
                MediaStore.Images.Media.DATA,
        };
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = managedQuery(images,
                projection,
                "",
                null,
                ""
        );
        final ArrayList<String> imagesPath = new ArrayList<String>();
        if (cur.moveToFirst()) {

            int dataColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            do {
                imagesPath.add(cur.getString(dataColumn));
            } while (cur.moveToNext());
        }
        cur.close();
        final Random random = new Random();
        final int count = imagesPath.size();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int number = random.nextInt(count);
                String path = imagesPath.get(number);
                if (currentBitmap != null)
                    currentBitmap.recycle();
                currentBitmap = BitmapFactory.decodeFile(path);
                mImageView.setImageBitmap(currentBitmap);
                handler.postDelayed(this, 1000);
            }
        });

        AssetManager am = getAssets();
        try {
            final String[] files = am.list("imagenes");

            GridView grid = findViewById(R.id.grid);
            grid.setAdapter(new ImageAdapter(this));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
                    intent.putExtra("mode", 2);
                    intent.putExtra("assetName", files[i % files.length]);
                    intent.putExtra("level", 5);
                    startActivity(intent);
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
        }
    }
}