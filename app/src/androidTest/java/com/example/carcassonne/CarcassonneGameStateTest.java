package com.example.carcassonne;

import junit.framework.TestCase;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CarcassonneGameStateTest extends TestCase {

    @Before
    public void beforeRun() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BitmapProvider.createInstance(appContext.getResources());

        CarcassonneGameState gameState = new CarcassonneGameState(2);

        Tile firstTile = new Tile('a');
        gameState.getBoard().setTileDirectly(1,1,firstTile);

        Tile secondTile = new Tile('a');
        secondTile.setRotation(270);
        gameState.getBoard().setTileDirectly(2,1,secondTile);
        secondTile.setMeepleSection(secondTile.getSectionFromPosition(Tile.SIZE / 2, Tile.SIZE / 2));

        Tile thirdTile = new Tile('k');
        gameState.getBoard().setTileDirectly(1,2, thirdTile);

        Tile fourthTile = new Tile('t');
        fourthTile.setRotation(90);
        gameState.getBoard().setTileDirectly(3,1, fourthTile);
        fourthTile.setMeepleSection(fourthTile.getRoadSection(3));

        Tile fifthTile = new Tile('e');
        fifthTile.setRotation(270);
        gameState.getBoard().setTileDirectly(2,2,fifthTile);
        fifthTile.setMeepleSection(fifthTile.getSection(6));

        Tile sixthTile = new Tile('e');
        gameState.getBoard().setTileDirectly(3,2,sixthTile);
        sixthTile.setMeepleSection(sixthTile.getSection(5));

        Tile seventhTile = new Tile('e');
        seventhTile.setRotation(180);
        gameState.getBoard().setTileDirectly(3,1, seventhTile);
        seventhTile.setMeepleSection(seventhTile.getSection(5));

        Tile eighthTile = new Tile('e');
        gameState.getBoard().setTileDirectly(2,1, eighthTile);

        Tile ninthTile = new Tile('e');
        gameState.getBoard().setTileDirectly(1,1, ninthTile);

    }


    @Test
    public void testPlaceTile() {
    }

    @Test
    public void testConfirmTile() {
    }

    @Test
    public void testPlaceMeeple() {
    }

    @Test
    public void testConfirmMeeple() {
    }
}