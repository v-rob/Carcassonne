package com.example.carcassonne;

import android.view.View;

import com.example.carcassonne.infoMsg.GameInfo;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;

public class CarcassonneHumanPlayer extends GameHumanPlayer{
    private CarcassonneGameState gameState;
    private GameMainActivity activity;
    private TextView[] playerNameTextViews;
    private TextView[] scoreTextViews;
    private TextView[] meepleCountTextViews;
    private Button rotateResetButton;
    private Button confirmButton;
    private Button quitButton;
    private ImageView tileImageView;
    private BoardSurfaceView boardSurfaceView;




    /**
     * constructor
     *
     * @param name the name of the player
     */
    public CarcassonneHumanPlayer(String name) {
        super(name);
    }

    @Override
    public View getTopView() {
        return activity.findViewById(R.id.topView);
    }

    @Override
    public void receiveInfo(GameInfo info) {
       CarcassonneGameState gameState = (CarcassonneGameState)info;
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

        playerNameTextViews = new TextView[playerNum];
        scoreTextViews = new TextView[playerNum];
        meepleCountTextViews = new TextView[playerNum];

        for (int i = 0; i < playerNum; i++) {
            playerNameTextViews[i] = activity.findViewById(PLAYER_NAME_RESOURCES[i]);
            scoreTextViews[i] = activity.findViewById(PLAYER_SCORE_RESOURCES[i]);
            meepleCountTextViews[i] = activity.findViewById(MEEPLE_COUNT_RESOURCES[i]);
        }

        this.rotateResetButton = activity.findViewById(R.id.rotateResetButton);
        this.confirmButton = activity.findViewById(R.id.confirmButton);
        this.quitButton = activity.findViewById(R.id.quitButton);
        this.tileImageView = activity.findViewById(R.id.currentTile);
        this.boardSurfaceView = activity.findViewById(R.id.boardSurfaceView);

        this.rotateResetButton.setOnClickListener(this::onClickRotateReset);
        this.confirmButton.setOnClickListener(this::onClickConfirm);
        this.quitButton.setOnClickListener(this::onClickQuit);
        this.tileImageView.setOnTouchListener(this::onTouchTileImage);
        this.boardSurfaceView.setOnTouchListener(this::onTouchBoardSurfaceView);
    }

    private void onClickRotateReset(View button){
        if (this.gameState.isPlacementStage()) {
            game.sendAction(new CarcassonneRotateTileAction(this));
        } else {
            game.sendAction(new CarcassonneResetTurnAction(this));
        }
    }

    private void onClickConfirm(View button){
        if (this.gameState.isPlacementStage()) {
            game.sendAction(new CarcassonneConfirmTileAction(this));
        } else {
            game.sendAction(new CarcassonneConfirmMeepleAction(this));
        }
    }

    private void onClickQuit(View button){
            game.sendAction(new CarcassonneQuitGameAction(this));
    }

    private boolean onTouchTileImage(View view, MotionEvent event){
        // TODO: Make sure units are correct.
        if (!this.gameState.isPlacementStage()) {
            game.sendAction(new CarcassonnePlaceMeepleAction(this,
                    (int) event.getX(), (int) event.getY()));
        }
        return false;
    }

    private boolean onTouchBoardSurfaceView(View view, MotionEvent event){
        return false;
    }

}
