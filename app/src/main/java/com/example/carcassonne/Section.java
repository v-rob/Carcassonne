package com.example.carcassonne;

import java.util.HashSet;

public class Section {
    private HashSet<Integer> parts;

    // TODO: Can we just have type?
    private int color;

    private int meepleX;
    private int meepleY;

    public HashSet<Integer> getParts() {
        return this.parts;
    }

    public void addPart(int part) {
        this.parts.add(part);
    }

    public int getType() {
        if (Tile2.FARM_COLORS.contains(this.color)) {
            return Tile2.TYPE_FARM;
        } else if (Tile2.CITY_COLORS.contains(this.color)) {
            return Tile2.TYPE_CITY;
        } else if (Tile2.ROAD_COLORS.contains(this.color)) {
            return Tile2.TYPE_ROAD;
        } else if (Tile2.CLOISTER_COLORS.contains(this.color)) {
            return Tile2.TYPE_CLOISTER;
        }

        // Shouldn't happen; this means that there is an invalid color in one of the tiles.
        assert false;
        return Tile2.TYPE_NONE;
    }

    public int getMeepleX() {
        return this.meepleX;
    }

    public int getMeepleY() {
        return this.meepleY;
    }

    public void rotate() {
        // Figure out how much each part needs to be rotated
        int type = getType();

        int add;
        int mod;
        if (type == Tile2.TYPE_ROAD) {
            // There are four roads, and each side has only one road
            add = 1;
            mod = 4;
        } else if (type == Tile2.TYPE_FARM || type == Tile2.TYPE_CITY) {
            // There are eight farms/cities, and each side has two roads
            add = 2;
            mod = 8;
        } else {
            // Cloister sections don't need rotating
            return;
        }

        // Rotate each part in the set of parts
        HashSet<Integer> rotated = new HashSet<>();
        for (int part : this.parts) {
            // Increase the part to the rightwards size and modulus to ensure it doesn't
            // increase past the highest number, but instead wraps back around to 0.
            rotated.add((part + add) % mod);
        }
        this.parts = rotated;

        // Rotate the meeple positions
        int temp = this.meepleX;
        this.meepleX = Tile2.SIZE - this.meepleY;
        this.meepleY = temp;
    }

    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Section");

        toStr.add("parts", this.parts);
        toStr.add("color", this.color);
        toStr.add("meepleX", this.meepleX);
        toStr.add("meepleY", this.meepleY);

        return toStr.toString();
    }

    public Section(int color, int meepleX, int meepleY) {
        this.parts = new HashSet<>();

        this.color = color;

        this.meepleX = meepleX;
        this.meepleY = meepleY;
    }

    public Section(Section other) {
        this.parts = new HashSet<>(other.parts);

        this.color = other.color;

        this.meepleX = other.meepleX;
        this.meepleY = other.meepleY;
    }
}
