package com.example.carcassonne;

import com.example.carcassonne.config.GameConfig;
import com.example.carcassonne.config.GamePlayerType;
import java.util.ArrayList;

/* Header comment for final release:
 *
 * Additional features/changes:
 * - The game works in both portrait and landscape mode, with separate GUIs for each.
 * - The board can zoom in and out with pinch gestures.
 *
 * Bugs found and fixed from beta:
 * - None have been found
 *
 * Known bugs:
 * - None
 */

/* Header comment for beta release:
 *
 * Necessary functionality added since alpha:
 * - Scoring and incomplete scoring
 * - Smart AI
 * - Network play
 *
 * Additional features/changes:
 * - Loading screen
 * - Improved graphics
 * - Current player highlight, non-players are hidden
 * - Used Toasts instead of flashing on invalid moves
 * - Show number of tiles left
 *
 * Bugs found and fixed from alpha:
 * - Meeples are now shown next to each player
 * - The code for loading sections had some sections flipped
 * - Bounds checking for meeple placement on the current tile
 *
 * Known bugs:
 * - None--we smashed them all WITH A HAMMER.
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
        BitmapProvider.createInstance(getResources());

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

        playerTypes.add(new GamePlayerType("Computer (Smart)") {
            public GamePlayer createPlayer(String name) {
                return new CarcassonneComputerPlayer(name, true);
            }
        });

        // Create the game configuration object with the max and min number of players
        // and the game name and add the default players to it.
        GameConfig defaultConfig = new GameConfig(playerTypes, 1,
                CarcassonneGameState.MAX_PLAYERS, "Carcassonne", PORT_NUMBER);

        defaultConfig.addPlayer("Count of Anjou", 0);
        defaultConfig.addPlayer("Duke of Touraine", 1);
        defaultConfig.addPlayer("Count of Valois", 2);
        defaultConfig.addPlayer("Baron of Coucy", 1);
        defaultConfig.addPlayer("Count of Toulouse", 2);
        defaultConfig.setRemoteData("Duke of Orléans", "", 0);

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
}
