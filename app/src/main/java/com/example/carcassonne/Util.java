package com.example.carcassonne;

/**
 * A class that contains static utility methods (similar to Math) that are applicable
 * anywhere, not only in a single class.
 *
 * @author Vincent Robinson
 */
public class Util {
    /**
     * Indents all lines except the first line in a string by adding four spaces
     * after every newline. This is especially useful for toString() methods that
     * want to indent member instance variables' toString() representations.
     *
     * @param str The string to indent.
     * @return The newly indented string.
     */
    public static String indent(String str) {
        return str.replace("\n", "\n    ");
    }
}
