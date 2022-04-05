package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameInfo;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
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
            Toast.makeText(this.activity.getApplicationContext(), "That move's invalid.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        this.gameState = (CarcassonneGameState)info;

        // Hand the latest GameState to the BoardSurfaceView and invalidate it so it
        // shows the latest updates.
        this.boardSurfaceView.setGameState(this.gameState);
        this.boardSurfaceView.invalidate();

        // Update the meeples and scores of each player. Also update the names of the
        // players since the GUI will not have the correct names.
        for (int i = 0; i < this.gameState.getNumPlayers(); i++) {
            // TODO: Also show what the scores will be if the tile is confirmed?
            this.playerNameTextViews[i].setText(this.allPlayerNames[i]);
            this.meepleCountTextViews[i].setText("Meeples: " + this.gameState.getPlayerMeeples(i));
            this.scoreTextViews[i].setText("Score: " + this.gameState.getPlayerCompleteScore(i) +
                    " | " + this.gameState.getPlayerIncompleteScore(i));
        }

        // Change the text of the buttons above the current tile to be proper for the
        // current placement stage of the game.
        if (this.gameState.isPlacementStage()) {
            this.rotateResetButton.setText("Rotate");
            this.confirmButton.setText("✓ Tile");
        } else {
            this.rotateResetButton.setText("Reset");
            this.confirmButton.setText("✓ Meeple");
        }

        // Update the current tile image to have the proper resource and rotation.
        Tile currentTile = this.gameState.getBoard().getCurrentTile();
        if (currentTile != null) {
            BitmapProvider bitmapProvider = CarcassonneMainActivity.getBitmapProvider();
            this.currentTileImageView.setImageResource(
                    bitmapProvider.getTile(currentTile.getId()).visual.resource);

            // TODO: Also draw meeples on current tile
            this.currentTileImageView.setRotation(currentTile.getRotation());
        }
    }

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
        activity.setContentView(R.layout.activity_main);

        // TODO: Hide players that aren't playing
        // TODO: Show whose turn it is
        // TODO: Show how many tiles are left

        // Create the arrays for the information pertaining to each player and fill
        // them out with the appropriate GUI objects.
        playerNameTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];
        scoreTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];
        meepleCountTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];

        for (int i = 0; i < CarcassonneGameState.MAX_PLAYERS; i++) {
            playerNameTextViews[i] = activity.findViewById(PLAYER_NAME_RESOURCES[i]);
            scoreTextViews[i] = activity.findViewById(PLAYER_SCORE_RESOURCES[i]);
            meepleCountTextViews[i] = activity.findViewById(MEEPLE_COUNT_RESOURCES[i]);
        }

        // Find all the interactive GUI objects.
        this.rotateResetButton = activity.findViewById(R.id.rotateResetButton);
        this.confirmButton = activity.findViewById(R.id.confirmButton);
        this.quitButton = activity.findViewById(R.id.quitButton);
        this.currentTileImageView = activity.findViewById(R.id.currentTile);
        this.boardSurfaceView = activity.findViewById(R.id.boardSurfaceView);

        // Bind listeners to the interactive GUI objects. The event listeners are
        // method references to listener methods defined in this class.
        this.rotateResetButton.setOnClickListener(this::onClickRotateReset);
        this.confirmButton.setOnClickListener(this::onClickConfirm);
        this.quitButton.setOnClickListener(this::onClickQuit);
        this.currentTileImageView.setOnTouchListener(this::onTouchTileImage);
        this.boardSurfaceView.setOnTouchListener(this::onTouchBoardSurfaceView);
    }

    /**
     * Listener for the Rotate/Reset button; sends either a CarcassonneRotateTileAction
     * or a CarcassonneResetTurnAction depending on which placement stage is current.
     *
     * @param button Unused.
     */
    private void onClickRotateReset(View button) {
        if (this.gameState.isPlacementStage()) {
            // Calculate the new rotation, which is 90 degrees clockwise.
            int rotation = gameState.getBoard().getCurrentTile().getRotation();
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
        if (this.gameState.isPlacementStage()) {
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
        if (!this.gameState.isPlacementStage()) {
            // Convert the scaled display pixels of the ImageView to pixels on a tile
            // bitmap.
            int x = (int)(event.getX() / this.currentTileImageView.getWidth() * Tile.SIZE);
            int y = (int)(event.getY() / this.currentTileImageView.getHeight() * Tile.SIZE);

            // Do NOT rotate the X and Y position; received positions are rotated with
            // the GUI object automatically.
            this.game.sendAction(new CarcassonnePlaceMeepleAction(this, x, y));
        }
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
     * @return True since the even was handled.
     */
    private boolean onTouchBoardSurfaceView(View view, MotionEvent event) {
        // Pass this event to the board's onTouch() method
        Point point = this.boardSurfaceView.onTouch(event);

        // If the board reports that it was pressed and not scrolled, send an action
        if (point != null) {
            this.game.sendAction(new CarcassonnePlaceTileAction(this, point.x, point.y));
        }
        
        // For unknown reasons, ACTION_MOVE and ACTION_UP don't register unless you
        // return true.
        return true;
    }
}
