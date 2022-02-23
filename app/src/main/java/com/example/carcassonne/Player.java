package com.example.carcassonne;
/**
 * Represents a player in our game of Carcassonne, each player will consist of
 * the number of meeples placed and the current score that player has. This class
 * will help keep track of what every player has, while allowing access to get and set
 * those values for each player.
 *
 * @author Sophie Arcangel, Cheyanne Yim, Alex Martinez-Lopez, DJ Backus, Vincent Robinison
 */

public class Player {
    private int meepleCount;
    private int score;
    private String name;

    public Player(String n){
        meepleCount = 7;
        score = 0;
        name = n;
    }

    public Player(Player player){
        this.meepleCount = player.meepleCount;
        this.score = player.score;
        this.name = player.name;
    }

    public int getMeepleCount(Player p){ return p.meepleCount;
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

    @Override
    public String toString(){
        return "Name: " + this.name + " Score: " + score + " Meeple Count: " + meepleCount;
    }
}
