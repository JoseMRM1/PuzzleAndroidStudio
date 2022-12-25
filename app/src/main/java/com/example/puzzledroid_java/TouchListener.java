package com.example.puzzledroid_java;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class TouchListener implements View.OnTouchListener {
    private final PuzzleActivity activity;
    private float xDelta;
    private float yDelta;

    public TouchListener(PuzzleActivity activity) {
        this.activity = activity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getRawX();
        float y = motionEvent.getRawY();
        final double tolerancia = sqrt(pow(view.getWidth(), 2) + pow(view.getHeight(), 2)) / 10;

        PuzzlePiece pieza = (PuzzlePiece) view;
        if (!pieza.canMove) {
            return true;
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xDelta = x - layoutParams.leftMargin;
                yDelta = y - layoutParams.topMargin;
                pieza.bringToFront();
                break;

            case MotionEvent.ACTION_MOVE:
                layoutParams.leftMargin = (int) (x - xDelta);
                layoutParams.topMargin = (int) (y - yDelta);
                view.setLayoutParams(layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                int xDiff = abs(pieza.coordX - layoutParams.leftMargin);
                int yDiff = abs(pieza.coordY - layoutParams.topMargin);
                if (xDiff <= tolerancia && yDiff <= tolerancia) {
                    layoutParams.leftMargin = pieza.coordX;
                    layoutParams.topMargin = pieza.coordY;
                    pieza.setLayoutParams(layoutParams);
                    pieza.canMove = false;
                    sendViewToBack(pieza);
                    activity.checkGameOver();
                }
                break;
        }
        return true;

    }

    public void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }
}
