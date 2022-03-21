package com.example.carcassonne;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * A class that contains static utility methods (similar to Math) that are applicable
 * anywhere, not only in a single class.
 *
 * @author Vincent Robinson
 */
public final class Util {
    // Prevents other code from creating Util objects
    private Util() {}

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

    /*
     * External Citation
     * Date: 17 March 2022
     * Problem: Needed knowledge on how Java generics work.
     * Resource:
     *     https://docs.oracle.com/javase/tutorial/java/generics/methods.html
     *     and other following links on left sidebar.
     * Solution: Read up on this and used the given syntax and feature sets.
     */

    /*
     * External Citation
     * Date: 17 March 2022
     * Problem: Needed to brush up on lambdas and how method and constructor references work.
     * Resource:
     *     https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
     * Solution: Read up on this, especially using the table halfway down.
     */

    // TODO: Document these
    public interface Copier<T> {
        T copy(T other);
    }

    public interface Constructor<T> {
        T construct();
    }

    public static <T> T shallowCopier(T other) {
        return other;
    }

    public static <T> T copy(T value, Copier<T> copier) {
        if (value == null) {
            return null;
        }
        return copier.copy(value);
    }

    public static <T> T[] deepCopy(T[] src, Copier<T> copier) {
        T[] copy = Arrays.copyOf(src, src.length);

        for (int i = 0; i < copy.length; i++) {
            copy[i] = copy(src[i], copier);
        }

        return copy;
    }

    public static <T, C extends Collection<T>> C deepCopy(
            C other, Constructor<C> constructor, Copier<T> copier) {
        C copy = constructor.construct();

        for (T elem : other) {
            copy.add(copy(elem, copier));
        }

        return copy;
    }

    public static <K, V, C extends Map<K, V>> C deepCopy(
            C other, Constructor<C> constructor, Copier<K> keyCopier, Copier<V> valCopier) {
        C copy = constructor.construct();

        for (C.Entry<K, V> entry : other.entrySet()) {
            copy.put(copy(entry.getKey(), keyCopier),
                    copy(entry.getValue(), valCopier));
        }

        return copy;
    }

    public static <K, V, C extends Map<K, V>> C deepCopy(
            C other, Constructor<C> constructor, Copier<V> valCopier) {
        return deepCopy(other, constructor, Util::shallowCopier, valCopier);
    }

    public static <T> T[] arrayCopy(T[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static boolean[] arrayCopy(boolean[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static char[] arrayCopy(char[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static byte[] arrayCopy(byte[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static short[] arrayCopy(short[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static int[] arrayCopy(int[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static long[] arrayCopy(long[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static float[] arrayCopy(float[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static double[] arrayCopy(double[] src) {
        return Arrays.copyOf(src, src.length);
    }
}
