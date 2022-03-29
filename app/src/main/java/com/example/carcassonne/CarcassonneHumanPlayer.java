package com.example.carcassonne;

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

    private BitmapProvider bitmapProvider;

    private GameMainActivity activity;

    private TextView[] playerNameTextViews;
    private TextView[] scoreTextViews;
    private TextView[] meepleCountTextViews;

    private Button rotateResetButton;
    private Button confirmButton;
    private Button quitButton;

    private ImageView currentTileImageView;
    private BoardSurfaceView boardSurfaceView;

    public CarcassonneHumanPlayer(String name, BitmapProvider bitmapProvider) {
        super(name);
        this.bitmapProvider = bitmapProvider;
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
        } else {
            this.rotateResetButton.setText("Reset");
        }

        Tile currentTile = this.gameState.getBoard().getCurrentTile();
        if (currentTile != null) {
            this.currentTileImageView.setImageResource(
                    this.bitmapProvider.getTile(currentTile.getId()).tile.resource);
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
        // TODO: Send actions for board touch
        this.boardSurfaceView.setOnTouchListener(this.boardSurfaceView::onTouch);

        // Hand the BitmapProvider to the BoardSurfaceView now that we have it.
        this.boardSurfaceView.setBitmapProvider(this.bitmapProvider);
    }

    private void onClickRotateReset(View button) {
        if (this.gameState.isPlacementStage()) {
            game.sendAction(new CarcassonneRotateTileAction(this));
        } else {
            game.sendAction(new CarcassonneResetTurnAction(this));
        }
    }

    private void onClickConfirm(View button) {
        if (this.gameState.isPlacementStage()) {
            game.sendAction(new CarcassonneConfirmTileAction(this));
        } else {
            game.sendAction(new CarcassonneConfirmMeepleAction(this));
        }
    }

    private void onClickQuit(View button) {
        game.sendAction(new CarcassonneQuitGameAction(this));
    }

    private boolean onTouchTileImage(View view, MotionEvent event) {
        if (!this.gameState.isPlacementStage()) {
            // Convert the scaled display pixels of the ImageView to pixels on an actual tile.
            int x = (int)(event.getX() / this.currentTileImageView.getWidth() * Tile.SIZE);
            int y = (int)(event.getY() / this.currentTileImageView.getHeight() * Tile.SIZE);

            game.sendAction(new CarcassonnePlaceMeepleAction(this, x, y));
        }
        return false;
    }
}
