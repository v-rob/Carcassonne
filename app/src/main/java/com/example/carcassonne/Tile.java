package com.example.carcassonne;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents a single Carcassonne tile. Carcassonne's main complexity lies in its
 * tiles: they have to match up with each other, they have multiple section per
 * tile (farms, cities, roads, and cloisters), the section can appear multiple
 * unconnected times on each time, etc.
 *
 * The way Tiles represent this are "sections" (represented by the Section class)
 * which comprise one or more "parts". Parts look like the following:
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
 * The eight parts numbered 0-7 inside the tile represent either part of a city or
 * a farm and are called normal "parts". The four parts numbered 0-3 outside the
 * tile represent roads and are called "road parts".
 *
 * Consider the example of tile D, which looks like this:
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
 * There is a road down the middle, the dotted section on the right is a city, and
 * the rest of the space is farmland. Therefore, there are the following sections:
 *
 * - Parts 2, 3:       These parts comprise the only city section.
 * - Parts 1, 4:       This is the section of farmland on the right. It is not in the
 *                     same section as the rest of the farmland because there is a
 *                     road in between them.
 * - Parts 0, 5, 6, 7: This is the other section of farmland on the left.
 * - Road parts 0, 2:  This section contains the only road section.
 * - Road parts 1, 3:  Not part of any section.
 *
 * Any section containing normal parts will be of TYPE_FARM or TYPE_CITY. Sections
 * with road parts will be of TYPE_ROAD. Cloister sections contain no parts and are
 * of TYPE_CLOISTER.
 *
 * Note that all normal parts must be part of a section. Road parts, however, may be
 * left unused if there is no section.
 *
 * Rotations are handled by rotating the sections, not the parts. Consider tile D
 * rotated 90 degrees clockwise:
 *
 *               0
 *       +---------------+
 *       |    0     1    |
 *       |               |
 *       |  7         2  |
 *       |               |
 *     3 |---------------| 1
 *       |               |
 *       |  6 _______ 3  |
 *       |  /. . . . .\  |
 *       |/ . 5 . . 4 . \|
 *       +---------------+
 *               2
 *
 * Now, the city section comprises parts 4 and 5 rather than 2 and 3, and the road
 * contains 1 and 3 rather than 0 and 2, and similarly for farmland. This allows
 * methods to be called on a single part, independent of a tile's rotation. For
 * instance, tile.getSection(2).getType() will always get the type of the right-top
 * of the tile, either TYPE_FARM or TYPE_CITY, without having to account for rotation.
 *
 * Sections are loaded from "section" image files. Each color in the files represents
 * a section type: green represents farmland, red represents cities, blue represents
 * roads, black indicates _no_ road, and fuchsia represents cloisters. To represent
 * different sections, different shades of colors are used: bright green for the first
 * farm section, darker green for the second, and so on. The specific colors can be
 * found in e.g. the FARM_COLORS constant set.
 *
 * There are also image files that tell where a meeple should be placed based on a
 * screen touch, called "map" files. They use the same colors to fill the entire image,
 * every pixel filled. When the player touches a position on a tile, that position is
 * looked up in the corresponding map for that tile. The color retrieved then indicates
 * what section the meeple should be placed in.
 *
 * The section image files also contain single pixels of color that represent where
 * the meeple should appear on the screen when it's in that section. Again, this color
 * is the same color as the section it's in. A third, smaller thing that section image
 * files contain is a "special" pixel in the very corners of the image. If it is black,
 * the tile is normal. If red, it means that the city in the tile has a pennant.
 *
 * Since the colors are already in use in the image files, they are also used in the
 * code. The sections in the tile are stored in a hash map of colors to Sections.
 * The section which a meeple is in is stored as a color. That way, when a meeple is
 * placed, the color can just be looked up in the map and the section is returned.
 * There is also a special pure white constant, NO_MEEPLE, which indicates that no
 * meeple has been placed on this tile.
 *
 * Finally, tiles also contain an "owner", which is the player index of the player
 * who placed the tile (and therefore owns the meeple). Tiles also keep track of
 * their ID (for looking up map images in BitmapProvider) and their rotation in
 * degrees (for rendering).
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class Tile {
    /** The size of every tile image, which is 292 pixels. */
    public static final int SIZE = 292;

    /** The ID of the tile, which is from A-X. */
    char id;

    /** The map of all section colors to Sections that this tile contains. */
    private HashMap<Integer, Section> sections;

    /** Indicates whether this tile has a pennant on it. */
    private boolean hasPennant;

    /**
     * The rotation of the tile, clockwise, in degrees. It will always be a multiple
     * of 90 in the range 0-270.
     */
    private int rotation;

    /** Indicates that a section or section color is a farm. */
    public static final int TYPE_FARM = 1;
    /** Indicates that a section or section color is a city. */
    public static final int TYPE_CITY = 2;
    /** Indicates that a section or section color is a road. */
    public static final int TYPE_ROAD = 3;
    /** Indicates that a section or section color is a cloister. */
    public static final int TYPE_CLOISTER = 4;

    /**
     * Constant returned from getMeepleType() to indicate that there is no meeple on
     * the tile.
     */
    public static final int NO_TYPE = 0;

    /**
     * A color constant (pure white) that indicates that there is no meeple on this tile.
     * It is also used in section images to indicate that the pixel is not a position
     * where meeples should appear when in some section, i.e. the general filler color.
     */
    private static final int NO_MEEPLE = 0xFFFFFFFF;

    /**
     * A color constant (total black) that indicates that there is no section here in a
     * section image. It is only relevant for roads since farms and cities require a
     * section at every part. It is also used to indicate that the tile has no special
     * value.
     */
    private static final int NO_SECTION_COLOR = 0xFF000000;

    /** The special color used to indicate that the tile has a pennant. */
    private static final int PENNANT_COLOR = 0xFFFF0000;

    /*
     * External Citation
     * Date: 17 March 2022
     * Problem: Wanted a way to create a HashSet without calling add() a bunch of
     *          times on it.
     * Resource:
     *     https://docs.oracle.com/javase/7/docs/api/java/util/Arrays.html
     * Solution: Used Arrays.asList() and called the HashSet copy constructor on it.
     */

    /** The set of all colors that indicate distinct farms. */
    public static final HashSet<Integer> FARM_COLORS = new HashSet<>(Arrays.asList(
            0xFF00FF00, 0xFF00BF00, 0xFF007F00, 0xFF003F00));
    /** The set of all colors that indicate distinct cities. */
    public static final HashSet<Integer> CITY_COLORS = new HashSet<>(Arrays.asList(
            0xFFFF0000, 0xFFBF0000));
    /** The set of all colors that indicate distinct roads. */
    public static final HashSet<Integer> ROAD_COLORS = new HashSet<>(Arrays.asList(
            0xFF0000FF, 0xFF0000BF, 0xFF00007F, 0xFF00003F));
    /** The color that indicates a cloister section. */
    public static final int CLOISTER_COLOR = 0xFFFF00FF;

    /**
     * Returns the part number on the opposite side of the tile. For instance, the
     * opposite for 2 is 7.
     *
     * @param part The part number to find the opposite of.
     * @return The opposite of the provided part number.
     */
    public static int flipPart(int part) {
        // Section numbers have an interesting property: subtracting five by the
        // vertical numbers and nine by the horizontal numbers give the part on
        // the opposite side.
        switch (part) {
            case 0:
            case 1:
            case 4:
            case 5:
                return 5 - part;
        }
        return 9 - part;
    }

    /**
     * Returns the road part number on the opposite side of the tile. For instance,
     * the opposite for 0 is 2.
     *
     * @param part The road part number to find the opposite of.
     * @return The opposite of the provided road part number.
     */
    public static int flipRoadPart(int part) {
        // Similarly to flipPart(), some subtraction can give the road part number on
        // the opposite side based on the axis.
        if (part == 1 || part == 3) {
            return 4 - part;
        }
        return 2 - part;
    }

    /**
     * Returns the part number for the part diagonally adjacent to the specified one.
     * For instance, 1 flipped is 2 and vice-versa.
     *
     * @param part The part to find the diagonally adjacent part for.
     * @return The diagonally adjacent part to the provided part.
     */
    public static int getDiagonalPart(int part) {
        /* If a part is odd, the diagonally adjacent is the next even one, and if the
         * part is even, it is the previous odd one.
         *
         * To make this work for the parts 0 and 7, we have to modulus by 8 to make it
         * wrap around. Additionally, since (-1 % 8) == -1 in Java, we add 7 rather than
         * subtract one so 0 goes to 7 rather than to -1; everything else will still wrap
         * around to their proper parts.
         */
        if (part % 2 == 0) {
            return (part + 9) % 8;
        }
        return (part + 1) % 8;
    }

    /**
     * Given a part number, this function returns an offset of either -1 or 1 for the
     * tile next to the specified part number in the horizontal direction. Parts on
     * the vertical side of the tile return 0.
     *
     * @param part The part number to find a horizontal offset for.
     * @return One of -1, 0, or 1 as the horizontal offset.
     */
    public static int partXOffset(int part) {
        switch (part) {
            case 2:
            case 3:
                return 1;
            case 6:
            case 7:
                return -1;
        }
        return 0;
    }

    /**
     * See partXOffset(), but for the vertical direction.
     *
     * @param part The part number to find a vertical offset for.
     * @return One of -1, 0, or 1 as the vertical offset.
     */
    public static int partYOffset(int part) {
        switch (part) {
            case 0:
            case 1:
                return -1;
            case 4:
            case 5:
                return 1;
        }
        return 0;
    }

    /**
     * See partXOffset(), but works on road parts.
     *
     * @param part The road part number to find a horizontal offset for.
     * @return One of -1, 0, or 1 as the horizontal offset.
     */
    public static int roadPartXOffset(int part) {
        if (part == 1) {
            return 1;
        }
        else if (part == 3) {
            return -1;
        }
        return 0;
    }

    /**
     * See roadPartXOffset(), but for the vertical direction.
     *
     * @param part The road part number to find a vertical offset for.
     * @return One of -1, 0, or 1 as the vertical offset.
     */
    public static int roadPartYOffset(int part) {
        if (part == 0) {
            return -1;
        }
        else if (part == 2) {
            return 1;
        }
        return 0;
    }

    /**
     * Rotates an X and Y point some multiple of 90 degrees clockwise. This is used
     * to rotate meeple positions in Section when the tile rotates.
     *
     * @param x  The X position to rotate some multiple of 90 degrees.
     * @param y  The Y position to rotate some multiple of 90 degrees.
     * @param by The amount to rotate by. Must be a multiple of 90 in the range 0-270.
     * @return A Point containing the rotated X and Y.
     */
    public static Point rotatePointCW(int x, int y, int by) {
        Point point = new Point(x, y);

        for (int i = 0; i < by; i += 90) {
            int temp = point.x;
            point.x = SIZE - point.y;
            point.y = temp;
        }

        return point;
    }

    /**
     * Rotates an X and Y point some multiple of 90 degrees counterclockwise. This is
     * used to rotate a position backwards from an already rotated tile to look up a
     * position on a tile map since tile maps are never rotated.
     *
     * @param x  The X position to rotate some multiple of 90 degrees.
     * @param y  The Y position to rotate some multiple of 90 degrees.
     * @param by The amount to rotate by. Must be a multiple of 90 in the range 0-270.
     * @return A Point containing the rotated X and Y.
     */
    public static Point rotatePointCCW(int x, int y, int by) {
        return rotatePointCW(x, y, 360 - by);
    }

    /**
     * Queries the ID of the tile.
     *
     * @return The ID of the tile, which is in the range A-X.
     */
    public char getId() {
        return this.id;
    }

    /**
     * Gets a set of all the sections in this tile of every type.
     *
     * @return All the sections in this tile.
     */
    public Collection<Section> getSections() {
        return this.sections.values();
    }

    /**
     * Gets a set of all the sections with the specified type.
     *
     * @param type The type of the sections to get.
     * @return All the sections in this tile with that type.
     */
    public Collection<Section> getSectionsByType(int type) {
        ArrayList<Section> sections = new ArrayList<>();

        for (Section section : this.sections.values()) {
            if (section.getType() == type) {
                sections.add(section);
            }
        }

        return sections;
    }

    /**
     * Gets a farm or city section from a normal part.
     *
     * @param part The part to find the parent section for.
     * @return The section that contains that part. It will never be null.
     */
    public Section getSection(int part) {
        for (Section section : this.sections.values()) {
            if (section.isFarmOrCity() && section.getParts().contains(part)) {
                return section;
            }
        }

        // There should always be a section for each part
        assert false;
        return null;
    }

    /**
     * Gets a road section from a road part.
     *
     * @param part The road part to find the parent road section for.
     * @return The section that contains that part. It may be null if there is no road
     *         in that direction.
     */
    public Section getRoadSection(int part) {
        for (Section section : this.sections.values()) {
            if (section.getType() == TYPE_ROAD && section.getParts().contains(part)) {
                return section;
            }
        }
        return null;
    }

    /**
     * Queries whether there is a road that spans the specified road part.
     *
     * @param part The road part to check if there is a section for.
     * @return True if there is a road section with that part, false otherwise.
     */
    public boolean hasRoad(int part) {
        return getRoadSection(part) != null;
    }

    /**
     * Sets the meeple on this tile to a new X and Y position. It looks up the section
     * from the tile's map image.
     *
     * @param x The X position to place the meeple at, in pixels.
     * @param y The Y position to place the meeple at, in pixels.
     */
    public void setMeeple(int x, int y) {
        // Remove any existing meeple first.
        removeMeeple();

        // We don't need to rotate the positions because the ImageView rotates X
        // and Y positions automatically.
        BitmapProvider bitmapProvider = CarcassonneMainActivity.getBitmapProvider();

        int color = bitmapProvider.getTile(this.id).map.bitmap.getPixel(x, y);
        this.sections.get(color).addMeeple();
    }

    /** Removes the meeple from this tile if there is one. */
    public void removeMeeple() {
        for (Section section : this.sections.values()) {
            section.removeMeeple();
        }
    }

    /**
     * Queries the section that the meeple is in. If there is no meeple, returns null.
     *
     * @return The section that the meeple is in, or null if no meeple.
     */
    public Section getMeepleSection() {
        for (Section section : this.sections.values()) {
            if (section.hasMeeple()) {
                return section;
            }
        }
        return null;
    }

    /**
     * Queries the type of the meeple on this tile, or TYPE_NONE if there is no meeple.
     *
     * @return The type of the meeple.
     */
    public int getMeepleType() {
        Section meepleSection = getMeepleSection();
        if (meepleSection == null) {
            return NO_TYPE;
        }
        return meepleSection.getType();
    }

    /**
     * Queries whether the tile has a meeple on it.
     *
     * @return True if there is a meeple on this tile, false otherwise.
     */
    public boolean hasMeeple() {
        return getMeepleType() != NO_TYPE;
    }

    /**
     * Queries the index of the player that placed this tile, and therefore the owner
     * of any meeples on the tile as well. If there is no owner (i.e. it was freshly
     * drawn from the deck), returns -1.
     *
     * @return The index of the player who placed this tile, or -1 if there is no
     *         owner yet.
     */
    public int getMeepleOwner() {
        // Since all sections have an owner instance variable, there's no need to
        // store our own copy; just get it from the first section we come across.
        return this.sections.values().iterator().next().getMeepleOwner();
    }

    /**
     * Sets the player index of the player that placed this tile and therefore the
     * owner of any meeples on the tile as well. Effectively, this sets the owner
     * of all the sections in this tile.
     *
     * @param owner The index of the player who placed this tile.
     */
    public void setMeepleOwner(int owner) {
        for (Section section : this.sections.values()) {
            section.setMeepleOwner(owner);
        }
    }

    /**
     * Queries the rotation of this tile in degrees.
     *
     * @return The rotation of this tile. It will always be a multiple of 90 in the
     *         range 0-270.
     */
    public int getRotation() {
        return this.rotation;
    }

    /**
     * Set the rotation of this tile in degrees.
     *
     * @param rotation The new rotation of the tile. It must be a multiple of 90 in
     *                 the range 0-270 or an infinite loop will be triggered.
     */
    public void setRotation(int rotation) {
        while (this.rotation != rotation) {
            rotate();
        }
    }

    /** Rotates the tile 90 degrees clockwise. */
    public void rotate() {
        // By using modulus, this ensures the rotation is always in the range 0-270.
        this.rotation = (this.rotation + 90) % 360;

        for (Section section : this.sections.values()) {
            section.rotate();
        }
    }

    /**
     * Queries whether the city in this tile has a pennant. Tiles with zero or two
     * cities will always return false.
     *
     * @return True if there is a pennant, false otherwise.
     */
    public boolean hasPennant() {
        return this.hasPennant;
    }

    /**
     * Converts the tile to a string representation showing all instance variables.
     *
     * @return The string representation of the tile.
     */
    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Tile");

        toStr.add("sections", this.sections);
        toStr.add("hasPennant", this.hasPennant);

        return toStr.toString();
    }

    /**
     * Creates a new tile from the specified ID. The tile will load its sections from the
     * section image. It will have no owner, no meeple, and a rotation of zero.
     *
     * @param id The ID of the tile to create.
     */
    public Tile(char id) {
        this.id = id;

        // Fill out everything to the default/empty state
        this.sections = new HashMap<>();
        this.hasPennant = false;

        this.rotation = 0;

        BitmapProvider bitmapProvider = CarcassonneMainActivity.getBitmapProvider();
        Bitmap sectionBitmap = bitmapProvider.getTile(id).section.bitmap;

        // Run all the parsers on the section bitmap to load all the necessary information.
        parseSectionPositions(sectionBitmap);
        parseSectionParts(sectionBitmap);
        parseSectionSpecials(sectionBitmap);
    }

    /**
     * Create a new tile that is a deep copy of another tile and all its instance
     * variables.
     *
     * @param other The tile to make a copy of.
     */
    public Tile(Tile other) {
        this.id = other.id;

        this.sections = Util.deepCopyMap(other.sections, HashMap::new, Section::new);
        this.hasPennant = other.hasPennant;

        this.rotation = other.rotation;
    }

    /**
     * Converts a color to a section type by looking it up in the <TYPE>_COLORS sets.
     * It is an error if the color is not a valid section color.
     *
     * @param color The section color to look up the type of.
     * @return The type of that section color.
     */
    private static int getTypeFromColor(int color) {
        if (FARM_COLORS.contains(color)) {
            return TYPE_FARM;
        } else if (CITY_COLORS.contains(color)) {
            return TYPE_CITY;
        } else if (ROAD_COLORS.contains(color)) {
            return TYPE_ROAD;
        } else if (color == CLOISTER_COLOR) {
            return TYPE_CLOISTER;
        }

        // If it's any other color, the images are invalid.
        assert false;
        return NO_TYPE;
    }

    /**
     * Creates the list of sections that this tile has by searching through the provided
     * section bitmap to find the meeple position for each section and creating a new
     * section with that color and meeple position. The sections will have no parts.
     *
     * Sections are created in this method rather than elsewhere because meeple positions
     * are the only places in the section bitmap that indicate whether there is a cloister
     * or not.
     *
     * @param sectionBitmap The section image for this tile.
     */
    private void parseSectionPositions(Bitmap sectionBitmap) {
        // Iterate over every position in the tile except the one pixel borders, which
        // contain section part information, not meeple positions.
        for (int y = 1; y < SIZE - 1; y++) {
            for (int x = 1; x < SIZE - 1; x++) {
                int color = sectionBitmap.getPixel(x, y);
                if (color == NO_MEEPLE) {
                    // Do nothing if the pixel is the filler white color.
                    continue;
                }

                // There should be no duplicate sections with the same color.
                assert !this.sections.containsKey(color);

                this.sections.put(color, new Section(getTypeFromColor(color), x, y));
            }
        }
    }

    /**
     * Represents a single section connection position in the edges of a section
     * bitmap. These are collected into the PART_POSITIONS array for section
     * connection parsing.
     */
    private static class SectionConn {
        /** Whether this connection is a road or farm/city. */
        public boolean isRoad;

        /** The part number of this section. */
        public int part;

        /** The X lookup position on the section image of this section. */
        public int x;
        /** The Y lookup position on the section image of this section. */
        public int y;

        /**
         * Constructor; just fills in the data values of the same name.
         *
         * @param isRoad Whether this connection is a road or farm/city.
         * @param part   The part number of this section.
         * @param x      The X position on the section image.
         * @param y      The Y position on the section image.
         */
        public SectionConn(boolean isRoad, int part, int x, int y) {
            this.isRoad = isRoad;
            this.part = part;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Array of all the positions that need to be looked up on the sections array
     * to be able to fill out the parts that each section has. Cloister sections can
     * be ignored since they have no parts.
     *
     * Normal parts are found by looking at the 1/4 and 3/4 positions on the edges.
     * Road parts are found by looking at the 1/2 positions. The actual section images
     * have larger areas of color to make it easier for the human eye even though the
     * code ignores most of them.
     */
    private static final SectionConn[] PART_POSITIONS = {
            // Farm/city parts
            new SectionConn(false, 0, SIZE / 4,     0),
            new SectionConn(false, 1, SIZE * 3 / 4, 0),
            new SectionConn(false, 2, SIZE - 1,     SIZE / 4),
            new SectionConn(false, 3, SIZE - 1,     SIZE * 3 / 4),
            new SectionConn(false, 4, SIZE * 3 / 4, SIZE - 1),
            new SectionConn(false, 5, SIZE / 4,     SIZE - 1),
            new SectionConn(false, 6, 0,            SIZE * 3 / 4),
            new SectionConn(false, 7, 0,            SIZE / 4),

            // Road parts
            new SectionConn(true, 0, SIZE / 2, 0),
            new SectionConn(true, 1, SIZE - 1, SIZE / 2),
            new SectionConn(true, 2, SIZE / 2, SIZE - 1),
            new SectionConn(true, 3, 0,         SIZE / 2),
    };

    /**
     * Parses all the part (both normal and road) numbers from the section image and
     * places them in the proper tile and image.
     *
     * @param sectionBitmap The section image for this tile.
     */
    private void parseSectionParts(Bitmap sectionBitmap) {
        // Iterate over all the positions that we need to look at.
        for (SectionConn sectionConn : PART_POSITIONS) {
            int color = sectionBitmap.getPixel(sectionConn.x, sectionConn.y);

            if (color == NO_SECTION_COLOR) {
                // Only roads may not have a section; everything else must have one.
                assert sectionConn.isRoad;
                continue;
            }

            // Ensure we have the proper colors for the proper section type.
            if (sectionConn.isRoad) {
                assert ROAD_COLORS.contains(color);
            } else {
                assert FARM_COLORS.contains(color) || CITY_COLORS.contains(color);
            }

            Section section = this.sections.get(color);

            // There must be a section for this color; otherwise, the sectionBitmap
            // is incorrect and is missing a meeple position.
            assert section != null;

            section.addPart(sectionConn.part);
        }
    }

    /**
     * Parses section special information from the section image by looking at the
     * top left corner color. Currently, it only looks for pennants.
     *
     * @param sectionBitmap The section image for this tile.
     */
    private void parseSectionSpecials(Bitmap sectionBitmap) {
        int specialColor = sectionBitmap.getPixel(0, 0);
        if (specialColor == PENNANT_COLOR) {
            this.hasPennant = true;
        } else {
            // It must be black if there is no special.
            assert specialColor == NO_SECTION_COLOR;
        }
    }
}
