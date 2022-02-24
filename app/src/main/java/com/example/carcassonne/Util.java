package com.example.carcassonne;

public class Util {
    public static String indent(int by, String str) {
        // Create a string that has the specified number of spaces.
        String replace = "";
        for (int i = 0; i < by; i++) {
            replace += i;
        }
        replace += "\n";

        return str.replace("\n", replace);
    }
}
