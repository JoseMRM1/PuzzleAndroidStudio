package com.example.puzzledroid_java;

import static java.lang.Math.abs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    ArrayList<PuzzlePiece> piezas;

    Intent intent = new Intent();
    long startTime = SystemClock.elapsedRealtime();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        Toolbar toolbar = findViewById(R.id.barraMenu);
        setSupportActionBar(toolbar);

        Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
        simpleChronometer.start();
        simpleChronometer.setFormat("%s");

        final RelativeLayout layout = findViewById(R.id.layout);
        final ImageView imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        final String assetName = intent.getStringExtra("assetName");
        final String assetUrl = intent.getStringExtra("assetUrl");
        final int mode = intent.getIntExtra("mode", -1);
        imageView.post(() -> {
            if (assetName != null) {
                fotoAsset(assetName,assetUrl, imageView, mode);
            }
            piezas = splitImage();
            TouchListener touchListener = new TouchListener(PuzzleActivity.this);
            Collections.shuffle(piezas);
            for (PuzzlePiece pieza : piezas) {
                pieza.setOnTouchListener(touchListener);
                layout.addView(pieza);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pieza.getLayoutParams();
                layoutParams.leftMargin = new Random().nextInt(layout.getWidth() - pieza.anchoPieza);
                layoutParams.topMargin = layout.getHeight() - pieza.altoPieza;
                pieza.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menupuzzle, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(getApplicationContext(), Ayuda.class);
                startActivity(intent);
                return true;
            case R.id.item3:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.SeguroNoGuardar)
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.SeguroNoGuardar)
                .setCancelable(false)
                .setPositiveButton(R.string.SI, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.NO, null)
                .show();
    }

    private void fotoAsset(String assetName,String assetUrl, ImageView imageView, int mode) {
        int objAncho = imageView.getWidth();
        int objAlto = imageView.getHeight();
        InputStream is = null;
        AssetManager am = getAssets();
        try {
            switch (mode){
                case 1:
                    is = am.open("imagenes/" + assetName);
                    break;
                case 2:
                    is = am.open("MediaStore.Images.Media.DATA." + assetName );
                    break;
                case 3:
                    System.out.println("is cloud");
                    Picasso.get().load(assetUrl).into(imageView);
                    return;
            }

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
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(-1, -1, -1, -1), bmOpciones);
            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private ArrayList<PuzzlePiece> splitImage() {
        Intent intent_image = getIntent();
        final int level = intent_image.getIntExtra("level", -1);
        int numeroPiezas = 0;
        int filas = 1;
        int columnas = 1;


        if (level == 1) {
            numeroPiezas = 4;
            filas = 2;
            columnas = 2;
        } else if (level == 2) {
            numeroPiezas = 9;
            filas = 3;
            columnas = 3;
        } else if (level == 3) {
            numeroPiezas = 16;
            filas = 4;
            columnas = 4;
        } else if (level == 4) {
            numeroPiezas = 25;
            filas = 5;
            columnas = 5;
        } else if (level == 5) {
            numeroPiezas = 36;
            filas = 6;
            columnas = 6;
        }


        ImageView imageView = findViewById(R.id.imageView);
        ArrayList<PuzzlePiece> piezas = new ArrayList<>(numeroPiezas);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        int[] dimensiones = getBitmapPositionInsideImageView(imageView);
        int escalaIzqBitmap = dimensiones[0];
        int escalaSupBitmap = dimensiones[1];
        int escalaAnchoqBitmap = dimensiones[2];
        int escalaAltoBitmap = dimensiones[3];

        int anchoImgRecortada = escalaAnchoqBitmap - 2 * abs(escalaIzqBitmap);
        int altoImgRecortada = escalaAltoBitmap - 2 * abs(escalaSupBitmap);

        Bitmap escalaBitmap = Bitmap.createScaledBitmap(bitmap, escalaAnchoqBitmap, escalaAltoBitmap, true);
        Bitmap recorteBitmap = Bitmap.createBitmap(escalaBitmap, abs(escalaIzqBitmap), abs(escalaSupBitmap), anchoImgRecortada, altoImgRecortada);

        int anchoPieza = anchoImgRecortada / columnas;
        int altoPieza = altoImgRecortada / filas;

        int coordY = 0;
        for (int fila = 0; fila < filas; fila++) {
            int coordX = 0;
            for (int columna = 0; columna < columnas; columna++) {
                int offsetX = 0;
                int offsetY = 0;
                if (columna > 0) {
                    offsetX = anchoPieza / 3;
                }
                if (fila > 0) {
                    offsetY = altoPieza / 3;
                }

                Bitmap piezaBitmap = Bitmap.createBitmap(recorteBitmap, coordX - offsetX, coordY - offsetY, anchoPieza + offsetX, altoPieza + offsetY);
                PuzzlePiece pieza = new PuzzlePiece(getApplicationContext());
                pieza.setImageBitmap(piezaBitmap);
                pieza.coordX = coordX - offsetX + imageView.getLeft();
                pieza.coordY = coordY - offsetY + imageView.getTop();
                pieza.anchoPieza = anchoPieza + offsetX;
                pieza.altoPieza = altoPieza + offsetY;

                Bitmap puzzlePiece = Bitmap.createBitmap(anchoPieza + offsetX, altoPieza + offsetY, Bitmap.Config.ARGB_8888);

                int bumpSize = altoPieza / 4;
                Canvas canvas = new Canvas(puzzlePiece);
                Path path = new Path();
                path.moveTo(offsetX, offsetY);

                if (fila == 0) {
                    path.lineTo(piezaBitmap.getWidth(), offsetY);
                } else {
                    path.lineTo(offsetX + (piezaBitmap.getWidth() - offsetX) / 3, offsetY);
                    path.cubicTo(offsetX + (piezaBitmap.getWidth() - offsetX) / 6, offsetY - bumpSize, offsetX + (piezaBitmap.getWidth() - offsetX) / 6 * 5, offsetY - bumpSize, offsetX + (piezaBitmap.getWidth() - offsetX) / 3 * 2, offsetY);
                    path.lineTo(piezaBitmap.getWidth(), offsetY);
                }

                if (columna == columnas - 1) {
                    path.lineTo(piezaBitmap.getWidth(), piezaBitmap.getHeight());
                } else {
                    path.lineTo(piezaBitmap.getWidth(), offsetY + (piezaBitmap.getHeight() - offsetY) / 3);
                    path.cubicTo(piezaBitmap.getWidth() - bumpSize, offsetY + (piezaBitmap.getHeight() - offsetY) / 6, piezaBitmap.getWidth() - bumpSize, offsetY + (piezaBitmap.getHeight() - offsetY) / 6 * 5, piezaBitmap.getWidth(), offsetY + (piezaBitmap.getHeight() - offsetY) / 3 * 2);
                    path.lineTo(piezaBitmap.getWidth(), piezaBitmap.getHeight());
                }

                if (fila == filas - 1) {
                    path.lineTo(offsetX, piezaBitmap.getHeight());
                } else {
                    path.lineTo(offsetX + (piezaBitmap.getWidth() - offsetX) / 3 * 2, piezaBitmap.getHeight());
                    path.cubicTo(offsetX + (piezaBitmap.getWidth() - offsetX) / 6 * 5, piezaBitmap.getHeight() - bumpSize, offsetX + (piezaBitmap.getWidth() - offsetX) / 6, piezaBitmap.getHeight() - bumpSize, offsetX + (piezaBitmap.getWidth() - offsetX) / 3, piezaBitmap.getHeight());
                    path.lineTo(offsetX, piezaBitmap.getHeight());
                }

                if (columna == 0) {

                    path.close();
                } else {

                    path.lineTo(offsetX, offsetY + (piezaBitmap.getHeight() - offsetY) / 3 * 2);
                    path.cubicTo(offsetX - bumpSize, offsetY + (piezaBitmap.getHeight() - offsetY) / 6 * 5, offsetX - bumpSize, offsetY + (piezaBitmap.getHeight() - offsetY) / 6, offsetX, offsetY + (piezaBitmap.getHeight() - offsetY) / 3);
                    path.close();
                }
                Paint pintar = new Paint();
                pintar.setColor(0XFF00_0000);
                pintar.setStyle(Paint.Style.FILL);

                canvas.drawPath(path, pintar);
                pintar.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(piezaBitmap, 0, 0, pintar);

                Paint borde = new Paint();
                borde.setColor(0XFFBBBBBB);
                borde.setStyle(Paint.Style.STROKE);
                borde.setStrokeWidth(5.0f);
                canvas.drawPath(path, borde);

                pieza.setImageBitmap(puzzlePiece);

                piezas.add(pieza);
                coordX += anchoPieza;
            }
            coordY += altoPieza;
        }
        return piezas;
    }

    private int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] devuelve = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return devuelve;

        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        final float escalaX = f[Matrix.MSCALE_X];
        final float escalaY = f[Matrix.MSCALE_Y];

        final Drawable d = imageView.getDrawable();
        final int origAncho = d.getIntrinsicWidth();
        final int origAlto = d.getIntrinsicHeight();

        final int actAncho = Math.round(origAncho * escalaX);
        final int actAlto = Math.round(origAlto * escalaY);

        devuelve[2] = actAncho;
        devuelve[3] = actAlto;

        int imgViewAncho = imageView.getWidth();
        int imgViewAlto = imageView.getHeight();

        int sup = (int) (imgViewAlto - actAlto) / 2;
        int izq = (int) (imgViewAncho - actAncho) / 2;

        devuelve[0] = izq;
        devuelve[1] = sup;

        return devuelve;
    }

    public void checkGameOver() {
        Intent intent_image = getIntent();
        final String assetName = intent_image.getStringExtra("assetName");
        final int level = intent_image.getIntExtra("level", -1);

        if (GameOver()) {

            long endTime = SystemClock.elapsedRealtime();
            long elapsedMilliSeconds = endTime - startTime;
            double elapsedSeconds = elapsedMilliSeconds / 1000.0;

            Intent intent = new Intent(getApplicationContext(), PuzzleOver.class);
            intent.putExtra("elapsedSeconds", elapsedSeconds);
            intent.putExtra("level", level);
            startActivity(intent);

//            if (assetName.equals("imagen1.jpg"))
//                NivelesSuperados.guardarNivelSuperado(1);
        }
    }

    private boolean GameOver() {
        for (PuzzlePiece pieza : piezas) {
            if (pieza.canMove) {
                return false;
            }
        }
        return true;
    }
}