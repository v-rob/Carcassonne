package com.example.carcassonne;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BoardUnitTest {
    /** Tests the default constructor for Board and ensures that it behaves as expected. */
    @Test
    public void testConstructor() {
        TileOLD startingTile = new DeckOLD().drawStartingTile();
        BoardOLD board = new BoardOLD(startingTile);

        // The board should be 3x3.
        assertSame(3, board.getWidth());
        assertSame(3, board.getHeight());

        // Make sure only the middle position is occupied by the given tile; the rest
        // should be null.
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (x == 1 && y == 1) {
                    assertSame(startingTile, board.getTile(1, 1));
                    assertSame(startingTile, board.getConfirmedTile(1, 1));
                } else {
                    assertSame(null, board.getTile(x, y));
                    assertSame(null, board.getConfirmedTile(x, y));
                }
            }
        }

        // There should be no current tile on board creation.
        assertSame(null, board.getCurrentTile());
        assertSame(-1, board.getCurrentTileX());
        assertSame(-1, board.getCurrentTileY());
    }

    /**
     * Test the copy constructor for Board; it should make an exact copy, but none of
     * the references should be the same.
     */
    @Test
    public void testCopyConstructor() {
        DeckOLD deck = new DeckOLD();
        TileOLD startingTile = deck.drawStartingTile();
        TileOLD drawn = deck.drawTile();

        // Create a board and give it a current tile.
        BoardOLD orig = new BoardOLD(startingTile);
        orig.setCurrentTile(drawn);
        orig.setCurrentTilePosition(1, 2);

        // Copy the board and ensure that everything is the same, but all references
        // have changed.
        BoardOLD copy = new BoardOLD(orig);

        // Width and height should be identical.
        assertSame(3, copy.getWidth());
        assertSame(3, copy.getHeight());

        // There should be tiles at the same positions, but not the same references.
        assertNotSame(null, copy.getConfirmedTile(1, 1));
        assertNotSame(orig.getConfirmedTile(1, 1), copy.getConfirmedTile(1, 1));

        assertNotSame(null, copy.getTile(1, 2));
        assertNotSame(orig.getTile(1, 2), copy.getTile(1, 2));

        assertNotSame(null, copy.getCurrentTile());
        assertNotSame(orig.getCurrentTile(), copy.getCurrentTile());

        // All other positions should still be null.
        for (int x = 0; x < copy.getWidth(); x++) {
            for (int y = 0; y < copy.getHeight(); y++) {
                if (orig.getTile(x, y) == null) {
                    assertSame(null, copy.getTile(x, y));
                }
            }
        }
    }

    /**
     * Test all functions relating to the current tile, namely:
     * - setCurrentTile()
     * - setCurrentTilePosition()
     * - resetCurrentTilePosition()
     * - getCurrentTile()
     * - getCurrentTileX()
     * - getCurrentTileY()
     * - getTile() vs getConfirmedTile()
     */
    @Test
    public void testCurrentTile() {
        DeckOLD deck = new DeckOLD();
        BoardOLD board = new BoardOLD(deck.drawStartingTile());

        // Set the current tile. It should be set, but the position invalid at (-1, -1).
        TileOLD drawn = deck.drawTile();
        board.setCurrentTile(drawn);

        assertSame(drawn, board.getCurrentTile());
        assertSame(-1, board.getCurrentTileX());
        assertSame(-1, board.getCurrentTileY());

        // Neither getTile() nor getConfirmedTile() should return the tile anywhere.
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                assertNotSame(drawn, board.getTile(x, y));
                assertNotSame(drawn, board.getConfirmedTile(x, y));
            }
        }

        // Set an absolute position for the tile.
        board.setCurrentTilePosition(2, 1);
        assertSame(drawn, board.getCurrentTile());
        assertSame(2, board.getCurrentTileX());
        assertSame(1, board.getCurrentTileY());

        // The board should not have resized yet.
        assertSame(3, board.getWidth());
        assertSame(3, board.getHeight());

        // Make sure getTile() returns the tile, but not getConfirmedTile().
        assertSame(drawn, board.getTile(2, 1));
        assertSame(null, board.getConfirmedTile(2, 1));

        // Reset the current tile position and make sure it worked.
        board.resetCurrentTilePosition();
        assertSame(-1, board.getCurrentTileX());
        assertSame(-1, board.getCurrentTileY());
    }
}