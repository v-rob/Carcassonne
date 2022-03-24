package com.example.carcassonne;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Tile2 {
    private int[][] map;
    private HashMap<Integer, Section> sections;
    private int meepleSection;
    private int owner;
    private boolean hasPennant;

    private static final int WIDTH = 292;
    private static final int HEIGHT = 292;

    private static final int NO_MEEPLE = 0xFFFFFFFF;

    private static final int NO_CONN_COLOR = 0xFF000000;
    private static final int PENNANT_COLOR = 0xFFFF0000;

    /*
     * External Citation
     * Date: 17 March 2022
     * Problem: Wanted a way to create a HashSet from a list of constants.
     * Resource:
     *     https://docs.oracle.com/javase/7/docs/api/java/util/Arrays.html
     * Solution: Used Arrays.asList() and called the HashSet copy constructor on it.
     */
    public static final HashSet<Integer> FARM_COLORS = new HashSet<>(Arrays.asList(
            0xFF00FF00, 0xFF00BF00, 0xFF007F00, 0xFF003F00));
    public static final HashSet<Integer> CITY_COLORS = new HashSet<>(Arrays.asList(
            0xFFFF0000, 0xFFBF0000));
    public static final HashSet<Integer> ROAD_COLORS = new HashSet<>(Arrays.asList(
            0xFF0000FF, 0xFF0000BF, 0xFF00007F, 0xFF00003F));
    public static final HashSet<Integer> CLOISTER_COLORS = new HashSet<>(Arrays.asList(
            0xFFFF00FF));

    public static final int TYPE_NONE = 0;
    public static final int TYPE_FARM = 1;
    public static final int TYPE_CITY = 2;
    public static final int TYPE_ROAD = 3;
    public static final int TYPE_CLOISTER = 4;

    public HashMap<Integer, Section> getSections() {
        return this.sections;
    }

    private static class PartPos {
        public boolean isRoad;
        public int x;
        public int y;
        public int part;

        public PartPos(boolean isRoad, int part, int x, int y) {
            this.isRoad = isRoad;
            this.x = x;
            this.y = y;
            this.part = part;
        }
    }

    private static final PartPos[] PART_POSITIONS = {
            // Farm/city parts
            new PartPos(false, 0, WIDTH / 4,     0),
            new PartPos(false, 1, WIDTH * 3 / 4, 0),
            new PartPos(false, 2, WIDTH - 1,     HEIGHT / 4),
            new PartPos(false, 3, WIDTH - 1,     HEIGHT * 3 / 4),
            new PartPos(false, 4, WIDTH / 4,     HEIGHT - 1),
            new PartPos(false, 5, WIDTH * 3 / 4, HEIGHT - 1),
            new PartPos(false, 6, 0,             HEIGHT / 4),
            new PartPos(false, 7, 0,             HEIGHT * 3 / 4),

            // Road parts
            new PartPos(true, 0, WIDTH / 2, 0),
            new PartPos(true, 1, WIDTH - 1, HEIGHT / 2),
            new PartPos(true, 2, WIDTH / 2, HEIGHT - 1),
            new PartPos(true, 3, 0,         HEIGHT / 2),
    };

    public Tile2(int[][] map, int[][] connImage) {
        this.map = Util.deepCopyArray(map, Util::copyArray);
        this.sections = new HashMap<>();
        this.meepleSection = NO_MEEPLE;

        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                int color = connImage[y][x];
                if (color != NO_MEEPLE) {
                    this.sections.put(color, new Section(color, x, y));
                }
            }
        }

        for (PartPos partPos : PART_POSITIONS) {
            int color = connImage[partPos.y][partPos.x];
            if (color != NO_CONN_COLOR) {
                // Ensure we have the proper colors for the proper section type.
                if (partPos.isRoad) {
                    assert ROAD_COLORS.contains(color);
                } else {
                    assert FARM_COLORS.contains(color) || CITY_COLORS.contains(color);
                }

                Section section = this.sections.get(color);

                // There must be a section at this point; otherwise, the connImage
                // is incorrect.
                assert section != null;

                section.addPart(partPos.part);
            } else {
                // Only roads may not have a connection; everything else must have one.
                assert partPos.isRoad;
            }
        }

        int specialColor = connImage[0][0];
        if (specialColor == PENNANT_COLOR) {
            this.hasPennant = true;
        } else {
            // It must be black if there is no special.
            assert specialColor == NO_CONN_COLOR;
        }
    }

    public Tile2(Tile2 other) {
        this.map = Util.deepCopyArray(other.map, Util::copyArray);

        this.sections = Util.deepCopyMap(other.sections, HashMap::new, Section::new);

        this.meepleSection = other.meepleSection;
        this.hasPennant = other.hasPennant;
    }

    @Override
    public String toString() {
        // TODO
        /*String str = "{\n";

        for (int i = 0; i < this.sections.size(); i++) {
            str += "    {";

            for (int part : this.sections.get(i)) {
                str += part + " ";
            }

            str = str.substring(0, str.length() - 1) + "}\n";
        }

        return str + "}";*/
        return "";
    }
}
