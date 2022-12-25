package com.example.puzzledroid_java.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.puzzledroid_java.entidadesbd.Puntuacion;

import java.util.ArrayList;
import java.util.Vector;

public class PuntuacionesProviderBD {
    public synchronized static boolean addScore(Context context, Puntuacion item) {
        boolean result = false;
        try {
            SQLiteDatabase db = BDHelper.getInstance(context).getSqLiteDb();
            db.beginTransaction();
            DatabaseUtils.InsertHelper inserHelper = new DatabaseUtils.InsertHelper(db,
                    DataBase.PUNTUACION_TABLE);
            final int firstColumn = inserHelper
                    .getColumnIndex(DataBase.PuntuacionesColumn.LEVEL);
            final int secondColumn = inserHelper
                    .getColumnIndex(DataBase.PuntuacionesColumn.PLAYER);
            final int thirdColumn = inserHelper
                    .getColumnIndex(DataBase.PuntuacionesColumn.DATE);
            final int fourColumn = inserHelper
                    .getColumnIndex(DataBase.PuntuacionesColumn.SECONDS);
            try {
                db.setLockingEnabled(false);
                inserHelper.prepareForInsert();
                inserHelper.bind(firstColumn, Puntuacion.getLevel());
                inserHelper.bind(secondColumn, Puntuacion.getPlayer());
                inserHelper.bind(thirdColumn, Puntuacion.getDate());
                inserHelper.bind(fourColumn, Puntuacion.getSeconds());
                inserHelper.execute();
                db.setTransactionSuccessful();
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inserHelper != null) {
                    inserHelper.close();
                }
                db.setLockingEnabled(true);
                db.endTransaction();
            }
        } catch (Exception e) {
        }
        return result;
    }


    public synchronized static boolean removeScores(Context context,
                                                    String level) {
        boolean result = false;
        String where = DataBase.PuntuacionesColumn.LEVEL + "='" + level + "'";
        int count = BDHelper.getInstance(context).delete(
                DataBase.PUNTUACION_TABLE, where, null);
        if (count > 0) {
            result = true;
        }
        return result;
    }


    public synchronized static Vector<String> getScoreList(
            Context context, Integer nivel) {
        //List<Puntuacion> listaPuntuaciones = new ArrayList <Puntuacion>();
        Vector<String> puntuacionesNivel = new Vector<String>();
        String where = DataBase.PuntuacionesColumn.LEVEL + "='" + nivel + "'";
        String order_by = DataBase.PuntuacionesColumn.SECONDS + " ASC LIMIT 10";
        Cursor scoreCursor = BDHelper.getInstance(context).select(
                DataBase.PUNTUACION_TABLE, null, where, null, null, null, order_by);
        if (scoreCursor != null && scoreCursor.getCount() > 0) {
            scoreCursor.moveToFirst();
            for (int k = 0; k < scoreCursor.getCount(); k++) {
                Puntuacion item = new Puntuacion();
                item.setPosition(k + 1);
                Puntuacion.setLevel(scoreCursor.getInt(scoreCursor.getColumnIndexOrThrow(DataBase.PuntuacionesColumn.LEVEL)));
                Puntuacion.setPlayer(scoreCursor.getString(scoreCursor.getColumnIndexOrThrow(DataBase.PuntuacionesColumn.PLAYER)));
                Puntuacion.setDate(scoreCursor.getString(scoreCursor.getColumnIndexOrThrow(DataBase.PuntuacionesColumn.DATE)));
                Puntuacion.setSeconds(scoreCursor.getDouble(scoreCursor.getColumnIndexOrThrow(DataBase.PuntuacionesColumn.SECONDS)));
                String stringPuntuacionForVector = item.toString();
                puntuacionesNivel.add(stringPuntuacionForVector);
                scoreCursor.moveToNext();
            }
        }
        scoreCursor.close();
        return puntuacionesNivel;
    }

    public synchronized static ArrayList<Integer> getLevelsList(
            Context context) {
        ArrayList<Integer> list = null;
        String column_level = "DISTINCT " + DataBase.PuntuacionesColumn.LEVEL;
        String order_by = DataBase.PuntuacionesColumn.SECONDS + " ASC";
        Cursor scoreCursor = BDHelper.getInstance(context).select(
                DataBase.PUNTUACION_TABLE, new String[]{column_level}, null, null, null, null, order_by);
        if (scoreCursor != null && scoreCursor.getCount() > 0) {
            list = new ArrayList<Integer>();
            scoreCursor.moveToFirst();
            for (int k = 0; k < scoreCursor.getCount(); k++) {
                list.add(scoreCursor.getInt(scoreCursor.getColumnIndexOrThrow(DataBase.PuntuacionesColumn.LEVEL)));
                scoreCursor.moveToNext();
            }
        }
        return list;
    }


}
