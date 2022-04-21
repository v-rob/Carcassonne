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