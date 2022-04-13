package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Action class that signifies that a player wishes to place a meeple on the current
 * tile. It contains the section to place it at.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonnePlaceMeepleAction extends GameAction {
    /** The section on the tile to place the meeple at. */
    private Section section;

    /**
     * Constructs a new action for placing a meeple for the specified player.
     *
     * @param player  The player performing the action.
     * @param section The section to place the meeple on.
     */
    public CarcassonnePlaceMeepleAction(GamePlayer player, Section section) {
        super(player);
        this.section = section;
    }

    /**
     * Gets the section on the tile to place the meeple at.
     *
     * @return The section.
     */
    public Section getSection() {
        return this.section;
    }
}
