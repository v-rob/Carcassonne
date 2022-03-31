package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

import java.util.ArrayList;

public class CarcassonneLocalGame extends LocalGame {
    private CarcassonneGameState gameState;

    @Override
    protected void sendUpdatedStateTo(GamePlayer player) {
        // We have to create the game state here
        if (this.gameState == null) {
            this.gameState = new CarcassonneGameState(this.players.length);
        }

        player.sendInfo(new CarcassonneGameState(this.gameState));
    }

    @Override
    protected boolean canMove(int player) {
        return this.gameState.getCurrentPlayer() == player;
    }

    @Override
    protected String checkIfGameOver() {
        if (this.gameState.getDeck().isEmpty()) {
            int highestScore = 0;
            ArrayList<String> highestNames = new ArrayList<>();

            for (int i = 0; i < this.gameState.getNumPlayers(); i++) {
                int score = this.gameState.getPlayerScore(i);
                if (score > highestScore) {
                    highestNames.clear();
                }

                if (score >= highestScore) {
                    highestNames.add(this.playerNames[i]);
                }
            }

            StringBuilder names = new StringBuilder("The winners are ");
            for (int i = 0; i < highestNames.size(); i++) {
                names.append(highestNames.get(i));

                if (i < highestNames.size() - 2) {
                    names.append(", ");
                } else if (i == highestNames.size() - 2) {
                    names.append(", and ");
                }
            }

            return names.toString();
        }

        return null;
    }

    @Override
    protected boolean makeMove(GameAction action) {
        if (action instanceof CarcassonnePlaceTileAction) {
            CarcassonnePlaceTileAction placeTileAction = (CarcassonnePlaceTileAction)action;
            return this.gameState.placeTile(placeTileAction.getX(), placeTileAction.getY());
        } else if (action instanceof CarcassonneRotateTileAction) {
            CarcassonneRotateTileAction rotateTileAction = (CarcassonneRotateTileAction)action;
            return this.gameState.rotateTile(((CarcassonneRotateTileAction) action).getRotation());
        } else if (action instanceof CarcassonneConfirmTileAction) {
            return this.gameState.confirmTile();
        } else if (action instanceof CarcassonneResetTurnAction) {
            return this.gameState.resetTurn();
        } else if (action instanceof CarcassonnePlaceMeepleAction) {
            CarcassonnePlaceMeepleAction placeMeepleAction = (CarcassonnePlaceMeepleAction)action;
            return this.gameState.placeMeeple(placeMeepleAction.getX(), placeMeepleAction.getY());
        } else if (action instanceof CarcassonneConfirmMeepleAction) {
            return this.gameState.confirmMeeple();
        }

        return false;
    }
}
