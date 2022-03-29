package com.example.carcassonne;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.view.View;

public class BoardSurfaceView extends SurfaceView {
    private CarcassonneGameState gameState;
    private BitmapProvider bitmapProvider;

    private float prevTouchX;
    private float prevTouchY;

    private float scrollX;
    private float scrollY;

    private static final float SCALE = 1.0f;

    public BoardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resetPrevTouch();
    }

    public boolean onTouch(View view, MotionEvent event) {
        /*
         * External Citation
         * Date: 28 March 2022
         * Problem: Needed to know how to get the type of a MotionEvent.
         * Resource:
         *     https://developer.android.com/reference/android/view/MotionEvent#getActionMasked()
         * Solution: Used getActionMasked() and looked at the action constants in MotionEvent.
         */
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_UP) {
            resetPrevTouch();
        } else if (action == MotionEvent.ACTION_MOVE) {
            this.scrollX += event.getX() - this.prevTouchX;
            this.scrollY += event.getY() - this.prevTouchY;
        }

        this.prevTouchX = event.getX();
        this.prevTouchY = event.getY();

        return false;
    }

    public void setBitmapProvider(BitmapProvider bitmapProvider) {
        this.bitmapProvider = bitmapProvider;
    }

    public void setGameState(CarcassonneGameState gameState) {
        this.gameState = gameState;
    }

    public void onDraw(Canvas canvas) {
        // We can't draw unless we've received parameters from CarcassonneHumanPlayer
        if (this.gameState == null || this.bitmapProvider == null) {
            return;
        }

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

                /*
                 * External Citation
                 * Date: 25 March 2022
                 * Problem: Needed to know how to do a scaled draw of a bitmap to a Canvas
                 * Resource:
                 *     https://developer.android.com/reference/android/graphics/Canvas#drawBitmap(
                 *     android.graphics.Bitmap,%20android.graphics.Rect,%20android.graphics.RectF,
                 *     %20android.graphics.Paint)
                 * Solution: Used this overload of drawBitmap() with null for src and paint.
                 */
                float top = (x * Tile.SIZE - this.scrollY) * SCALE;
                float left = (x * Tile.SIZE - this.scrollX) * SCALE;
                RectF dest = new RectF(left, top, left + Tile.SIZE * SCALE, top + Tile.SIZE * SCALE);

                canvas.drawBitmap(tileBitmap, null, dest, null);
            }
        }
    }

    private void resetPrevTouch() {
        this.prevTouchX = this.prevTouchY = -1.0f;
    }
}
