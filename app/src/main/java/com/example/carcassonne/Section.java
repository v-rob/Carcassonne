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
    /** The parent tile that contains this section. */
    private Tile parent;

    /** The type of the section, which is one of the TYPE_* constants in Tile. */
    private int type;

    /**
     * The set of all the parts in this section. For TYPE_FARM or TYPE_CITY, it contains
     * normal parts; for TYPE_ROAD, it contains road parts; for TYPE_CLOISTER, it
     * contains nothing since cloisters have no parts.
     */
    private HashSet<Integer> parts;

    /**
     * The X display position that meeples in this section should have. It will always
     * be inside this section in the map image.
     */
    private int meepleX;
    /**
     * The Y display position that meeples in this section should have. It will always
     * be inside this section in the map image.
     */
    private int meepleY;

    /**
     * Gets the parent tile that contains this section.
     *
     * @return The parent tile that contains this section.
     */
    public Tile getParent() {
        return this.parent;
    }

    /**
     * Gets the type of this section.
     *
     * @return The type of this section, which is one of the TYPE_* constants in Tile.
     */
    public int getType() {
        return this.type;
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
     * Gets the X display position that meeples in this section should have. It will
     * always be inside this section in the map image.
     *
     * @return The X display position for meeples in this section.
     */
    public int getMeepleX() {
        return this.meepleX;
    }

    /**
     * Gets the Y display position that meeples in this section should have. It will
     * always be inside this section in the map image.
     *
     * @return The Y display position for meeples in this section.
     */
    public int getMeepleY() {
        return this.meepleY;
    }

    /**
     * Queries whether this section has a meeple or not. Convenience method, just
     * calls getMeepleSection() on the parent tile and compares it to this.
     *
     * @return True if the section has a meeple, false otherwise.
     */
    public boolean hasMeeple() {
        return this.getParent().getMeepleSection() == this;
    }

    /**
     * Queries the index of the player that placed this tile, and therefore the owner
     * of any meeples on the tile as well. If there is no owner (i.e. it was freshly
     * drawn from the deck), returns -1. Convenience method, just calls getOwner() on
     * the parent tile.
     *
     * @return The index of the player who placed this tile, or -1 if there is no
     *         owner yet.
     */
    public int getOwner() {
        return this.getParent().getOwner();
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

        // We can't add the parent tile directly to avoid infinite recursion, so just
        // add it's address instead.
        toStr.add("parent", this.parent.hashCode());

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
     * @param parent  The parent tile that contains this section.
     * @param type    The type of the section.
     * @param meepleX The X display position of meeples in this section.
     * @param meepleY The Y display position of meeples in this section.
     */
    public Section(Tile parent, int type, int meepleX, int meepleY) {
        this.parent = parent;
        this.type = type;

        this.parts = new HashSet<>();

        this.meepleX = meepleX;
        this.meepleY = meepleY;
    }

    /**
     * Create a new section that is a deep copy of another section and all its
     * instance variables. Since sections contain a **reference** to their
     * parent tile, the parent must be provided manually.
     *
     * @param other  The section to make a copy of.
     * @param parent The new parent of the deep copy of the original section.
     */
    public Section(Section other, Tile parent) {
        this.parent = parent;
        this.type = other.type;

        this.parts = new HashSet<>(other.parts);

        this.meepleX = other.meepleX;
        this.meepleY = other.meepleY;
    }
}
