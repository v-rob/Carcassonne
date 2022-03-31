package com.example.carcassonne;

import android.graphics.Point;

import java.util.HashSet;

/**
 * Represents a single section in a tile. It contains the type of the section, the
 * list of parts or road parts associated with this section, and the X and Y position
 * that a meeple should appear at when placed in this section.
 *
 * When a Section is rotated, the data inside the section (namely the part numbers
 * and meeple position) are rotated with it.
 *
 * Refer to Tile's documentation for a detailed synopsis on how sections work.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class Section {
    /** The type of the section, which is one of the TYPE_* constants in Tile. */
    private int type;

    /**
     * The set of all the parts in this section. For TYPE_FARM or TYPE_CITY, it contains
     * normal parts; for TYPE_ROAD, it contains road parts; for TYPE_CLOISTER, it
     * contains nothing since cloisters have no parts.
     */
    private HashSet<Integer> parts;

    /** The X display position that meeples in this section should have. */
    private int meepleX;
    /** The Y display position that meeples in this section should have. */
    private int meepleY;

    /**
     * Gets the type of this section.
     *
     * @return The type of this section, which is one of the TYPE_* constants in Tile.
     */
    public int getType() {
        return this.type;
    }

    /**
     * Queries whether this section is either a farm or a city. It is useful when
     * dealing with normal parts rather than farms or cities in specific.
     *
     * @return True if this section is a farm or city, false otherwise.
     */
    public boolean isFarmOrCity() {
        return this.type == Tile.TYPE_FARM || this.type == Tile.TYPE_CITY;
    }

    /**
     * Gets the set of all parts in this section. It depends on the type of the section
     * as to the contents; refer to the documentation of the "parts" instance variable
     * for more information.
     *
     * @return The set of all parts in this section.
     */
    public HashSet<Integer> getParts() {
        return this.parts;
    }

    /**
     * Adds a part number to this tile. It should only be called by
     * Tile.parseSectionParts().
     *
     * @param part The part number to add to the set of parts.
     */
    public void addPart(int part) {
        this.parts.add(part);
    }

    /**
     * Gets the X display position that meeples in this section should have.
     *
     * @return The X display position for meeples in this section.
     */
    public int getMeepleX() {
        return this.meepleX;
    }

    /**
     * Gets the Y display position that meeples in this section should have.
     *
     * @return The Y display position for meeples in this section.
     */
    public int getMeepleY() {
        return this.meepleY;
    }

    /** Rotates this section 90 degrees clockwise. */
    public void rotate() {
        int type = getType();

        // Rotate the meeple positions.
        Point rotatedPoint = Tile.rotatePointCW(meepleX, meepleY, 90);
        this.meepleX = rotatedPoint.x;
        this.meepleY = rotatedPoint.y;

        // Figure out how much each part needs to be rotated.
        int add, mod;
        if (type == Tile.TYPE_ROAD) {
            // There are four roads, and each side has only one road.
            add = 1;
            mod = 4;
        } else if (type == Tile.TYPE_FARM || type == Tile.TYPE_CITY) {
            // There are eight farms/cities, and each side has two roads.
            add = 2;
            mod = 8;
        } else {
            // Cloister section parts don't need rotating.
            return;
        }

        // Rotate each part in the set of parts.
        HashSet<Integer> rotatedSet = new HashSet<>();
        for (int part : this.parts) {
            // Increasing the parts rotates them clockwise on the tile. The modulus
            // ensures that increasing it past the highest part number wraps it back
            // around to zero again.
            rotatedSet.add((part + add) % mod);
        }
        this.parts = rotatedSet;
    }

    /**
     * Converts the section to a string representation showing all instance variables.
     *
     * @return The string representation of the section.
     */
    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Section");

        toStr.add("parts", this.parts);
        toStr.add("type", this.type);
        toStr.add("meepleX", this.meepleX);
        toStr.add("meepleY", this.meepleY);

        return toStr.toString();
    }

    /**
     * Creates a new section with the specified type and meeple X and Y display
     * position.
     *
     * @param type    The type of the section.
     * @param meepleX The X display position of meeples in this section.
     * @param meepleY The Y display position of meeples in this section.
     */
    public Section(int type, int meepleX, int meepleY) {
        this.type = type;

        this.parts = new HashSet<>();

        this.meepleX = meepleX;
        this.meepleY = meepleY;
    }

    /**
     * Create a new section that is a deep copy of another section and all its
     * instance variables.
     *
     * @param other The section to make a copy of.
     */
    public Section(Section other) {
        this.type = other.type;

        this.parts = new HashSet<>(other.parts);

        this.meepleX = other.meepleX;
        this.meepleY = other.meepleY;
    }
}
