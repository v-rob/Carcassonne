package com.example.carcassonne;

import java.util.HashSet;

public class Section {
    private int type;

    private HashSet<Integer> parts;

    private int meepleX;
    private int meepleY;

    public int getType() {
        return this.type;
    }

    public boolean isFarmOrCity() {
        return this.type == Tile.TYPE_FARM || this.type == Tile.TYPE_CITY;
    }

    public HashSet<Integer> getParts() {
        return this.parts;
    }

    public void addPart(int part) {
        this.parts.add(part);
    }

    public int getMeepleX() {
        return this.meepleX;
    }

    public int getMeepleY() {
        return this.meepleY;
    }

    public void rotate() {
        // Figure out how much each part needs to be rotated.
        int type = getType();

        int add;
        int mod;
        if (type == Tile.TYPE_ROAD) {
            // There are four roads, and each side has only one road.
            add = 1;
            mod = 4;
        } else if (type == Tile.TYPE_FARM || type == Tile.TYPE_CITY) {
            // There are eight farms/cities, and each side has two roads.
            add = 2;
            mod = 8;
        } else {
            // Cloister sections don't need rotating.
            return;
        }

        // Rotate each part in the set of parts.
        HashSet<Integer> rotated = new HashSet<>();
        for (int part : this.parts) {
            // Increasing the parts rotates them clockwise on the tile. The modulus
            // ensures that increasing it past the highest part number wraps it back
            // around to zero again.
            rotated.add((part + add) % mod);
        }
        this.parts = rotated;

        // Rotate the meeple positions.
        int temp = this.meepleX;
        this.meepleX = Tile.SIZE - this.meepleY;
        this.meepleY = temp;
    }

    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Section");

        toStr.add("parts", this.parts);
        toStr.add("type", this.type);
        toStr.add("meepleX", this.meepleX);
        toStr.add("meepleY", this.meepleY);

        return toStr.toString();
    }

    public Section(int type, int meepleX, int meepleY) {
        this.type = type;

        this.parts = new HashSet<>();

        this.meepleX = meepleX;
        this.meepleY = meepleY;
    }

    public Section(Section other) {
        this.type = other.type;

        this.parts = new HashSet<>(other.parts);

        this.meepleX = other.meepleX;
        this.meepleY = other.meepleY;
    }
}