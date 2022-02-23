package com.example.carcassonne;

public class Util {
    public static int[] copyArray(int[] array) {
        int[] ret = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            ret[i] = array[i];
        }

        return ret;
    }

    public static int[][] copyNestedArray(int[][] array) {
        int[][] ret = new int[array.length][];

        for (int i = 0; i < array.length; i++) {
            ret[i] = copyArray(array[i]);
        }

        return ret;
    }

    public static String arrayToString(int[] array) {
        String str = "{";

        for (int i = 0; i < array.length; i++) {
            str += array[i];
            if (i != array.length - 1) {
                str += ", ";
            }
        }

        str += "}";
        return str;
    }

    public static String nestedArrayToString(int[][] array) {
        String str = "{";

        for (int i = 0; i < array.length; i++) {
            str += "\n    " + arrayToString(array[i]);
            if (i != array.length - 1) {
                str += ",";
            }
        }

        str += "\n}";
        return str;
    }

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
