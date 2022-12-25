package com.example.puzzledroid_java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.puzzledroid_java.utils.GestorNotificacion;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    SignInButton signInButton;
    Button signOutButton;
    TextView statusTextView;
    GoogleApiClient mGoogleApiClient;
    //public static Utilidades almacen = new NivelesSuperados();
    public static MediaPlayer mediaPlayer;
    public static boolean musicPermit = true;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_MUSIC_FILE = 9002;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int RC_SIGN_IN2 = 1234;

    private GoogleSignInClient mSignInClient;
    public static final String ANONYMOUS = "anonymous";

    //private GoogleSignInClient mSignInClient;


    private FirebaseAuth mFirebaseAuth;

    // Firebase instance variables
    //private FirebaseAuth mFirebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.barraMenu);
        setSupportActionBar(toolbar);

        if (musicPermit == true) {
            createStandardMusic();
            musicPermit = false;
        }

        GestorNotificacion.createNotificationChannel(this);

        Button btnJugar = findViewById(R.id.btnJugar);
        Button btnPuntos = findViewById(R.id.btnPuntos);
        Button btnPuntosCluod = findViewById(R.id.btnPuntosCloud);
        ImageButton btnMusica = findViewById(R.id.btnSelectMusic);
        ImageButton btnMusica2 = findViewById(R.id.btnSelectMusic2);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        mFirebaseAuth = FirebaseAuth.getInstance();

        statusTextView = (TextView) findViewById(R.id.statusTextView);
        signInButton = (SignInButton) findViewById(R.id.btnSingIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAuth();
            }
        });
        signOutButton = (Button) findViewById(R.id.btnSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

            }
        });

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SelectionModeActivity.class);
                startActivity(intent);
            }
        });
        btnPuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PuntuacionActivity.class);
                startActivity(intent);
            }
        });
        btnPuntosCluod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PuntuacionActivityCloud.class);
                startActivity(intent);
            }
        });
        btnMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,RC_MUSIC_FILE);
            }
        });
        btnMusica2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.release();
                createStandardMusic();
            }
        });
    }
    // Empezamos con la musica estandard del app
    private void createStandardMusic(){
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.jazzmenu);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
    // Permite buscar una pista de audio personalizada
    private void changeSelectedMusic(Uri uri){
        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(MainActivity.this, uri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }


    private void handleSignResult(GoogleSignInResult result){
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        Map<String, Object> user = new HashMap<>();
        GoogleSignInAccount acct = result.getSignInAccount();

        user.put(acct.getEmail(),acct.getDisplayName());

        statusTextView.setText("Hello, " + acct.getDisplayName());
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void signOut(){
        mFirebaseAuth.signOut();
        mSignInClient.signOut().addOnSuccessListener(this, e -> Toast.makeText(MainActivity.this, "Signed Out!",
                Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFAiled:" + connectionResult);
    }

    //Comprobamos los intents que realizamos para el login y el servicio de musica
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN2){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        else if (requestCode == RC_MUSIC_FILE && resultCode == RESULT_OK){
                Uri uri = data.getData();
                changeSelectedMusic(uri);
        }
    }

    //Código de menús

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(getApplicationContext(), Ayuda.class);
                startActivity(intent);
                return true;
            case R.id.item2:
                if (mediaPlayer.isPlaying() != true) {
                    mediaPlayer.start();
                    Toast.makeText(this,R.string.ToastMusicaReanudada, Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.pause();
                    Toast.makeText(this, R.string.ToastMusicaPausada, Toast.LENGTH_LONG).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.SeguroCerrar)
                .setCancelable(false)
                .setPositiveButton(R.string.SI, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.NO, null)
                .show();
    }


    private void signInAuth() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN2);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, e -> Toast.makeText(MainActivity.this, "Authentication in firebase OK",
                            Toast.LENGTH_SHORT).show())

                .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, "Authentication in firebase failed.",
                        Toast.LENGTH_SHORT).show());
    }
}

