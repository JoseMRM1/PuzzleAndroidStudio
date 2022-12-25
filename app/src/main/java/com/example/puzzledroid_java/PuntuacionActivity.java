package com.example.puzzledroid_java;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.puzzledroid_java.db.PuntuacionesProviderBD;

import java.util.ArrayList;
import java.util.Vector;

public class PuntuacionActivity extends ListActivity {
    private static final Vector<String> puntuacionesToListAdapter = new Vector<String>();
    private Vector<String> listaPuntuacionesFromSqlite = new Vector<String>();
    private ArrayList<Integer> listaNivelesFromSqlite = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int i = 1;

        listaNivelesFromSqlite = PuntuacionesProviderBD.getLevelsList(getApplicationContext());
        if (listaNivelesFromSqlite == null) {
            puntuacionesToListAdapter.add("No existen puntuaciones, todavía no ha resuelto ningún nivel");
        } else {
            for (Integer nivel : listaNivelesFromSqlite) {
                listaPuntuacionesFromSqlite = PuntuacionesProviderBD.getScoreList(getApplicationContext(), nivel);

                String nivel_string_to_adapter = "                                LEVEL " + nivel.toString() + " TOP 10";
                puntuacionesToListAdapter.add(nivel_string_to_adapter);

                for (String puntuacion : listaPuntuacionesFromSqlite) {
                    puntuacionesToListAdapter.add(puntuacion);
                    i++;
                }
            }
        }

        setContentView(R.layout.activity_puntuaciones);

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
