package com.example.carcassonne;

import com.example.carcassonne.config.GameConfig;
import com.example.carcassonne.config.GamePlayerType;
import java.util.ArrayList;

/* Header comment for beta release:
 *
 * Necessary functionality added since alpha:
 *
 * Additional features/changes:
 * - Loading screen
 * - Improved graphics
 * - Current player highlight, non-players are hidden
 * - Used Toasts instead of flashing on invalid moves
 *
 * Bugfixes:
 * - Meeples are now shown next to each player
 * - The code for loading sections had some sections flipped
 *
 * Known bugs:
 * - The game ends right before the last tile is drawn and placed.
 */

/**
 * The entrypoint for the game. It creates the default configuration for the game, the
 * local game controlling the entire game, and the bitmap provider that provides the
 * bitmaps to every part of the game that requires the game's images or resources.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneMainActivity extends GameMainActivity {
    /**
     * The bitmap provider that provides bitmaps to every part of the game that requires
     * them, namely Tile (for the map and section images), CarcassonneHumanPlayer (for
     * the current tile image) and BoardSurfaceView (for the tile images, tile borders,
     * and meeple images).
     *
     * This is static for very good reason: game state objects and objects contained in
     * it must be entirely serializable, so no GUI objects. This makes it very difficult
     * and hackish to store the bitmap provider elsewhere (especially with the organization
     * of the game framework) and pass it up the call chain every time it is required
     * somewhere. Additionally, it doesn't make much sense to make copies of the bitmap
     * provider, and it should never be modified in general, so a static variable is the
     * best solution and very low risk.
     */
    private static BitmapProvider bitmapProvider;

    private static final int PORT_NUMBER = 2278;

    /*
     * External Citation
     * Date: 25 March 2022
     * Problem: Didn't know how to implement the main activity.
     * Resource:
     *     https://github.com/cs301up/PigGameStarter
     * Solution: Copied the code from Pig's main activity and modified it for Carcassonne
     */

    /**
     * Create the default configuration for Carcassonne, which is one human and one
     * dumb computer player. It also gives the game framework information about the
     * maximum and minimum number of players.
     *
     * @return The default game configuration
     */
    @Override
    public GameConfig createDefaultConfig() {
        // Create the bitmap provider that everyone will use now that we have a
        // resources object now before the game starts.
        bitmapProvider = new BitmapProvider(getResources());

        // Define the allowed player types.
        ArrayList<GamePlayerType> playerTypes = new ArrayList<>();

        playerTypes.add(new GamePlayerType("Human (Local)") {
            public GamePlayer createPlayer(String name) {
                return new CarcassonneHumanPlayer(name);
            }
        });

        playerTypes.add(new GamePlayerType("Computer (Dumb)") {
            public GamePlayer createPlayer(String name) {
                return new CarcassonneComputerPlayer(name, false);
            }
        });

        // Create the game configuration object with the max and min number of players
        // and the game name and add the default players to it.
        GameConfig defaultConfig = new GameConfig(playerTypes, 1,
                CarcassonneGameState.MAX_PLAYERS, "Carcassonne", PORT_NUMBER);

        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Computer", 1);
        defaultConfig.setRemoteData("Human (Remote)", "", 0);

        return defaultConfig;
    }

    /**
     * Create the local game class that controls Carcassonne.
     *
     * @return The new local game object.
     */
    @Override
    public LocalGame createLocalGame() {
        return new CarcassonneLocalGame();
    }

    /**
     * Gets the global bitmap provider used by the entire game.
     *
     * @return The bitmap provider.
     */
    public static BitmapProvider getBitmapProvider() {
        return bitmapProvider;
    }
}
