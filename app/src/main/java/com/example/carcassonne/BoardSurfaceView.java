package com.example.carcassonne;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.graphics.Canvas;

public class BoardSurfaceView extends SurfaceView {
    private CarcassonneGameState gameState;
    private BitmapProvider bitmapProvider;

    private int scrollX;
    private int scrollY;

    private static final float SCALE = 1.0f;

    public BoardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParameters(CarcassonneGameState gameState, BitmapProvider bitmapProvider) {
        this.gameState = gameState;
        this.bitmapProvider = bitmapProvider;
    }

    public void onDraw(Canvas canvas) {
        Board board = this.gameState.getBoard();

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Tile tile = board.getTile(x, y);
                Bitmap tileBitmap;

                if (tile == null) {
                    tileBitmap = this.bitmapProvider.getEmptyTile().bitmap;
                } else {
                    tileBitmap = this.bitmapProvider.getTile(tile.getId()).tile.bitmap;
                }

                float top = (x * Tile.SIZE - this.scrollY) * SCALE;
                float left = (x * Tile.SIZE - this.scrollX) * SCALE;
                RectF dest = new RectF(left, top, left + Tile.SIZE * SCALE, top + Tile.SIZE * SCALE);

                canvas.drawBitmap(tileBitmap, null, dest, null);
            }
        }
    }
}
