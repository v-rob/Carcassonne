package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameInfo;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Represents a human player for Carcassonne, mainly consisting of GUI objects and
 * listeners for receiving input from the user, sending them to the local game, and
 * displaying game information back to the user in response to changes in the state
 * of the game.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneHumanPlayer extends GameHumanPlayer {
    /**
     * The main activity of the entire game received in setAsGui(), used for getting
     * the necessary GUI objects.
     */
     private GameMainActivity activity;

    /** The game state of the game, received in the last receiveInfo() callback. */
    private CarcassonneGameState gameState;

    /** The table rows holding the information for each player. **/
    private TableRow[] playerTableRows;

    /**
     * The text views representing the name of each player. It includes all five GUI
     * objects even when there are less than five players.
     */
    private TextView[] playerNameTextViews;

    /**
     * The text views representing the score of each player, a combination of both
     * complete and incomplete scores. It includes all five GUI objects even when
     * there are less than five players.
     */
    private TextView[] scoreTextViews;

    /**
     * The text views representing the meeple counts of each player. It includes all
     * five GUI objects even when there are less than five players.
     */
    private TextView[] meepleCountTextViews;

    /**
     * The button used for rotating the current tile or resetting back to tile
     * placement stage from meeple placement stage. The text on the button is changed
     * when the placement stage changes.
     */
    private Button rotateResetButton;

    /** The text view containing the number of tiles left in the game. */
    private TextView tilesLeftTextView;

    /**
     * The button used for confirming both the current tile placement and the current
     * meeple placement. The text on the button is changed when the placement stage
     * ends.
     */
    private Button confirmButton;

    /** The button that immediately quits the application. */
    private Button quitButton;

    /** The image representing the current tile and its rotation. */
    private ImageView currentTileImageView;
    /** The surface view that draws the entire current state of the board. */
    private BoardSurfaceView boardSurfaceView;

    /** Contains the main gameplay (i.e. not loading) screen **/
    private LinearLayout mainLayout;

    /** Contains the loading screen **/
    private LinearLayout loadingScreen;

    /**
     * A reference to the last toast that was popped up when the player made an invalid
     * move, or null if no toasts have popped up yet.
     */
    Toast lastToast;

    /** Array of the resources of each player information table row. */
    private static final int[] PLAYER_TABLE_ROW_RESOURCES = {
            R.id.bluePlayer,
            R.id.yellowPlayer,
            R.id.greenPlayer,
            R.id.redPlayer,
            R.id.blackPlayer
    };

    /** Array of the resources of each player name GUI object. */
    private static final int[] PLAYER_NAME_RESOURCES = {
            R.id.bluePlayerName,
            R.id.yellowPlayerName,
            R.id.greenPlayerName,
            R.id.redPlayerName,
            R.id.blackPlayerName
    };

    /** Array of the resources of each player score GUI object. */
    private static final int[] PLAYER_SCORE_RESOURCES = {
            R.id.blueScore,
            R.id.yellowScore,
            R.id.greenScore,
            R.id.redScore,
            R.id.blackScore
    };

    /** Array of the resources of each player meeple count GUI object. */
    private static final int[] MEEPLE_COUNT_RESOURCES = {
            R.id.blueMeepleCount,
            R.id.yellowMeepleCount,
            R.id.greenMeepleCount,
            R.id.redMeepleCount,
            R.id.blackMeepleCount
    };

    /**
     * Array of all the current player background highlight colors. They are all
     * partially transparent to let the off-white background color blend in.
     */
    private static final int[] PLAYER_HIGHLIGHT_COLORS = {
            0x3F5454AA,
            0x4Ff0e34c,
            0x4862AD46,
            0x28C30017,
            0x381D1D1D
    };

    /**
     * Creates a new human player with the specified name.
     *
     * @param name The name of the player to create.
     */
    public CarcassonneHumanPlayer(String name) {
        super(name);
    }

    /**
     * Gets the topmost GUI object in the game's GUI.
     *
     * @return The topmost GUI object.
     */
    @Override
    public View getTopView() {
        return activity.findViewById(R.id.topView);
    }

    /**
     * Called when the player receives game state information. It updates all GUI objects
     * to reflect what the state of the game is.
     *
     * @param info The game state to be received, or any other type for an error flash.
     */
    @Override
    public void receiveInfo(GameInfo info) {
        if (!(info instanceof CarcassonneGameState)) {
            /* External Citation
             * Date: 4 April 2022
             * Problem: Wanted a nicer (and less buggy) thing than flashes
             * Resource:
             *     https://developer.android.com/guide/topics/ui/notifiers/toasts#java
             * Solution: Used Toast.makeText().show().
             *
             * Flashes aren't very nice in terms of looks, and they're also buggy because
             * receiving two invalid infos in quick succession causes a race condition
             * between two flashes, causing the screen to get stuck at red. So, use toasts
             * instead, which look nicer as well.
             */

            // If we already have a toast, hide it so they don't pile up like crazy and
            // take forever to disappear.
            if (this.lastToast != null) {
                this.lastToast.cancel();
            }

            this.lastToast = Toast.makeText(this.activity.getApplicationContext(),
                    "That move's invalid.", Toast.LENGTH_SHORT);
            this.lastToast.show();
            return;
        }

        this.gameState = (CarcassonneGameState)info;

        // Hand the latest board to the BoardSurfaceView and invalidate it so it
        // shows the latest updates.
        Board board = this.gameState.getBoard();
        this.boardSurfaceView.setBoard(board);
        this.boardSurfaceView.invalidate();

        /*
         * External Citation
         * Date: 6 April 2022
         * Problem: Required a way to set the visibility of a GUI object programmatically.
         * Resource:
         *     https://developer.android.com/reference/android/view/View#setVisibility(int)
         * Solution: Used View.setVisibility() with View.VISIBLE/View.GONE.
         */

        // Hide the player table rows for the players who are not playing. It only needs
        // to be done once, but setAsGui() doesn't know how many players are playing.
        for (int i = CarcassonneGameState.MAX_PLAYERS - 1; i >= this.allPlayerNames.length; i--) {
            this.playerTableRows[i].setVisibility(View.GONE);
        }

        // Update the data for each player.
        for (int i = 0; i < this.gameState.getNumPlayers(); i++) {
            // Setting names only has to be done once, but setAsGui() doesn't know the
            // names of the player.
            this.playerNameTextViews[i].setText(this.allPlayerNames[i]);

            // Update the meeple counts and scores.
            this.meepleCountTextViews[i].setText("Meeples: " +
                    this.gameState.getPlayerMeeples(i));
            this.scoreTextViews[i].setText("Score: " +
                    this.gameState.getPlayerCompleteScore(i) +
                    " | Partial: " + this.gameState.getPlayerIncompleteScore(i));

            // Set the background color back to transparent to remove current player
            // highlights.
            this.playerTableRows[i].setBackgroundColor(0x0);
        }

        // Highlight the background of the current player.
        int currentPlayer = this.gameState.getCurrentPlayer();
        this.playerTableRows[currentPlayer].setBackgroundColor(PLAYER_HIGHLIGHT_COLORS[currentPlayer]);

        // Update the number of tiles left in the deck.
        if (this.gameState.isGameOver()) {
            // There are no tiles if the game is over.
            this.tilesLeftTextView.setText("Tiles left: 0");
        } else {
            // The total number of tiles is the number of tiles left in the deck plus
            // the current tile.
            Deck deck = this.gameState.getDeck();
            this.tilesLeftTextView.setText("Tiles left: " + (deck.getTilesLeft() + 1));
        }

        // Change the text of the buttons above the current tile to be proper for the
        // current placement stage of the game.
        if (this.gameState.isTileStage()) {
            this.rotateResetButton.setText("Rotate");
            this.confirmButton.setText("✓ Tile");
        } else {
            this.rotateResetButton.setText("Reset");
            this.confirmButton.setText("✓ Meeple");
        }

        // Update the current tile image to have the proper resource and rotation.
        Tile currentTile = board.getCurrentTile();
        BitmapProvider bitmapProvider = CarcassonneMainActivity.getBitmapProvider();

        if (currentTile == null) {
            // There is no image after the game has ended.
            this.currentTileImageView.setImageResource(bitmapProvider.getEmptyTile().resource);
        } else {
            // Otherwise, use the proper image.
            this.currentTileImageView.setImageResource(
                    bitmapProvider.getTile(currentTile.getId()).visual.resource);
            // TODO: Also draw meeples on current tile
            this.currentTileImageView.setRotation(currentTile.getRotation());
        }

        // Once the game has finished loading everything and sends the game state to
        // each player, the game has started and we can remove the loading screen. If
        // this is a later receiveInfo() call, this is effectively a no-op.
        this.loadingScreen.setVisibility(View.GONE);
        this.mainLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up the human player, getting the references to each GUI object and
     * binding event listeners to the interactive ones.
     *
     * @param activity The main activity of the entire application.
     */
    @Override
    public void setAsGui(GameMainActivity activity) {
        this.activity = activity;

        // Set this GUI as the one we're using now that the game configuration
        // screen is through.
        this.activity.setContentView(R.layout.activity_main);

        // Create the arrays for the information pertaining to each player and fill
        // them out with the appropriate GUI objects.
        this.playerTableRows = new TableRow[CarcassonneGameState.MAX_PLAYERS];
        this.playerNameTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];
        this.scoreTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];
        this.meepleCountTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];

        for (int i = 0; i < CarcassonneGameState.MAX_PLAYERS; i++) {
            this.playerTableRows[i] = activity.findViewById(PLAYER_TABLE_ROW_RESOURCES[i]);
            this.playerNameTextViews[i] = activity.findViewById(PLAYER_NAME_RESOURCES[i]);
            this.scoreTextViews[i] = activity.findViewById(PLAYER_SCORE_RESOURCES[i]);
            this.meepleCountTextViews[i] = activity.findViewById(MEEPLE_COUNT_RESOURCES[i]);
        }

        // Find all the other GUI objects.
        this.tilesLeftTextView = activity.findViewById(R.id.tilesLeft);

        this.rotateResetButton = activity.findViewById(R.id.rotateResetButton);
        this.confirmButton = activity.findViewById(R.id.confirmButton);
        this.quitButton = activity.findViewById(R.id.quitButton);

        this.currentTileImageView = activity.findViewById(R.id.currentTile);
        this.boardSurfaceView = activity.findViewById(R.id.boardSurfaceView);

        this.mainLayout = activity.findViewById(R.id.gameView);
        this.loadingScreen = activity.findViewById(R.id.loadingLayout);

        // Bind listeners to the interactive GUI objects. The event listeners are
        // method references to listener methods defined in this class.
        this.rotateResetButton.setOnClickListener(this::onClickRotateReset);
        this.confirmButton.setOnClickListener(this::onClickConfirm);
        this.quitButton.setOnClickListener(this::onClickQuit);
        this.currentTileImageView.setOnTouchListener(this::onTouchTileImage);
        this.boardSurfaceView.setOnTouchListener(this::onTouchBoardSurfaceView);

        // Hide the main layout and show the loading screen. The loading screen will be
        // hidden after the first receiveInfo() call.
        this.mainLayout.setVisibility(View.GONE);
        this.loadingScreen.setVisibility(View.VISIBLE);
    }

    /**
     * Listener for the Rotate/Reset button; sends either a CarcassonneRotateTileAction
     * or a CarcassonneResetTurnAction depending on which placement stage is current.
     *
     * @param button Unused.
     */
    private void onClickRotateReset(View button) {
        if (this.gameState.isTileStage()) {
            Tile currentTile = this.gameState.getBoard().getCurrentTile();
            if (currentTile == null) {
                // The game has ended; there is no current tile.
                return;
            }

            // Calculate the new rotation, which is 90 degrees clockwise.
            int rotation = currentTile.getRotation();
            rotation = (rotation + 90) % 360;
            this.game.sendAction(new CarcassonneRotateTileAction(this, rotation));
        } else {
            this.game.sendAction(new CarcassonneResetTurnAction(this));
        }
    }

    /**
     * Listener for the Confirm Tile/Meeple button; sends either a
     * CarcassonneConfirmTileAction or a CarcassonneConfirmMeepleAction depending on
     * which placement stage is current.
     *
     * @param button Unused.
     */
    private void onClickConfirm(View button) {
        if (this.gameState.isTileStage()) {
            this.game.sendAction(new CarcassonneConfirmTileAction(this));
        } else {
            this.game.sendAction(new CarcassonneConfirmMeepleAction(this));
        }
    }

    /**
     * Listener for the Quit button; quits the application immediately.
     *
     * @param button Unused.
     */
    private void onClickQuit(View button) {
        /*
         * External Citation
         * Date: 28 March 2022
         * Problem: Didn't know the proper way to kill an Android application.
         * Resource:
         *     https://www.codegrepper.com/code-examples/java/quit+android+app+programmatically
         * Solution: The following method call can close an Android application from
         *           anywhere in the code.
         */
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * Called when the current tile image is touched. If it is the meeple placement
     * stage, a CarcassonnePlaceMeepleAction is send with the touched X and Y.
     *
     * @param view Unused.
     * @param event The motion event containing the touched X and Y position.
     * @return True since the event was handled.
     */
    private boolean onTouchTileImage(View view, MotionEvent event) {
        // Do nothing if it isn't the meeple placement stage.
        if (this.gameState.isTileStage()) {
            return true;
        }

        // Convert the scaled display pixels of the ImageView to pixels on a tile
        // bitmap.
        int x = (int)(event.getX() / this.currentTileImageView.getWidth() * Tile.SIZE);
        int y = (int)(event.getY() / this.currentTileImageView.getHeight() * Tile.SIZE);

        // It's possible for the very edges of the ImageView to return out of
        // bounds positions for the map image, so do bounds checking.
        if (x < 0 || y < 0 || x >= Tile.SIZE || y >= Tile.SIZE) {
            return true;
        }

        // Do NOT rotate the X and Y position; received positions are rotated with
        // the GUI object automatically.
        Tile currentTile = this.gameState.getBoard().getCurrentTile();
        Section section = currentTile.getSectionFromPosition(x, y);
        this.game.sendAction(new CarcassonnePlaceMeepleAction(this, section));

        return true;
    }

    /**
     * Called when the board surface view is touched or scrolled. This method passes
     * on its argument to BoardSurfaceView.onTouch() for scrolling the board and
     * placing tiles. If that method returns a position indicating that the player
     * wishes to place a tile, send a CarcassonnePlaceTileAction with that position.
     *
     * @param view Unused.
     * @param event The motion event to pass along to BoardSurfaceView.onTouch().
     * @return True since the event was handled.
     */
    private boolean onTouchBoardSurfaceView(View view, MotionEvent event) {
        // Pass this event to the board's onTouch() method
        Point point = this.boardSurfaceView.onTouch(event);

        // If the board reports that it was pressed and not scrolled, send an action
        // TODO: Allow placing meeple from board
        if (point != null) {
            this.game.sendAction(new CarcassonnePlaceTileAction(this, point.x, point.y));
        }
        
        // For unknown reasons, ACTION_MOVE and ACTION_UP don't register unless you
        // return true.
        return true;
    }
}
