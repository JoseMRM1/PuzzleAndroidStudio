package com.example.puzzledroid_java;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

public class PuzzlePiece extends AppCompatImageView {
    public int coordX;
    public int coordY;
    public int anchoPieza;
    public int altoPieza;
    public boolean canMove = true;

    public PuzzlePiece(Context context) {

        super(context);
    }
}
