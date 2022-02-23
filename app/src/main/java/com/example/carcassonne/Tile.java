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
    /**
     * The ID of the tile, which is a letter between 'a' and 'x' that corresponds to
     * the "tile_<id>.png" file of this tile.
     */
    private char id;

    /**
     * The parts of the tile that comprise each section of each farm in this tile. See
     * the main comment for Tile for more information.
     */
    private int[][] farmSections;
    /**
     * The parts of the tile that comprise each section of each cities in this tile. See
     * the main comment for Tile for more information.
     */
    private int[][] citySections;

    /**
     * The road parts that indicate which edge of the tile has a road in that direction.
     * See the main comment for Tile for more information.
     */
    private int[] roads;

    /**
     * Indicates whether the city on this tile has a pennant or not. Always false for
     * tiles with zero or two cities.
     */
    private boolean hasPennant;
    /**
     * Indicates whether this tile has a cloister on it. No tiles have both cities and
     * cloisters.
     */
    private boolean isCloister;

    private int meeple;

    /**
     * Returns the part number on the opposite side of the tile. For instance, the
     * opposite for 2 is 7.
     *
     * @param part The part number to find the opposite of.
     * @return The opposite of the provided part number.
     */
    public static int flipPart(int part) {
        // This is an interesting property that subtracting five by the vertical
        // numbers and nine by the horizontal numbers give the opposite part.
        if (part == 0 || part == 1 || part == 4 || part == 5) {
            return 5 - part;
        }
        return 9 - part;
    }

    /**
     * Rotates the tile 90 degrees clockwise. All sections and roads are rotated
     * with it.
     */
    public void rotate() {
        rotateSections(this.farmSections);
        rotateSections(this.citySections);

        for (int i = 0; i < this.roads.length; i++) {
            // Increase the road parts by one and modulus by four to ensure they stay
            // in the range 0-3.
            this.roads[i] = (this.roads[i] + 1) % 4;
        }
    }

    /**
     * Converts the tile to a string representation showing all instance variables.
     *
     * @return The string representation of the tile.
     */
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

    /**
     * Creates a new Tile. Generally, only Deck needs to create Tiles, as everything
     * else will get its tiles from Deck. Deep copies are made of all array parameters.
     *
     * @param id           The character id of the tile.
     * @param farmSections The parts of the tile that comprise each farm section.
     * @param citySections The parts of the tile that comprise each city section.
     * @param roads        The road parts for each road exiting the tile.
     * @param hasPennant   Whether or not the tile has a pennant for the city.
     * @param isCloister   Whether or not the tile has a cloister.
     */
    public Tile(char id, int[][] farmSections, int[][] citySections,
                int[] roads, boolean hasPennant, boolean isCloister) {
        this.id = id;

        this.farmSections = Util.copyNestedArray(farmSections);
        this.citySections = Util.copyNestedArray(citySections);

        this.roads = Util.copyArray(roads);

        // Give the tile a random rotation since tiles will be in no particular
        // rotation when drawing a tile in real Carcassonne.
        int by = (int)(Math.random() * 4);
        for (int i = 0; i < by; i++) {
            rotate();
        }

        this.hasPennant = hasPennant;
        this.isCloister = isCloister;
    }

    /**
     * Performs a deep copy of the tile and all its instance variables.
     *
     * @param other The tile to make a copy of.
     */
    public Tile(Tile other) {
        this.id = other.id;

        this.farmSections = Util.copyNestedArray(other.farmSections);
        this.citySections = Util.copyNestedArray(other.citySections);

        this.roads = Util.copyArray(other.roads);

        this.hasPennant = other.hasPennant;
        this.isCloister = other.isCloister;
    }

    /**
     * Rotates a set of sections by 90 degrees clockwise in place.
     *
     * @param sections The array of sections to rotate.
     */
    private static void rotateSections(int[][] sections) {
        for (int i = 0; i < sections.length; i++) {
            for (int j = 0; j < sections[i].length; j++) {
                // There are two part numbers per side of the tile, so add two,
                // and then modulus by eight to ensure it stays in the range 0-7.
                sections[i][j] = (sections[i][j] + 2) % 8;
            }
        }
    }
};
