package com.example.carcassonne;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;

/*
 * External Citation
 * Date: 28 March 2022
 * Problem: Bitmap.getPixel() was returning wacky results.
 * Resource:
 *     https://stackoverflow.com/questions/17800208/bitmap-is-returning-strange-color-values
 * Solution: Stinkin' Android auto-scales bitmaps unless they're in the drawable-nodpi folder.
 *           So, move all the image resources there.
 */

/**
 * Class that loads and holds all bitmaps and image resources used by the game. There is
 * only a single BitmapProvider for the entire game, which can be retrieved via
 * CarcassonneMainActivity.getBitmapProvider().
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class BitmapProvider {
    // TODO: Lazy loading instead of all at once? It may speed up game starting.
    /**
     * Holds the data for a single bitmap, namely the bitmap and its resource. All
     * instance variables are public, but they are final as they should not be modified.
     */
    public class BitmapData {
        /** The resource for this bitmap. */
        public final int resource;
        /** The actual loaded bitmap. */
        public final Bitmap bitmap;

        /**
         * Creates a new BitmapData, loading the bitmap from the specified resource.
         *
         * @param resource The resource to load the bitmap from.
         */
        private BitmapData(int resource) {
            this.resource = resource;
            this.bitmap = BitmapFactory.decodeResource(resources, resource);
        }
    }

    /**
     * Holds the three bitmaps relevant for each tile, namely the meeple placement map
     * bitmap, the section connection and meeple position bitmap, and the visual bitmap
     * that the user sees. Similarly to BitmapData, the variables may not be modified.
     */
    public class TileBitmapData {
        /** The meeple placement collision map bitmap. */
        public final BitmapData map;
        /** The section connection and meeple position bitmap. */
        public final BitmapData section;
        /** The visual bitmap that the user sees. */
        public final BitmapData visual;

        /**
         * Creates a new TileBitmapData, loading each bitmap from its proper resource.
         *
         * @param mapRes     The resource of the map bitmap.
         * @param sectionRes The resource of the section bitmap.
         * @param visualRes  The resource of the visual bitmap.
         */
        private TileBitmapData(int mapRes, int sectionRes, int visualRes) {
            this.map = new BitmapData(mapRes);
            this.section = new BitmapData(sectionRes);
            this.visual = new BitmapData(visualRes);
        }
    }

    /**
     * Holds the bitmaps for a single color of meeple, namely the normal upright meeple
     * and the laying down farmer meeple.
     */
    public class MeepleBitmapData {
        /** The normal upright meeple bitmap. */
        public final BitmapData normal;
        /** The laying down farmer meeple. */
        public final BitmapData farmer;

        /**
         * Creates a new MeepleBitmapData, loading each bitmap from its proper resource.
         *
         * @param normalRes The resource of the normal meeple bitmap.
         * @param farmerRes The resource of the farmer meeple bitmap.
         */
        private MeepleBitmapData(int normalRes, int farmerRes) {
            this.normal = new BitmapData(normalRes);
            this.farmer = new BitmapData(farmerRes);
        }
    }

    /**
     * The Resources object containing the resources of Carcassonne. Note that the
     * BitmapData classes are not static so that they can access this object from their
     * parent BitmapProvider.
     */
    private Resources resources;

    /** A map of tile IDs to tile bitmap data. */
    private HashMap<Character, TileBitmapData> tiles;

    /** The bitmap for the empty tile. */
    private BitmapData emptyTile;

    /** The bitmap for the valid border around the current tile. */
    private BitmapData validBorder;
    /** The bitmap for the invalid border around the current tile. */
    private BitmapData invalidBorder;

    /** The array of player IDs to meeple bitmap data. */
    MeepleBitmapData[] meeples;

    /**
     * Construct a new BitmapData object from the provided pack of resources. Only one
     * BitmapData should ever be created, namely by CarcassonneMainActivity.
     *
     * @param resources The pack of resources to load the resources from.
     */
    public BitmapProvider(Resources resources) {
        this.resources = resources;

        // Fill out the tile bitmap data. There is unfortunately no cleaner way to do
        // this than hardcoding all the IDs.
        this.tiles = new HashMap<>();

        this.tiles.put('A', new TileBitmapData(R.drawable.map_a, R.drawable.section_a, R.drawable.tile_a));
        this.tiles.put('B', new TileBitmapData(R.drawable.map_b, R.drawable.section_b, R.drawable.tile_b));
        this.tiles.put('C', new TileBitmapData(R.drawable.map_c, R.drawable.section_c, R.drawable.tile_c));
        this.tiles.put('D', new TileBitmapData(R.drawable.map_d, R.drawable.section_d, R.drawable.tile_d));
        this.tiles.put('E', new TileBitmapData(R.drawable.map_e, R.drawable.section_e, R.drawable.tile_e));
        this.tiles.put('F', new TileBitmapData(R.drawable.map_f, R.drawable.section_f, R.drawable.tile_f));
        this.tiles.put('G', new TileBitmapData(R.drawable.map_g, R.drawable.section_g, R.drawable.tile_g));
        this.tiles.put('H', new TileBitmapData(R.drawable.map_h, R.drawable.section_h, R.drawable.tile_h));
        this.tiles.put('I', new TileBitmapData(R.drawable.map_i, R.drawable.section_i, R.drawable.tile_i));
        this.tiles.put('J', new TileBitmapData(R.drawable.map_j, R.drawable.section_j, R.drawable.tile_j));
        this.tiles.put('K', new TileBitmapData(R.drawable.map_k, R.drawable.section_k, R.drawable.tile_k));
        this.tiles.put('L', new TileBitmapData(R.drawable.map_l, R.drawable.section_l, R.drawable.tile_l));
        this.tiles.put('M', new TileBitmapData(R.drawable.map_m, R.drawable.section_m, R.drawable.tile_m));
        this.tiles.put('N', new TileBitmapData(R.drawable.map_n, R.drawable.section_n, R.drawable.tile_n));
        this.tiles.put('O', new TileBitmapData(R.drawable.map_o, R.drawable.section_o, R.drawable.tile_o));
        this.tiles.put('P', new TileBitmapData(R.drawable.map_p, R.drawable.section_p, R.drawable.tile_p));
        this.tiles.put('Q', new TileBitmapData(R.drawable.map_q, R.drawable.section_q, R.drawable.tile_q));
        this.tiles.put('R', new TileBitmapData(R.drawable.map_r, R.drawable.section_r, R.drawable.tile_r));
        this.tiles.put('S', new TileBitmapData(R.drawable.map_s, R.drawable.section_s, R.drawable.tile_s));
        this.tiles.put('T', new TileBitmapData(R.drawable.map_t, R.drawable.section_t, R.drawable.tile_t));
        this.tiles.put('U', new TileBitmapData(R.drawable.map_u, R.drawable.section_u, R.drawable.tile_u));
        this.tiles.put('V', new TileBitmapData(R.drawable.map_v, R.drawable.section_v, R.drawable.tile_v));
        this.tiles.put('W', new TileBitmapData(R.drawable.map_w, R.drawable.section_w, R.drawable.tile_w));
        this.tiles.put('X', new TileBitmapData(R.drawable.map_x, R.drawable.section_x, R.drawable.tile_x));

        // Fill out other standalone bitmaps.
        this.emptyTile = new BitmapData(R.drawable.tile_empty);

        this.validBorder = new BitmapData(R.drawable.border_valid);
        this.invalidBorder = new BitmapData(R.drawable.border_invalid);

        // Finally, fill out the meeple bitmap data.
        this.meeples = new MeepleBitmapData[CarcassonneGameState.MAX_PLAYERS];

        this.meeples[0] = new MeepleBitmapData(R.drawable.meeple_blue,   R.drawable.farmer_blue);
        this.meeples[1] = new MeepleBitmapData(R.drawable.meeple_yellow, R.drawable.farmer_yellow);
        this.meeples[2] = new MeepleBitmapData(R.drawable.meeple_green,  R.drawable.farmer_green);
        this.meeples[3] = new MeepleBitmapData(R.drawable.meeple_red,    R.drawable.farmer_red);
        this.meeples[4] = new MeepleBitmapData(R.drawable.meeple_black,  R.drawable.farmer_black);
    }

    /**
     * Get the tile bitmap data for the tile with the specified ID.
     *
     * @param id The tile ID to get the bitmap data for, from A-X
     * @return The tile bitmap data for that ID.
     */
    public TileBitmapData getTile(char id) {
        return this.tiles.get(id);
    }

    /**
     * Retrieve the bitmap data for an empty (null) tile on the board.
     *
     * @return The empty tile bitmap data.
     */
    public BitmapData getEmptyTile() {
        return this.emptyTile;
    }

    /**
     * Retrieve the bitmap data for the border around the current tile when its
     * placement is valid.
     *
     * @return The valid tile border.
     */
    public BitmapData getValidBorder() {
        return this.validBorder;
    }

    /**
     * Retrieve the bitmap data for the border around the current tile when its
     * placement is invalid.
     *
     * @return The invalid tile border.
     */
    public BitmapData getInvalidBorder() {
        return this.invalidBorder;
    }

    /**
     * Return the meeple bitmap data associated with the specified player.
     *
     * @param player The player to get the meeples for. This determines the color.
     * @return The meeple bitmap data for that player.
     */
    public MeepleBitmapData getMeeple(int player) {
        return this.meeples[player];
    }
}
