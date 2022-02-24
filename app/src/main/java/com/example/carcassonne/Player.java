package com.example.carcassonne;
/**
 * Represents a player in our game of Carcassonne, each player will consist of
 * the number of meeples placed and the current score that player has. This class
 * will help keep track of what every player has, while allowing access to get
 * and set those values for each player.
 *
 * @author Sophie Arcangel, Cheyanne Yim, Alex Martinez-Lopez, DJ Backus, Vincent Robinison
 */

public class Player {
    private int meepleCount;
    private int score;

    /** constructor for the player only taking in a string as the player's name and
     * setting the starting number of meeples and starting score for each player
     */
    public Player(){
        meepleCount = 7;
        score = 0;
    }

    /**copy constructor for the player constructor
     *
     * @param player the player object to be copied
     */
    public Player(Player player){
        this.meepleCount = player.meepleCount;
        this.score = player.score;
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

    @Override
    public String toString() {
        return "Player {\n" +
                "    meepleCount = " + this.meepleCount + "\n" +
                "    score = " + this.score + "\n}";
    }
}
