package com.example.carcassonne;

import com.example.carcassonne.config.GameConfig;
import com.example.carcassonne.config.GamePlayerType;
import java.util.ArrayList;


/**
 * Creates the Carcassonne game to be played.
 * Includes the configuration of the game.
 *
 *
 * @author DJ Backus
 * @author Vincent Robinson
 * @author Alex Martinex-Lopez
 * @author Cheyanne Yim
 * @author Sophie Arcangel
 */
public class CarcassonneMainActivity extends GameMainActivity {
    /**
     * The bitmap provider that provides bitmaps to every part of the game that requires
     * them, namely to Tile through Deck, CarcassonneGameState, and CarcassonneLocalGame,
     * and to BoardSurfaceView through CarcassonneHumanPlayer.
     */
    private BitmapProvider bitmapProvider;

    private static final int PORT_NUMBER = 2278;

    /**
     External Citation
     Date: 25 March 2022
     Problem: Didn't know how to implement the main activity method
     Resource: https://github.com/cs301up/PigGameStarter
     Solution: We copied the code from Pig's main activity and
     modified it for Carcassonne
     */

    @Override
    public GameConfig createDefaultConfig() {
        // Create the bitmap provider that everyone will use
        this.bitmapProvider = new BitmapProvider(getResources());

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
        return new CarcassonneLocalGame(this.bitmapProvider);
    }
}
