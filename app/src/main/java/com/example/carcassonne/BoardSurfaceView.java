package com.example.carcassonne;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.view.View;

/**
 * Controls the surface view that holds the game board/tiles
 *
 * @author Cheyanne Yim
 * @author Vincent Robinson
 * @author Alex Martinez-Lopez
 * @author DJ Backus
 * @author Sophie Arcangel
 */

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
        setWillNotDraw(false);
        setBackgroundColor(0xFFFFFFFF);
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
                    // Empty tiles have their own special empty bitmap, which forms a grid
                    // and shows where the valid positions are.
                    tileBitmap = this.bitmapProvider.getEmptyTile().bitmap;
                } else {
                    // Otherwise, get the proper tile and bitmap.
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

                RectF tileRect = makeRect(
                        x * Tile.SIZE,
                        y * Tile.SIZE,
                        Tile.SIZE,
                        Tile.SIZE
                );

                // TODO: Draw rotated
                canvas.drawBitmap(tileBitmap, null, tileRect, null);

                if (tile != null) {
                    // If this is the current tile, draw a border around to indicate if it's valid
                    // or not. The rectangle for borders is identical to the tile rectangle.
                    if (x == board.getCurrentTileX() && y == board.getCurrentTileY()) {
                        Bitmap border = board.isCurrentPlacementValid() ?
                                this.bitmapProvider.getValidBorder().bitmap :
                                this.bitmapProvider.getInvalidBorder().bitmap;
                        canvas.drawBitmap(border, null, tileRect, null);
                    }

                    // If there's a meeple on the tile, draw it centered at the X and Y position
                    // of its section.
                    Section meepleSection = tile.getMeepleSection();
                    if (meepleSection != null) {
                        // Choose the color of the meeple from the owner of the tile.
                        BitmapProvider.MeepleBitmapData bitmapData =
                                this.bitmapProvider.getMeeple(tile.getOwner());

                        // Farmers have their own special bitmaps, so select this.
                        Bitmap meepleBitmap = meepleSection.getType() == Tile.TYPE_FARM ?
                                bitmapData.farmer.bitmap :
                                bitmapData.meeple.bitmap;

                        float width = meepleBitmap.getWidth();
                        float height = meepleBitmap.getHeight();
                        canvas.drawBitmap(meepleBitmap, null, makeRect(
                                width / 2,
                                height / 2,
                                width,
                                height
                        ), null);
                    }
                }
            }
        }
    }

    private void resetPrevTouch() {
        this.prevTouchX = this.prevTouchY = -1.0f;
    }

    private RectF makeRect(float x, float y, float width, float height) {
        RectF rect = new RectF();

        // Apply scrolling to positions (which is subtracted to move towards the left/top)
        // and multiply to apply the scaling factor.
        rect.left = (x - this.scrollX) * SCALE;
        rect.top = (y - this.scrollY) * SCALE;
        rect.right = rect.left + (width * SCALE);
        rect.bottom = rect.top + (height * SCALE);

        return rect;
    }
}
