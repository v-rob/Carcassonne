package com.example.carcassonne;

public class Player {
    private int meepleCount;
    private int score;

    public Player(){
        meepleCount = 7;
        score = 0;
    }

    public int getMeepleCount(Player p){
        return p.meepleCount;
    }

    public int getScore(Player p){
        return p.score;
    }

    public void setMeepleCount(Player p, int i){
        p.meepleCount = i;
    }

    public void setScore(Player p, int i){
        p.score = i;
    }
}
