package com.example.carcassonne;

/**
 * Represents a single Carcassonne tile. Carcassonne's main complexity lies in
 * how things connect: for instance, farmland connects to farmland on different
 * tiles, but sometimes doesn't connect to farmland on the same tile due to a city
 * or road in between.
 *
 * The way Tile objects represent this is via "sections". All farmland that is
 * connected on one side of a road or city is put in its own section, and
 * likewise for cities.
 *
 * Sections are grouped together by the smallest unit of measure, a "part" of
 * the tile. Observe the following diagram and the eighths numbered 0-7: these
 * are the parts of every tile.
 *
 *               0
 *       +---------------+
 *       |\   0  |  1   /|
 *       |  \    |    /  |
 *       |  7 \  |  / 2  |
 *       |      \|/      |
 *     3 |-------+-------| 1
 *       |      /|\      |
 *       |  6 /  |  \ 3  |
 *       |  /    |    \  |
 *       |/   5  |  4   \|
 *       +---------------+
 *               2
 *
 * Any tile can be split into these eight parts, and these parts can then be
 * placed into sections. Finally, note the outer numbers 0-3 in the diagram:
 * these indicate whether there is a road in that direction.
 *
 * An example is likely the best way to explain this. Consider tile D:
 *
 *               0
 *       +---------------+
 *       |    0  |  1   /|
 *       |       |    /. |
 *       |  7    |  / 2 .|
 *       |       |  |. . |
 *     3 |       |  | . .| 1
 *       |       |  |. . |
 *       |  6    |  \ 3 .|
 *       |       |    \. |
 *       |    5  |  4   \|
 *       +---------------+
 *               2
 *
 * There is a city on the right and a vertical road through the middle. Note
 * that the part and road numbers have been left in the diagram. There are three
 * sections:
 *
 * - 2, 3:       These parts comprise the only city section.
 * - 1, 4:       This is one of the sections of farmland. It does not connect
 *               to the rest of the farmland because there is a road in between.
 * - 5, 6, 7, 8: This is the other section of farmland.
 *
 * Secondly, this tile will include the road numbers 0 and 2 because roads exit
 * in those two directions.
 *
 * Sections will vary from tile to tile: some may have as many as four farmland
 * sections, and others may have zero city or farmland sections. However, every
 * part must always be part of some section.
 *
 * Sections are stored as nested arrays of integers: the outermost array is the
 * array of sections, while the inner array is the array of part numbers. For
 * the previous example, this is the array structure for the farmland:
 *
 *     int[][] farmSections = {
 *         {1, 4},
 *         {5, 6, 7, 8}
 *     };
 *
 * And the city section:
 *
 *     int[][] citySections = {
 *         {2, 3}
 *     };
 *
 * Roads are simply stored as an array of road parts. So, for tile D, this is:
 *
 *     int[] roads = {0, 2};
 *
 * A final note: rotating a tile causes all the part and road numbers to be
 * rotated with it. In other words, a right rotation would cause tile D's city
 * to become 4 and 5 instead of the normal 2 and 3.
 *
 * Tiles have two additional boolean fields: hasPennant states whether the city
 * in the tile has a pennant. Tiles with two cities never have pennants, so this
 * this does not have to be accounted for. Secondly, isCloister states whether
 * the tile is a cloister or not.
 *
 * @author Vincent Robinson
 */
public class Tile {
    private char id;

    private int[][] farmSections;
    private int[][] citySections;

    private int[] roads;

    private boolean hasPennant;
    private boolean isCloister;

    private int meeple;

    public static int flipPart(int part) {
        if (part == 0 || part == 1 || part == 4 || part == 5) {
            return 5 - part;
        }
        return 9 - part;
    }

    public void rotate() {
        rotateBy(1);
    }

    public Tile(char id, int[][] farmSections, int[][] citySections,
                int[] roads, boolean hasPennant, boolean isCloister) {
        this.id = id;

        this.farmSections = Util.copyNestedArray(farmSections);
        this.citySections = Util.copyNestedArray(citySections);

        this.roads = Util.copyArray(roads);

        rotateBy((int)(Math.random() * 4));

        this.hasPennant = hasPennant;
        this.isCloister = isCloister;
    }

    @Override
    public String toString() {
        return "Tile {\n" +
                "    id = " + this.id + "\n" +
                "    farmSections = " + Util.nestedArrayToString(this.farmSections) + "\n" +
                "    citySections = " + Util.nestedArrayToString(this.citySections) + "\n" +
                "    roads = " + Util.arrayToString(this.roads) + "\n" +
                "    hasPennant = " + this.hasPennant + "\n" +
                "    isCloister = " + this.isCloister + "\n}";
    }
    
    public Tile(Tile other) {
        this.id = other.id;

        this.farmSections = Util.copyNestedArray(other.farmSections);
        this.citySections = Util.copyNestedArray(other.citySections);

        this.roads = Util.copyArray(other.roads);

        this.hasPennant = other.hasPennant;
        this.isCloister = other.isCloister;
    }

    private static void rotateSectionsBy(int[][] sections, int amount) {
        for (int i = 0; i < sections.length; i++) {
            int[] section = sections[i];
            for (int j = 0; j < section.length; j++) {
                section[j] = (section[j] + amount * 2) % 8;
            }
        }
    }

    private void rotateBy(int amount) {
        rotateSectionsBy(this.farmSections, amount);
        rotateSectionsBy(this.citySections, amount);

        for (int i = 0; i < this.roads.length; i++) {
            this.roads[i] = (this.roads[i] + amount) % 4;
        }
    }
};
