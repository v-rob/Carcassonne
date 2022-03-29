package com.example.carcassonne;

import com.example.carcassonne.config.GameConfig;
import com.example.carcassonne.config.GamePlayerType;
import java.util.ArrayList;

public class CarcassonneMainActivity extends GameMainActivity {
    /**
     * The bitmap provider that provides bitmaps to every part of the game, namely
     * to Tile through Deck, CarcassonneGameState, and CarcassonneLocalGame, and to
     * BoardSurfaceView through CarcassonneHumanPlayer.
     */
    private BitmapProvider bitmapProvider = new BitmapProvider(getResources());

    private static final int PORT_NUMBER = 2278;

    //TODO: Comments to give Pig Credit Mar 25

    @Override
    public GameConfig createDefaultConfig() {
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // Pig has two player types:  human and computer
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new CarcassonneHumanPlayer(name, bitmapProvider);
            }
        });
        playerTypes.add(new GamePlayerType("Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new CarcassonneComputerPlayer(name, false);
            }
        });

        // Create a game configuration class for Pig:
        GameConfig defaultConfig = new GameConfig(playerTypes, 1, 2, "Carcassonne", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer", 1); // player 2: a computer player
        defaultConfig.setRemoteData("Remote Human Player", "", 0);

        return defaultConfig;
    }

    @Override
    public LocalGame createLocalGame() {
        return new CarcassonneLocalGame();
    }
}
