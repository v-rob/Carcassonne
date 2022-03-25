package com.example.carcassonne;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TileImageProvider {
    private static class Images {
        public int[][] mapImage;
        public int[][] sectionImage;
        public Bitmap tileImage;
    }

    private HashMap<Character, Images> images;

    private static class ImageResources {
        public int mapRes;
        public int sectionRes;
        public int tileRes;

        public ImageResources(int mapRes, int sectionRes, int tileRes) {
            this.mapRes = mapRes;
            this.sectionRes = sectionRes;
            this.tileRes = tileRes;
        }
    }

    private static final List<ImageResources> imageResourcesList = Arrays.asList(
        new ImageResources(R.drawable.map_a, R.drawable.section_a, R.drawable.tile_a),
        new ImageResources(R.drawable.map_b, R.drawable.section_b, R.drawable.tile_b),
        new ImageResources(R.drawable.map_c, R.drawable.section_c, R.drawable.tile_c),
        new ImageResources(R.drawable.map_d, R.drawable.section_d, R.drawable.tile_d),
        new ImageResources(R.drawable.map_e, R.drawable.section_e, R.drawable.tile_e),
        new ImageResources(R.drawable.map_f, R.drawable.section_f, R.drawable.tile_f),
        new ImageResources(R.drawable.map_g, R.drawable.section_g, R.drawable.tile_g),
        new ImageResources(R.drawable.map_h, R.drawable.section_h, R.drawable.tile_h),
        new ImageResources(R.drawable.map_i, R.drawable.section_i, R.drawable.tile_i),
        new ImageResources(R.drawable.map_j, R.drawable.section_j, R.drawable.tile_j),
        new ImageResources(R.drawable.map_k, R.drawable.section_k, R.drawable.tile_k),
        new ImageResources(R.drawable.map_l, R.drawable.section_l, R.drawable.tile_l),
        new ImageResources(R.drawable.map_m, R.drawable.section_m, R.drawable.tile_m),
        new ImageResources(R.drawable.map_n, R.drawable.section_n, R.drawable.tile_n),
        new ImageResources(R.drawable.map_o, R.drawable.section_o, R.drawable.tile_o),
        new ImageResources(R.drawable.map_p, R.drawable.section_p, R.drawable.tile_p),
        new ImageResources(R.drawable.map_q, R.drawable.section_q, R.drawable.tile_q),
        new ImageResources(R.drawable.map_r, R.drawable.section_r, R.drawable.tile_r),
        new ImageResources(R.drawable.map_s, R.drawable.section_s, R.drawable.tile_s),
        new ImageResources(R.drawable.map_t, R.drawable.section_t, R.drawable.tile_t),
        new ImageResources(R.drawable.map_u, R.drawable.section_u, R.drawable.tile_u),
        new ImageResources(R.drawable.map_v, R.drawable.section_v, R.drawable.tile_v),
        new ImageResources(R.drawable.map_w, R.drawable.section_w, R.drawable.tile_w),
        new ImageResources(R.drawable.map_x, R.drawable.section_x, R.drawable.tile_x)
    );

    public TileImageProvider(Resources resources) {
        this.images = new HashMap<>();

        char id = 'A';
        for (ImageResources imageResources : imageResourcesList) {
            /*
             * External Citation
             * Date: 24 March 2022
             * Problem: Needed to remember how loading bitmaps from resource files worked
             * Resource: Looked at Section A's notes from January 25 on SurfaceViews
             * Solution: Used BitmapFactory.decodeResource() on the resource.
             */

            // Decode each resource into a bitmap, potentially copying it into an integer
            // array instead of leaving it in bitmap form.
            Images images = new Images();

            images.mapImage = bitmapToArray(
                    BitmapFactory.decodeResource(resources, imageResources.mapRes));
            images.sectionImage = bitmapToArray(
                    BitmapFactory.decodeResource(resources, imageResources.sectionRes));
            images.tileImage = BitmapFactory.decodeResource(resources, imageResources.tileRes);

            // Increment to the next letter in the alphabet.
            id++;
        }
    }

    public int[][] getMapImage(char id) {
        return this.images.get(id).mapImage;
    }

    public int[][] getSectionImage(char id) {
        return this.images.get(id).sectionImage;
    }

    public Bitmap getTileImage(char id) {
        return this.images.get(id).tileImage;
    }

    private static int[][] bitmapToArray(Bitmap bitmap) {
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
