package com.example.carcassonne;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Tile {
    public static final int SIZE = 292;

    char id;

    private HashMap<Integer, Section> sections;
    private boolean hasPennant;

    private int meepleSection;
    private int owner;

    private int rotation;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_FARM = 1;
    public static final int TYPE_CITY = 2;
    public static final int TYPE_ROAD = 3;
    public static final int TYPE_CLOISTER = 4;

    private static final int NO_MEEPLE = 0xFFFFFFFF;

    private static final int NO_SECTION_COLOR = 0xFF000000;
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
    public static final HashSet<Integer> FARM_COLORS = new HashSet<>(Arrays.asList(
            0xFF00FF00, 0xFF00BF00, 0xFF007F00, 0xFF003F00));
    public static final HashSet<Integer> CITY_COLORS = new HashSet<>(Arrays.asList(
            0xFFFF0000, 0xFFBF0000));
    public static final HashSet<Integer> ROAD_COLORS = new HashSet<>(Arrays.asList(
            0xFF0000FF, 0xFF0000BF, 0xFF00007F, 0xFF00003F));
    public static final HashSet<Integer> CLOISTER_COLORS = new HashSet<>(Arrays.asList(
            0xFFFF00FF));

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

    public static Point rotatePointCW(int x, int y, int by) {
        Point point = new Point(x, y);

        for (int i = 0; i < by; i += 90) {
            int temp = point.x;
            point.x = SIZE - point.y;
            point.y = temp;
        }

        return point;
    }

    public static Point rotatePointCCW(int x, int y, int by) {
        return rotatePointCW(x, y, 360 - by);
    }

    public char getId() {
        return this.id;
    }

    public Collection<Section> getSections() {
        return this.sections.values();
    }

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

    public Section getRoadSection(int part) {
        for (Section section : this.sections.values()) {
            if (section.getType() == TYPE_ROAD && section.getParts().contains(part)) {
                return section;
            }
        }
        return null;
    }

    public void setMeeple(int x, int y) {
        // We don't need to rotate the positions because the tile is rotated
        BitmapProvider bitmapProvider = CarcassonneMainActivity.getBitmapProvider();
        this.meepleSection = bitmapProvider.getTile(this.id).map.bitmap.getPixel(x, y);
    }

    public void removeMeeple() {
        this.meepleSection = NO_MEEPLE;
    }

    public Section getMeepleSection() {
        return this.sections.get(this.meepleSection);
    }

    public int getMeepleType() {
        Section meepleSection = getMeepleSection();
        if (meepleSection == null) {
            return TYPE_NONE;
        }
        return meepleSection.getType();
    }

    public boolean hasMeeple() {
        return this.meepleSection != NO_MEEPLE;
    }

    /**
     * Queries the index of the player that placed this tile, and therefore the owner
     * of any meeples on the tile as well. If there is no owner (i.e. it was freshly
     * drawn from the deck), it returns -1.
     *
     * @return The index of the player that placed this tile, or -1 if there is no
     *         owner yet.
     */
    public int getOwner() {
        return this.owner;
    }

    /**
     * Sets the player index of the player that placed this tile and therefore the
     * owner of any meeples on the tile as well.
     *
     * @param owner The index of the player that placed this tile.
     */
    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int rotation) {
        while (this.rotation != rotation) {
            rotate();
        }
    }

    public void rotate() {
        this.rotation = (this.rotation + 90) % 360;

        for (Section section : this.sections.values()) {
            section.rotate();
        }
    }

    public void rotateRandomly() {
        // Rotate to some multiple of 90 between 0 and 270
        setRotation((int)(Math.random() * 4) * 90);
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

    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Tile");

        toStr.add("sections", this.sections);
        toStr.add("hasPennant", this.hasPennant);
        toStr.add("meepleSection", this.meepleSection);
        toStr.add("owner", this.owner);

        return toStr.toString();
    }

    public Tile(char id) {
        this.id = id;
        
        this.sections = new HashMap<>();
        this.hasPennant = false;

        this.meepleSection = NO_MEEPLE;
        this.owner = -1;

        this.rotation = 0;

        Bitmap sectionBitmap = CarcassonneMainActivity.getBitmapProvider().getTile(id).section.bitmap;
        parseSectionPositions(sectionBitmap);
        parseSectionConnections(sectionBitmap);
        parseSectionSpecials(sectionBitmap);
    }

    public Tile(Tile other) {
        this.id = other.id;

        this.sections = Util.deepCopyMap(other.sections, HashMap::new, Section::new);
        this.hasPennant = other.hasPennant;

        this.meepleSection = other.meepleSection;
        this.owner = other.owner;

        this.rotation = other.rotation;
    }

    private static int getTypeFromColor(int color) {
        if (FARM_COLORS.contains(color)) {
            return TYPE_FARM;
        } else if (CITY_COLORS.contains(color)) {
            return TYPE_CITY;
        } else if (ROAD_COLORS.contains(color)) {
            return TYPE_ROAD;
        } else if (CLOISTER_COLORS.contains(color)) {
            return TYPE_CLOISTER;
        }

        // If it's any other color, the images are invalid.
        assert false;
        return TYPE_NONE;
    }

    private void parseSectionPositions(Bitmap sectionBitmap) {
        for (int y = 10; y < SIZE - 1; y++) {
            for (int x = 10; x < SIZE - 1; x++) {
                int color = sectionBitmap.getPixel(x, y);
                if (color != NO_MEEPLE) {
                    // There should be no section with this color at this point
                    assert !this.sections.containsKey(color);

                    this.sections.put(color, new Section(getTypeFromColor(color), x, y));
                }
            }
        }
    }

    private static class SectionConn {
        public boolean isRoad;
        public int x;
        public int y;
        public int part;

        public SectionConn(boolean isRoad, int part, int x, int y) {
            this.isRoad = isRoad;
            this.x = x;
            this.y = y;
            this.part = part;
        }
    }

    private static final SectionConn[] PART_POSITIONS = {
            // Farm/city parts
            new SectionConn(false, 0, SIZE / 4,     0),
            new SectionConn(false, 1, SIZE * 3 / 4, 0),
            new SectionConn(false, 2, SIZE - 1,     SIZE / 4),
            new SectionConn(false, 3, SIZE - 1,     SIZE * 3 / 4),
            new SectionConn(false, 4, SIZE / 4,     SIZE - 1),
            new SectionConn(false, 5, SIZE * 3 / 4, SIZE - 1),
            new SectionConn(false, 6, 0,             SIZE / 4),
            new SectionConn(false, 7, 0,             SIZE * 3 / 4),

            // Road parts
            new SectionConn(true, 0, SIZE / 2, 0),
            new SectionConn(true, 1, SIZE - 1, SIZE / 2),
            new SectionConn(true, 2, SIZE / 2, SIZE - 1),
            new SectionConn(true, 3, 0,         SIZE / 2),
    };

    private void parseSectionConnections(Bitmap sectionBitmap) {
        for (SectionConn sectionConn : PART_POSITIONS) {
            int color = sectionBitmap.getPixel(sectionConn.x, sectionConn.y);
            if (color != NO_SECTION_COLOR) {
                // Ensure we have the proper colors for the proper section type.
                if (sectionConn.isRoad) {
                    assert ROAD_COLORS.contains(color);
                } else {
                    assert FARM_COLORS.contains(color) || CITY_COLORS.contains(color);
                }

                Section section = this.sections.get(color);

                // There must be a section at this point; otherwise, the sectionBitmap
                // is incorrect.
                assert section != null;

                section.addPart(sectionConn.part);
            } else {
                // Only roads may not have a section; everything else must have one.
                assert sectionConn.isRoad;
            }
        }
    }

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
