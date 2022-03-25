package com.example.carcassonne;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.graphics.Canvas;

public class BoardSurfaceView extends SurfaceView{
    private BoardOLD board;
    private int scrollX;
    private int scrollY;
    private double Scale;

    public BoardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onDraw(Canvas canvas){

    }
}
