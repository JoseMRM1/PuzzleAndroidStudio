package com.example.puzzledroid_java.utils;

import java.util.Vector;

public class NivelesSuperados implements Utilidades {

    private static Vector<Integer> niveles_superados;

    public NivelesSuperados() {
        niveles_superados = new Vector<Integer>();
    }

    public static void guardarNivelSuperado(Integer nivel_superado) {
        niveles_superados.add(nivel_superado);
    }

    public Vector<Integer> getNivelesSuperados() {
        return niveles_superados;
    }
}