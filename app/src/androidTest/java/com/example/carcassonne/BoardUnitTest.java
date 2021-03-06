package com.example.carcassonne;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Tests everything relating to the Board class
 */
@RunWith(AndroidJUnit4.class)
public class BoardUnitTest {
    @Before
    public void beforeRun() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BitmapProvider.createInstance(appContext.getResources());
    }

    /** Tests the default constructor for Board and ensures that it behaves as expected. */
    @Test
    public void testConstructor() {
        Tile startingTile = new Deck().drawStartingTile();
        Board board = new Board(startingTile);

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
    }

    /**
     * Test the copy constructor for Board; it should make an exact copy, but none of
     * the references should be the same.
     */
    @Test
    public void testCopyConstructor() {
        Deck deck = new Deck();
        Tile startingTile = deck.drawStartingTile();
        Tile drawn = deck.drawTile(0);

        // Create a board and give it a current tile.
        Board orig = new Board(startingTile);
        orig.setCurrentTile(drawn);
        drawn.setPosition(1, 2);

        // Copy the board and ensure that everything is the same, but all references
        // have changed.
        Board copy = new Board(orig);

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
     * - getTile() vs getConfirmedTile()
     */
    @Test
    public void testCurrentTile() {
        Deck deck = new Deck();
        Board board = new Board(deck.drawStartingTile());

        // Set the current tile. It should be set, but the position invalid at (-1, -1).
        Tile drawn = deck.drawTile(0);
        board.setCurrentTile(drawn);

        assertSame(drawn, board.getCurrentTile());

        // Neither getTile() nor getConfirmedTile() should return the tile anywhere.
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                assertNotSame(drawn, board.getTile(x, y));
                assertNotSame(drawn, board.getConfirmedTile(x, y));
            }
        }

        // Set an absolute position for the tile.
        board.getCurrentTile().setPosition(2, 1);
        assertSame(drawn, board.getCurrentTile());
        assertSame(2, board.getCurrentTile().getX());
        assertSame(1, board.getCurrentTile().getY());

        // The board should not have resized yet.
        assertSame(3, board.getWidth());
        assertSame(3, board.getHeight());

        // Make sure getTile() returns the tile, but not getConfirmedTile().
        assertSame(drawn, board.getTile(2, 1));
        assertSame(null, board.getConfirmedTile(2, 1));
    }
}