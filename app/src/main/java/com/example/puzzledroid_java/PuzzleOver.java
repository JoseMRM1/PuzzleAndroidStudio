package com.example.puzzledroid_java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puzzledroid_java.db.PuntuacionesProviderBD;
import com.example.puzzledroid_java.entidadesbd.Puntuacion;
import com.example.puzzledroid_java.utils.GestorNotificacion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PuzzleOver extends AppCompatActivity {

    public String m_Text = "";
    Bundle datos;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SavigScoreActivity";
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_over);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.kidscheering);
        mediaPlayer.start();

        datos = getIntent().getExtras();
        Double segundos = datos.getDouble("elapsedSeconds");
        int nivel = datos.getInt("level");

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Date today = Calendar.getInstance().getTime();
                Format formatter = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
                String stringFecha = formatter.format(today);
                RegistrarPuntuacion(nivel, stringFecha, segundos);
            }
        });
    }

    private void RegistrarPuntuacion(int nivel, String fecha, Double secs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PuzzleOver.this);
        Puntuacion puntuacion_item = new Puntuacion();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            builder.setTitle(R.string.NombreJugador);
            final EditText input = new EditText(PuzzleOver.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Puntuacion.setLevel(nivel);
                    Puntuacion.setDate(fecha);
                    Puntuacion.setSeconds(secs);
                    Puntuacion.setPlayer(m_Text);
                    PuntuacionesProviderBD.addScore(getApplicationContext(), puntuacion_item);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    GestorNotificacion.insertNotification(getApplicationContext(), secs);
                    // GestorCalendar.AddEvent();

                }
            });
            builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            Map<String, Object> puntuacion = new HashMap<>();
            builder.setTitle("Do you want save score in firebase cloud for " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "?");
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    puntuacion.put("level", nivel);
                    puntuacion.put("player", mFirebaseAuth.getCurrentUser().getDisplayName());
                    puntuacion.put("date", fecha);
                    puntuacion.put("secs", secs);
                    db.collection("puntuaciones")
                            .add(puntuacion)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Score registered in the firebase cloud: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding score in the firebase cloud", e);
                                }
                            });
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);    }
            });
            builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }


    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.SeguroPulsarGuardar)
                .setCancelable(false)
                .setPositiveButton(R.string.SI, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.NO, null)
                .show();
    }
}