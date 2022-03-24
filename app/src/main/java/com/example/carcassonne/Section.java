package com.example.carcassonne;

import java.util.HashSet;

public class Section {
    private HashSet<Integer> parts;

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

    private void rotate() {
        int type = getType();

        int add;
        int mod;
        if (type == Tile2.TYPE_ROAD) {
            add = 1;
            mod = 4;
        } else if (type == Tile2.TYPE_FARM || type == Tile2.TYPE_CITY) {
            add = 2;
            mod = 8;
        } else {
            return;
        }

        // We have to create a new set and return it because we can't change set
        // elements in place: they aren't positional.
//        HashSet<Integer> rotated = new HashSet<>();
//        for (int part : set) {
//            rotated.add((part + add) % mod);
//        }
//        return rotated;
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
