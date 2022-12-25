package com.example.puzzledroid_java.db;

import android.provider.BaseColumns;

public class DataBase {
    public final static String PUNTUACION_TABLE = "Puntuacion";

    public interface PuntuacionesColumn extends BaseColumns {
        String LEVEL = "level";
        String PLAYER = "player";
        String DATE = "date";
        String SECONDS = "seconds";

    }
}
