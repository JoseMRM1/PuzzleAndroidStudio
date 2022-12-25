package com.example.puzzledroid_java.entidadesbd;

public class Puntuacion {
    public static int level;
    public static String player;
    public static String date;
    public static Double seconds;
    public int position;

    public Puntuacion() {
    }

    public Puntuacion(int level, String player, String date, Double seconds) {
        Puntuacion.level = level;
        Puntuacion.player = player;
        Puntuacion.date = date;
        Puntuacion.seconds = seconds;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        Puntuacion.level = level;
    }

    public static String getPlayer() {
        return player;
    }

    public static void setPlayer(String player) {
        Puntuacion.player = player;
    }

    public static String getDate() {
        return date;
    }

    public static void setDate(String date) {
        Puntuacion.date = date;
    }

    public static Double getSeconds() {
        return seconds;
    }

    public static void setSeconds(Double seconds) {
        Puntuacion.seconds = seconds;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return this.position + ". " + player + ", " + date + ", " + "tiempo: " + seconds + " segundos";
    }

}
