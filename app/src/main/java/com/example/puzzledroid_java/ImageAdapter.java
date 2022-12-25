package com.example.puzzledroid_java;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final AssetManager am;
    private String[] files;

    public ImageAdapter(Context c) {
        mContext = c;
        am = mContext.getAssets();
        try {
            files = am.list("imagenes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return files.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        int layout;
        int imageViewType;

        layout = R.layout.grid_element;
        imageViewType = R.id.gridImageView;


        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(layout, null);
        }

        final ImageView imageView = convertView.findViewById(imageViewType);
        imageView.setImageBitmap(null);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    private Bitmap bitmap;

                    @Override
                    protected Void doInBackground(Void... voids) {
                        bitmap = getPicFromAsset(imageView, files[position]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        imageView.setImageBitmap(bitmap);
                    }
                }.execute();
            }
        });

        return convertView;
    }

    private Bitmap getPicFromAsset(ImageView imageView, String assetName) {
        int objAncho = imageView.getWidth();
        int objAlto = imageView.getHeight();

        if (objAncho == 0 || objAlto == 0) {
            return null;
        }
        try {
            InputStream is = am.open("imagenes/" + assetName);
            BitmapFactory.Options bmOpciones = new BitmapFactory.Options();
            bmOpciones.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, new Rect(-1, -1, -1, -1), bmOpciones);
            int anchoFoto = bmOpciones.outWidth;
            int altoFoto = bmOpciones.outHeight;

            int factorEscala = Math.min(anchoFoto / objAncho, altoFoto / objAlto);

            is.reset();

            bmOpciones.inJustDecodeBounds = false;
            bmOpciones.inSampleSize = factorEscala;
            bmOpciones.inPurgeable = true;

            return BitmapFactory.decodeStream(is, new Rect(-1, -1, -1, -1), bmOpciones);
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }
}
