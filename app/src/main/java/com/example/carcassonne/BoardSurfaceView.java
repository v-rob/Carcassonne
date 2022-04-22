package com.example.carcassonne;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.graphics.Canvas;

/**
 * Draws the entire main game board containing the tiles, and also includes information
 * about the scrolling of the board.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class BoardSurfaceView extends SurfaceView {
    /**
     * A reference to the board being drawn. It gets set with setBoard() every time
     * CarcassonneHumanPlayer receives an updated game state. It is null when
     * BoardSurfaceView is first constructed.
     */
    private Board board;

    /** Number of pixels a touch event may move before it registers as scrolling. */
    private static final float SCROLL_THRESHOLD = 10.0f;

    /** The current X scroll of the board, measured in pixels towards the right. */
    private float scrollX;
    /** The current Y scroll of the board, measured in pixels towards the bottom. */
    private float scrollY;

    private float scale;

    /**
     * Whether the board has been scrolled since the user last started touching the
     * screen. If the touch has moved more than SCROLL_THRESHOLD pixels, this is set
     * to true. This is used to determine whether or not to register a touch event
     * as intended to scroll or place a tile.
     */
    private boolean moved;

    private boolean switching;

    /** The previous X position of the last touch event, used to calculate movement. */
    private float prevTouchX;
    /** The previous Y position of the last touch event, used to calculate movement. */
    private float prevTouchY;

    private float origScale;
    private float origDistance;
    private float lastDistance;

    /**
     * Constructs a new BoardSurfaceView from the XML. It sets it as drawable and gives
     * it a white background color. It will have no board.
     *
     * @param context Unused.
     * @param attrs   Unused.
     */
    public BoardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);
        setBackgroundColor(0xFFFFFFFF);

        this.scale = 1.0f;

        this.moved = false;
        this.switching = false;

        float tileSize = getTileSize();

        // Hacky code to center the board: will be replaced later.
