package com.example.carcassonne;

import android.view.View;

import com.example.carcassonne.infoMsg.GameInfo;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;
//import board

public class CarcassonneHumanPlayer extends GameHumanPlayer{
    private CarcassonneGameState gameState;
    private GameMainActivity activity;
    private TextView playerNameTextView;
    private TextView completeScoreTextView;
    private TextView incompleteScoreTextView;
    private TextView meepleCountTextView;
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
        return null;
    }

    @Override
    public void receiveInfo(GameInfo info) {

    }

    @Override
    public void setAsGui(GameMainActivity activity) {

    }

    private void onClickRotateReset(View button){

    }
    private void onClickConfirm(View button){

    }
    private void onClickQuit(View button){

    }

    private boolean onTouchTileImage(View view, MotionEvent event){
        return false;
    }

    private boolean onTouchBoardSurfaceView(View view, MotionEvent event){
        return false;
    }

}
