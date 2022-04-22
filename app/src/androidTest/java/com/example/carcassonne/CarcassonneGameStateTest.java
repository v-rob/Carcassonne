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
    CarcassonneGameState gameState;

    @Before
    public void beforeRun() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BitmapProvider.createInstance(appContext.getResources());

        gameState = new CarcassonneGameState(2);

      /* Tile firstTile = new Tile('A');
        gameState.getBoard().setTileDirectly(1,1,firstTile);

        Tile secondTile = new Tile('A');
        secondTile.setRotation(270);
        gameState.getBoard().setTileDirectly(2,1,secondTile);
        secondTile.setMeepleSection(secondTile.getSectionFromPosition(Tile.SIZE / 2, Tile.SIZE / 2));

        Tile thirdTile = new Tile('K');
        gameState.getBoard().setTileDirectly(1,2, thirdTile);

        Tile fourthTile = new Tile('T');
        fourthTile.setRotation(90);
        gameState.getBoard().setTileDirectly(3,1, fourthTile);
        fourthTile.setMeepleSection(fourthTile.getRoadSection(3));

        Tile fifthTile = new Tile('E');
        fifthTile.setRotation(270);
        gameState.getBoard().setTileDirectly(2,2,fifthTile);
        fifthTile.setMeepleSection(fifthTile.getSection(6));

        Tile sixthTile = new Tile('E');
        gameState.getBoard().setTileDirectly(3,2,sixthTile);
        sixthTile.setMeepleSection(sixthTile.getSection(5));

        Tile seventhTile = new Tile('E');
        seventhTile.setRotation(180);
        gameState.getBoard().setTileDirectly(3,1, seventhTile);
        seventhTile.setMeepleSection(seventhTile.getSection(5));

        Tile eighthTile = new Tile('E');
        gameState.getBoard().setTileDirectly(2,1, eighthTile);

        Tile ninthTile = new Tile('E');
        gameState.getBoard().setTileDirectly(1,1, ninthTile);
        */

    }


    @Test
    public void testTurnActions() {

        //Tile testTile = gameState.getBoard().getCurrentTile();
        Board.TilePlacement validPlacement = gameState.getBoard().getValidTilePlacements().get(0);
        assertTrue(gameState.rotateTile(validPlacement.rotation));

        assertTrue(gameState.placeTile(validPlacement.x, validPlacement.y));

        assertTrue(gameState.confirmTile());

        Section validMeeplePlacement = gameState.getBoard().getValidMeeplePlacements().get(0).meepleSection;

        assertTrue(gameState.placeMeeple(validMeeplePlacement));

        assertTrue(gameState.confirmMeeple());


        /*
                this.textBox.clearComposingText();

        //Creating instances
        CarcassonneGameState firstInstance = new CarcassonneGameState(4);
        CarcassonneGameState firstCopy = new CarcassonneGameState(firstInstance);

        //Calls all actions
        firstInstance.placeTile(1, 2, 1);
        this.textBox.append("Player 1 placed the tile at (2, 1)\n");

        firstInstance.rotateTile(1);
        this.textBox.append("Player 1 rotated the current tile\n");

        firstInstance.confirmTile(1);
        this.textBox.append("Player 1 confirmed the current tile placement\n");

        firstInstance.placeMeeple(1, 50, 50);
        this.textBox.append("Player 1 placed a meeple\n");

        firstInstance.confirmMeeple(1);
        this.textBox.append("Player 1 confirmed meeple placement\n");

        firstInstance.placeTile(2, 2, 2);
        this.textBox.append("Player 2 placed the tile at (2, 2)\n");

        firstInstance.confirmTile(2);
        this.textBox.append("Player 2 confirmed the current tile\n");

        firstInstance.resetTurn(2);
        this.textBox.append("Player 2 reset turn\n");

        firstInstance.confirmTile(2);
        this.textBox.append("Player 2 confirmed the current tile\n");

        firstInstance.confirmMeeple(2);
        this.textBox.append("Player 2 confirmed meeple placement\n");

        firstInstance.quitGame(3);
        this.textBox.append("Player 3 quit the game.\n");

        CarcassonneGameState secondInstance = new CarcassonneGameState(4);
        CarcassonneGameState secondCopy = new CarcassonneGameState(secondInstance);

        //Verify the copies are the same
        this.textBox.append("\n Is first copy equal to second copy?\n");
        this.textBox.append(" " + secondInstance.toString().equals(secondCopy.toString()));

        //Print all information to screen
        this.textBox.append("firstCopy: " + firstCopy.toString());
        this.textBox.append("secondCopy: " + secondCopy.toString());
         */
    }

}