//        this.scrollX = tileSize * -1.3f;
//        this.scrollY = tileSize * -0.4f;
    }

    /**
     * Sets the board to be used in all drawing. Should be called every time
     * CarcassonneHumanPlayer receives a new game state.
     *
     * @param board The board to use in drawing.
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Helper method for the OnTouchListener registered in CarcassonneHumanPlayer that,
     * given the MotionEvent, scrolls the board and, if the user touched the board with
     * intent to place a tile, returns the position that the tile is to be placed at.
     *
     * @param event The MotionEvent received by the calling OnTouchListener.
     * @return The position to place the current tile, or null if the user did not intend
     *         to place any such tile.
     */
    public Point onTouch(MotionEvent event) {
        // We can't do anything until we've received the board from CarcassonneHumanPlayer.
        if (this.board == null) {
            return null;
        }

        /*
         * External Citation
         * Date: 28 March 2022
         * Problem: Needed to know how to get the type of a MotionEvent.
         * Resource:
         *     https://developer.android.com/reference/android/view/MotionEvent#getActionMasked()
         * Solution: Used getActionMasked() and looked at the action constants in MotionEvent.
         */
        float x = event.getX();
        float y = event.getY();

        float tileSize = getTileSize();

        // TODO: Center scrolling initially
        // TODO: Don't jerk when adding to left/top
        // TODO: Allow scaling

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                // The point to return, if any.
                Point point = null;

                float posX = x + this.scrollX;
                float posY = y + this.scrollY;

                // Return the position the player tapped at if it's in bounds. Also ensure
                // that the position is non-negative since floor division will round -1 to
                // 0 and register it as a valid position, which is wrong.
                if (!this.moved && posX >= 0 && posY >= 0) {
                    // Convert from screen coordinates to tile positions.
                    point = new Point(
                            (int)(posX / tileSize),
                            (int)(posY / tileSize)
                    );

                    // Ensure the position just calculated is in bounds for the board; otherwise,
                    // still return null.
                    if (point.x < 0 || point.x >= this.board.getWidth() ||
                            point.y < 0 || point.y >= this.board.getHeight()) {
                        point = null;
                    }
                }

                // Reset the moved state to prepare for the next ACTION_DOWN.
                this.moved = false;

                return point;
            case MotionEvent.ACTION_MOVE:
                float deltaX;
                float deltaY;

                if (event.getPointerCount() == 2) {
                    // If they've got more than one finger down, they're definitely not placing.
                    this.moved = true;

                    float x2 = event.getX(1);
                    float y2 = event.getY(1);

                    float avgX = (x + x2) / 2;
                    float avgY = (y + y2) / 2;

                    deltaX = avgX - this.prevTouchX;
                    deltaY = avgY - this.prevTouchY;

                    this.prevTouchX = avgX;
                    this.prevTouchY = avgY;

                    float distance = (float)Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
                    this.scale = this.origScale * distance / this.origDistance;

                    // Clamp scaling to reasonable amounts.
                    if (this.scale < 0.125f) {
                        this.scale = 0.125f;
                    } else if (this.scale > 2.0f) {
                        this.scale = 2.0f;
                    } else {
                        // Otherwise, compensate for zooming around the center of the pinch rather
                        // than the top-left of the screen.
                        this.scrollX = (this.scrollX + avgX) * distance / this.lastDistance - avgX;
                        this.scrollY = (this.scrollY + avgY) * distance / this.lastDistance - avgY;
                    }

                    this.lastDistance = distance;
                } else {
                    deltaX = x - this.prevTouchX;
                    deltaY = y - this.prevTouchY;

                    this.prevTouchX = x;
                    this.prevTouchY = y;
                }

                // Don't scroll the first frame after switching between scrolling and zooming
                // to avoid jerking since the center of motion has changed.
                if (this.switching) {
                    this.switching = false;
                    break;
                }

                // If the user moved only a slight bit after ACTION_DOWN, they're probably
                // still tapping rather than scrolling. Don't change the previous position.
                if (!this.moved && Math.abs(deltaX) < SCROLL_THRESHOLD &&
                        Math.abs(deltaY) < SCROLL_THRESHOLD) {
                    break;
                }

                // Otherwise, register as moving and scroll the board.
                this.moved = true;
                this.scrollX -= deltaX;
                this.scrollY -= deltaY;

                // Perform bounds clipping: if we've scrolled more than half the screen
                // away from the edge of the board, snap it back to the edge.
                float leftEdge = -(float)getWidth() / 2;
                float topEdge = -(float)getHeight() / 2;

                float rightEdge = board.getWidth() * tileSize + leftEdge;
                float bottomEdge = board.getHeight() * tileSize + topEdge;

                if (this.scrollX < leftEdge) {
                    this.scrollX = leftEdge;
                } else if (this.scrollX > rightEdge) {
                    this.scrollX = rightEdge;
                }

                if (this.scrollY < topEdge) {
                    this.scrollY = topEdge;
                } else if (this.scrollY > bottomEdge) {
                    this.scrollY = bottomEdge;
                }

                break;
            case MotionEvent.ACTION_DOWN:
                // Set the last position to the current one.
                this.prevTouchX = x;
                this.prevTouchY = y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                float x2 = event.getX(1);
                float y2 = event.getY(1);

                this.origScale = scale;
                this.origDistance = (float)Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
                this.lastDistance = this.origDistance;

                // Fallthrough
            case MotionEvent.ACTION_POINTER_UP:
                this.switching = true;
                break;
        }

        this.invalidate();

        // It was not tapped, so return null.
        return null;
    }

    /**
     * Draws everything on the board: tiles, empty tile positions, tile borders, and
     * meeples.
     *
     * @param canvas The canvas to draw with.
     */
    public void onDraw(Canvas canvas) {
        // We can't draw unless we've received the board from CarcassonneHumanPlayer.
        if (this.board == null) {
            return;
        }

        BitmapProvider bitmapProvider = BitmapProvider.getInstance();
        float tileSize = getTileSize();

        // Draw all the tiles on the board, including empty tiles.
        for (int x = -3; x < this.board.getWidth() + 3; x++) {
            for (int y = -3; y < this.board.getHeight() + 3; y++) {
                Tile tile = this.board.getTile(x, y);
                boolean outside = this.board.isOutOfBounds(x, y);

                Bitmap tileBitmap;

                if (outside) {
                    // If we're outside the boundaries of the board, use the outside bitmap.
                    tileBitmap = bitmapProvider.getOutsideTile().bitmap;
                } else if (tile == null) {
                    // Empty tiles have their own special empty bitmap, which forms a grid
                    // and shows where the valid positions are.
                    tileBitmap = bitmapProvider.getEmptyTile().bitmap;
                } else {
                    // Otherwise, get the proper tile bitmap.
                    tileBitmap = bitmapProvider.getTile(tile.getId()).visual.bitmap;
                }

                /*
                 * External Citation
                 * Date: 25 March 2022
                 * Problem: Needed to know how to do a rotated draw of a bitmap.
                 * Resource:
                 *     https://developer.android.com/reference/android/graphics/Matrix
                 *         and
                 *     https://developer.android.com/reference/android/graphics/Canvas#drawBitmap(
                 *     android.graphics.Bitmap,%20android.graphics.Matrix,%20android.graphics.Paint)
                 * Solution: Used this overload of drawBitmap() with a transformation matrix.
                 */

                /* Create a transformation matrix. We use the post() methods rather than the
                 * set() methods so earlier changes don't get overwritten. Order matters:
                 * scale, then rotate around the center of the tile, then move the tile. Doing
                 * it out of order will apply the transformations out of order and give
                 * incorrect results.
                 */
                Matrix matrix = new Matrix();

                matrix.postScale(this.scale, this.scale);

                // Only rotate actual tiles, not empty tiles or felt
                if (!outside && tile != null) {
                    matrix.postRotate(tile.getRotation(), tileSize / 2,
                            tileSize / 2);
                }

                matrix.postTranslate(x * tileSize - this.scrollX,
                        y * tileSize - this.scrollY);

                // Draw the bitmap with the above matrix.
                canvas.drawBitmap(tileBitmap, matrix, null);
            }
        }

        // If the current tile has been placed on the board, draw the valid/invalid
        // border around it.
        Tile currentTile = this.board.getCurrentTile();
        if (currentTile != null && currentTile.getX() != -1) {
            Bitmap border = this.board.isCurrentPlacementValid() ?
                    bitmapProvider.getValidBorder().bitmap :
                    bitmapProvider.getInvalidBorder().bitmap;

            canvas.drawBitmap(border, null, makeRect(
                    currentTile.getX() * tileSize,
                    currentTile.getY() * tileSize,
                    tileSize,
                    tileSize
            ), null);
        }

        // Draw all the meeples _after_ the tiles so tiles never overlap meeples.
        for (int x = 0; x < this.board.getWidth(); x++) {
            for (int y = 0; y < this.board.getHeight(); y++) {
                Tile tile = this.board.getTile(x, y);
                if (tile == null) {
                    continue;
                }

                Section meepleSection = tile.getMeepleSection();
                if (meepleSection == null) {
                    continue;
                }

                // Choose the color of the meeple from the owner of the tile.
                BitmapProvider.MeepleBitmapData bitmapData =
                        bitmapProvider.getMeeple(tile.getOwner());

                // Farmers have their own special bitmaps, so select the correct one.
                Bitmap meepleBitmap = meepleSection.getType() == Tile.TYPE_FARM ?
                        bitmapData.farmer.bitmap :
                        bitmapData.normal.bitmap;

                // Draw the meeple centered at the meeple position for the section it's in.
                float width = meepleBitmap.getWidth() * scale;
                float height = meepleBitmap.getHeight() * scale;

                // Flooring the width and height prevents rounding errors causing meeples
                // to jiggle around ever so slightly.
                canvas.drawBitmap(meepleBitmap, null, makeRect(
                        x * tileSize + meepleSection.getMeepleX() * this.scale -
                                (float)Math.floor(width / 2),
                        y * tileSize + meepleSection.getMeepleY() * this.scale -
                                (float)Math.floor(height / 2),
                        width,
                        height
                ), null);
            }
        }
    }

    private float getTileSize() {
        return Tile.SIZE * this.scale;
    }

    /**
     * Creates a new RectF for drawing some Bitmap given a position and size and
     * applies scrolling.
     *
     * @param x      The left side of the rectangle.
     * @param y      The top of the rectangle.
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle
     * @return The rect to be used for drawing.
     */
    private RectF makeRect(float x, float y, float width, float height) {
        RectF rect = new RectF();

        rect.left = x - this.scrollX;
        rect.top = y - this.scrollY;
        rect.right = rect.left + width;
        rect.bottom = rect.top + height;

        return rect;
    }
}
