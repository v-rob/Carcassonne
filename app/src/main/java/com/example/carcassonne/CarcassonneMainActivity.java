package com.example.carcassonne;

import com.example.carcassonne.config.GameConfig;
import com.example.carcassonne.config.GamePlayerType;
import java.util.ArrayList;

public class CarcassonneMainActivity extends GameMainActivity {
    /**
     * The bitmap provider that provides bitmaps to every part of the game that requires
     * them, namely to Tile through Deck, CarcassonneGameState, and CarcassonneLocalGame,
     * and to BoardSurfaceView through CarcassonneHumanPlayer.
     */
    private BitmapProvider bitmapProvider;

    private static final int PORT_NUMBER = 2278;

    //TODO: Comments to give Pig Credit Mar 25

    public CarcassonneMainActivity() {
        this.bitmapProvider = new BitmapProvider(getResources());
    }

    @Override
    public GameConfig createDefaultConfig() {
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<>();

        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new CarcassonneHumanPlayer(name, bitmapProvider);
            }
        });
        playerTypes.add(new GamePlayerType("Dumb Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new CarcassonneComputerPlayer(name, false);
            }
        });

        // Create a game configuration class for Carcassonne
        GameConfig defaultConfig = new GameConfig(playerTypes, 1, 2, "Carcassonne", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Computer", 1);
        defaultConfig.setRemoteData("Remote Human Player", "", 0);

        return defaultConfig;
    }

    @Override
    public LocalGame createLocalGame() {
        return new CarcassonneLocalGame();
    }
}
