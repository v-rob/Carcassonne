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

public class BitmapProvider {
    public class BitmapData {
        public int resource;
        public Bitmap bitmap;

        public BitmapData(int resource) {
            this.resource = resource;
            this.bitmap = BitmapFactory.decodeResource(resources, resource);
        }
    }

    public class TileBitmapData {
        public BitmapData map;
        public BitmapData section;
        public BitmapData tile;

        public TileBitmapData(int mapRes, int sectionRes, int tileRes) {
            this.map = new BitmapData(mapRes);
            this.section = new BitmapData(sectionRes);
            this.tile = new BitmapData(tileRes);
        }
    }

    public class MeepleBitmapData {
        public BitmapData meeple;
        public BitmapData farmer;

        public MeepleBitmapData(int meepleRes, int farmerRes) {
            this.meeple = new BitmapData(meepleRes);
            this.farmer = new BitmapData(farmerRes);
        }
    }

    private Resources resources;

    private HashMap<Character, TileBitmapData> tiles;

    private BitmapData emptyTile;

    private BitmapData validBorder;
    private BitmapData invalidBorder;

    MeepleBitmapData[] meeples;

    public BitmapProvider(Resources resources) {
        this.resources = resources;

        this.tiles = new HashMap<>();
        this.meeples = new MeepleBitmapData[CarcassonneGameState.MAX_PLAYERS];

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

        this.emptyTile = new BitmapData(R.drawable.tile_empty);

        this.validBorder = new BitmapData(R.drawable.border_valid);
        this.invalidBorder = new BitmapData(R.drawable.border_invalid);

        this.meeples[0] = new MeepleBitmapData(R.drawable.meeple_blue,   R.drawable.farmer_blue);
        this.meeples[1] = new MeepleBitmapData(R.drawable.meeple_yellow, R.drawable.farmer_yellow);
        this.meeples[2] = new MeepleBitmapData(R.drawable.meeple_green,  R.drawable.farmer_green);
        this.meeples[3] = new MeepleBitmapData(R.drawable.meeple_red,    R.drawable.farmer_red);
        this.meeples[4] = new MeepleBitmapData(R.drawable.meeple_black,  R.drawable.farmer_black);
    }

    public TileBitmapData getTile(char id) {
        return this.tiles.get(id);
    }

    public BitmapData getEmptyTile() {
        return this.emptyTile;
    }

    public BitmapData getValidBorder() {
        return this.validBorder;
    }

    public BitmapData getInvalidBorder() {
        return this.invalidBorder;
    }

    public MeepleBitmapData getMeeple(int player) {
        return this.meeples[player];
    }

    public static int[][] toArray(Bitmap bitmap) {
        /*
         * External Citation
         * Date: 24 March 2022
         * Problem: Needed ways to extract pixel information from Bitmaps
         * Resource:
         *     https://developer.android.com/reference/android/graphics/Bitmap
         * Solution: Used getWidth(), getHeight(), and getPixel()
         */
        int[][] arr = new int[bitmap.getHeight()][bitmap.getWidth()];

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                arr[y][x] = bitmap.getPixel(x, y);
            }
        }

        return arr;
    }
}
