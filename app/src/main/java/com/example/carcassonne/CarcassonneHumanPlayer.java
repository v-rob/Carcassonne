package com.example.carcassonne;

import android.graphics.Point;
import android.view.View;

import com.example.carcassonne.infoMsg.GameInfo;

import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;

/**
 * Describes the human player for Carcassonne.
 * Sets up GUI and listeners
 *
 * @author Cheyanne Yim
 * @author Sophie Arcangel
 * @author Vincent Robinson
 * @author Alex Martinez-Lopez
 * @author DJ Backus
 */

public class CarcassonneHumanPlayer extends GameHumanPlayer {
    private CarcassonneGameState gameState;

    private GameMainActivity activity;

    private TextView[] playerNameTextViews;
    private TextView[] scoreTextViews;
    private TextView[] meepleCountTextViews;

    private Button rotateResetButton;
    private Button confirmButton;
    private Button quitButton;

    private ImageView currentTileImageView;
    private BoardSurfaceView boardSurfaceView;

    public CarcassonneHumanPlayer(String name) {
        super(name);
    }

    @Override
    public View getTopView() {
        return activity.findViewById(R.id.topView);
    }

    @Override
    public void receiveInfo(GameInfo info) {
        if (!(info instanceof CarcassonneGameState)) {
            flash(0xFFBF0000, 100);
            return;
        }

        this.gameState = (CarcassonneGameState)info;

        // Hand the latest GameState to the BoardSurfaceView
        this.boardSurfaceView.setGameState(this.gameState);

        for (int i = 0; i < this.gameState.getNumPlayers(); i++) {
            this.playerNameTextViews[i].setText(this.allPlayerNames[i]);
            this.meepleCountTextViews[i].setText("Meeples: " + this.gameState.getPlayerMeeples(i));
            this.scoreTextViews[i].setText("Score: " + this.gameState.getPlayerCompleteScore(i) +
                    " | " + this.gameState.getPlayerIncompleteScore(i));
        }

        if (this.gameState.isPlacementStage()) {
            this.rotateResetButton.setText("Rotate");
            this.confirmButton.setText("✓ Tile");
        } else {
            this.rotateResetButton.setText("Reset");
            this.confirmButton.setText("✓ Meeple");
        }

        Tile currentTile = this.gameState.getBoard().getCurrentTile();
        if (currentTile != null) {
            BitmapProvider bitmapProvider = CarcassonneMainActivity.getBitmapProvider();
            this.currentTileImageView.setImageResource(
                    bitmapProvider.getTile(currentTile.getId()).tile.resource);

            // TODO: Also draw meeples on current tile
            this.currentTileImageView.setRotation(currentTile.getRotation());
        }

        // Invalidate the BoardSurfaceView so it shows the new stuff.
        this.boardSurfaceView.invalidate();
    }

    private static final int[] PLAYER_NAME_RESOURCES = {
            R.id.bluePlayerName,
            R.id.yellowPlayerName,
            R.id.greenPlayerName,
            R.id.redPlayerName,
            R.id.blackPlayerName
    };

    private static final int[] PLAYER_SCORE_RESOURCES = {
            R.id.blueScore,
            R.id.yellowScore,
            R.id.greenScore,
            R.id.redScore,
            R.id.blackScore
    };

    private static final int[] MEEPLE_COUNT_RESOURCES = {
            R.id.blueMeepleCount,
            R.id.yellowMeepleCount,
            R.id.greenMeepleCount,
            R.id.redMeepleCount,
            R.id.blackMeepleCount
    };

    @Override
    public void setAsGui(GameMainActivity activity) {
        this.activity = activity;

        activity.setContentView(R.layout.activity_main);

        playerNameTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];
        scoreTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];
        meepleCountTextViews = new TextView[CarcassonneGameState.MAX_PLAYERS];

        // TODO: Hide players that aren't playing

        for (int i = 0; i < CarcassonneGameState.MAX_PLAYERS; i++) {
            playerNameTextViews[i] = activity.findViewById(PLAYER_NAME_RESOURCES[i]);
            scoreTextViews[i] = activity.findViewById(PLAYER_SCORE_RESOURCES[i]);
            meepleCountTextViews[i] = activity.findViewById(MEEPLE_COUNT_RESOURCES[i]);
        }

        this.rotateResetButton = activity.findViewById(R.id.rotateResetButton);
        this.confirmButton = activity.findViewById(R.id.confirmButton);
        this.quitButton = activity.findViewById(R.id.quitButton);
        this.currentTileImageView = activity.findViewById(R.id.currentTile);
        this.boardSurfaceView = activity.findViewById(R.id.boardSurfaceView);

        this.rotateResetButton.setOnClickListener(this::onClickRotateReset);
        this.confirmButton.setOnClickListener(this::onClickConfirm);
        this.quitButton.setOnClickListener(this::onClickQuit);
        this.currentTileImageView.setOnTouchListener(this::onTouchTileImage);
        this.boardSurfaceView.setOnTouchListener(this::onTouchBoardSurfaceView);
    }

    private void onClickRotateReset(View button) {
        if (this.gameState.isPlacementStage()) {
            this.game.sendAction(new CarcassonneRotateTileAction(this));
        } else {
            this.game.sendAction(new CarcassonneResetTurnAction(this));
        }
    }

    private void onClickConfirm(View button) {
        if (this.gameState.isPlacementStage()) {
            this.game.sendAction(new CarcassonneConfirmTileAction(this));
        } else {
            this.game.sendAction(new CarcassonneConfirmMeepleAction(this));
        }
    }

    private void onClickQuit(View button) {
        this.game.sendAction(new CarcassonneQuitGameAction(this));
    }

    private boolean onTouchTileImage(View view, MotionEvent event) {
        if (!this.gameState.isPlacementStage()) {
            // Convert the scaled display pixels of the ImageView to pixels on a tile bitmap.
            int x = (int)(event.getX() / this.currentTileImageView.getWidth() * Tile.SIZE);
            int y = (int)(event.getY() / this.currentTileImageView.getHeight() * Tile.SIZE);

            this.game.sendAction(new CarcassonnePlaceMeepleAction(this, x, y));
        }
        return true;
    }

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
