package com.example.carcassonne;

import com.example.carcassonne.config.GameConfig;

public class CarassonneMainActivity extends GameMainActivity{
    @Override
    public GameConfig createDefaultConfig() {
        return null;
    }

    @Override
    public LocalGame createLocalGame() {
        return null;
    }
}
