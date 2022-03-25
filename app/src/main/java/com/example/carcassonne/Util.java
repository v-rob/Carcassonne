package com.example.carcassonne;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * A class that contains static utility methods (similar to Math) that are applicable
 * anywhere, not only in a single class. Notably, it contains a suite of methods for
 * performing deep copies of arrays, Collections, and Maps.
 *
 * Util Deep Copying Guide
 *
 * - Type: Array
 *     - Dimensions: 1
 *         - Contains: Primitives or immutable objects
 *             String[] copy = Util.copyArray(orig);
 *         - Contains: Mutable objects
 *             Tile[] copy = Util.deepCopyArray(orig, Tile::new);
 *         - Contains: Collections/Maps of immutable objects
 *             ArrayList<Integer>[] copy = Util.deepCopyArray(orig, ArrayList::new);
 *             HashMap<Integer, String>[] copy = Util.deepCopyArray(orig, HashMap::new);
 *     - Dimensions: 2
 *         - Contains: Primitives or immutable objects
 *             int[][] copy = Util.deepCopyArray(orig, Util::copyArray);
 *         - Contains: Mutable objects
 *             Tile[][] copy = Util.deepCopyNested(orig, Tile::new);
 *         - Contains: Collections/Maps of immutable objects
 *             HashSet<String>[][] copy = Util.deepCopyNested(orig, HashSet::new);
 *             TreeMap<String, Double>[] copy = Util.deepCopyNested(orig, HashMap::new);
 *     - Dimensions: 3
 *         - Contains: Primitives or immutable objects
 *             float[][][] copy = Util.deepCopyNested(orig, Util::copyArray);
 * - Type: Collection
 *     - Contains: Immutable objects
 *         ArrayList<Integer> copy = new ArrayList(orig);
 *     - Contains: Mutable objects
 *         TreeSet<Tile> copy = Util.deepCopyCol(orig, TreeSet::new, Tile::new);
 *     - Contains: Collections/Maps of immutable objects
 *         ArrayList<HashSet<String>> copy = Util.deepCopyCol(
 *                 orig, ArrayList::new, HashSet::new);
 *         HashSet<HashMap<Character, String>> copy = Util.deepCopyCol(
 *                 orig, HashSet::new, HashMap::new);
 * - Type: Map
 *     - Contains: Immutable objects
 *         HashMap<String, Long> copy = new HashMap(orig);
 *     - Contains: Mutable values
 *         TreeMap<Integer, Tile> copy = Util.deepCopyMap(orig, TreeMap::new, Tile::new);
 *     - Contains: Mutable keys
 *         HashMap<Tile, String> copy = Util.deepCopyMap(
 *                 orig, HashMap::new, Tile::new, Util::byRef);
 *     - Contains: Both mutable keys and values
 *         HashMap<Tile, Tile> copy = Util.deepCopyMap(
 *                 orig, HashMap::new, Tile::new, Tile::new);
 *     - Contains: Containers/Maps of immutable objects
 *         TreeMap<Integer, ArrayList<Float>> copy = Util.deepCopyMap(
 *                 orig, TreeMap::new, ArrayList::new);
 *         Hashtable<Integer, TreeMap<Integer, String>> copy = Util.deepCopyMap(
 *                 orig, Hashtable::new, TreeMap::new);
 *
 * If you have anything else, it's too complicated for Util's deep copying methods to
 * handle in one line. Instead, copy the outer container manually. For instance:
 *
 *     // Shallow copy of outer array
 *     Tile[][][] copy = Util.copyArray(orig);
 *
 *     // Now deep copy each element in the shallow copy
 *     for (int i = 0; i < copy.length; i++) {
 *         // Make a deep copy of the Tile[][] at this index
 *         copy[i] = Util.deepCopyNested(copy[i], Tile::new);
 *     }
 *
 * If the data structures are this complicated, also consider refactoring the code instead.
 *
 * @author Vincent Robinson
 */
public final class Util {
    // Prevents other code from creating Util objects
    private Util() {}

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

    /**
     * Interface for holding a method reference to a copy constructor for the
     * deepCopy() methods.
     */
    public interface Copier<T> {
        T copy(T other);
    }

    /**
     * Interface for holding a method reference to a default constructor for the
     * Collection and Map deepCopy() methods.
     */
    public interface Constructor<T> {
        T construct();
    }

    /**
     * When a Util.deepCopy() method requires a copy constructor but a shallow copy of
     * the elements is desired or the elements are immutable, passing in Util::byRef
     * will copy the elements by reference. Generally, this is most useful for immutable
     * mapped values in Util.deepCopyMap().
     *
     * @param other The element being passed by reference.
     * @return The element returned verbatim without changes.
     */
    public static <T> T byRef(T other) {
        return other;
    }

    /**
     * Most copy constructors fail on null values, but it is often desirable to call a
     * copy constructor on a possibly null value, returning null if it is null and
     * calling the copy constructor otherwise.
     *
     * @param value  The value to either copy or return as null verbatim.
     * @param copier The copy constructor to call on non-null values.
     * @return Null if the parameter is null, or the copy of it otherwise.
     */
    public static <T> T copyOrNull(T value, Copier<T> copier) {
        return (value == null) ? null : copier.copy(value);
    }

    /**
     * Performs a deep copy of a one-dimensional non-primitive array type or a
     * two-dimensional primitive array type.
     *
     * See the "Util Deep Copying Guide" in the description for Util for more information.
     *
     * @param src    The source array to make a deep copy of.
     * @param copier The copy constructor of the type contained inside the array.
     * @return The deep copy of the array. Null elements will be preserved, but the
     *         copy constructor will not be called on them.
     */
    public static <T> T[] deepCopyArray(T[] src, Copier<T> copier) {
        T[] copy = copyArray(src);

        for (int i = 0; i < copy.length; i++) {
            copy[i] = copyOrNull(src[i], copier);
        }

        return copy;
    }

    /**
     * Performs a deep copy of a two-dimensional non-primitive array type or a
     * three-dimensional primitive array type.
     *
     * See the "Util Deep Copying Guide" in the description for Util for more information.
     *
     * @param src    The source array to make a deep copy of.
     * @param copier The copy constructor of the type contained inside the array.
     * @return The deep copy of the array. Null elements will be preserved, but the
     *         copy constructor will not be called on them.
     */
    public static <T> T[][] deepCopyNested(T[][] src, Copier<T> copier) {
        T[][] copy = copyArray(src);
        
        for (int i = 0; i < copy.length; i++) {
            if (copy[i] != null) {
                copy[i] = deepCopyArray(copy[i], copier);
            }
        }

        return copy;
    }

    /**
     * Performs a deep copy of a subclass of Collection, e.g. HashSet, ArrayList, etc.
     *
     * See the "Util Deep Copying Guide" in the description for Util for more information.
     *
     * @param other       The container to make a deep copy of.
     * @param constructor A method reference to the default constructor of the container.
     * @param copier      A method reference to the copy constructor of the elements of
     *                    the container.
     * @return The deep copy of the container. Null elements will be preserved, but the
     *         copy constructor will not be called on them.
     */
    public static <T, C extends Collection<T>> C deepCopyCol(
            C other, Constructor<C> constructor, Copier<T> copier) {
        C copy = constructor.construct();

        for (T elem : other) {
            copy.add(copyOrNull(elem, copier));
        }

        return copy;
    }

    /**
     * Performs a deep copy of a subclass of Map, e.g. HashMap. Unlike the other
     * Util.deepCopyMap() overload, this overload has copy constructor references for both
     * the key and the value, so it should only be used when the key is mutable. If the
     * value is immutable, pass in Util::byRef.
     *
     * See the "Util Deep Copying Guide" in the description for Util for more information.
     *
     * @param other       The map to make a deep copy of.
     * @param constructor A method reference to the default constructor of the map.
     * @param keyCopier   A method reference to the copy constructor of the keys.
     * @param valCopier   A method reference to the copy constructor of the mapped values.
     * @return The deep copy of the container. Null elements will be preserved, but the
     *         copy constructor will not be called on them.
     */
    public static <K, V, C extends Map<K, V>> C deepCopyMap(
            C other, Constructor<C> constructor, Copier<K> keyCopier, Copier<V> valCopier) {
        C copy = constructor.construct();

        for (C.Entry<K, V> entry : other.entrySet()) {
            copy.put(copyOrNull(entry.getKey(), keyCopier),
                    copyOrNull(entry.getValue(), valCopier));
        }

        return copy;
    }

    /**
     * Performs a deep copy of a subclass of Map, e.g. HashMap. Unlike the other
     * Util.deepCopyMap() overload, this one assumes that the key is immutable. If this is
     * not true, use the other overload.
     *
     * See the "Util Deep Copying Guide" in the description for Util for more information.
     *
     * @param other       The map to make a deep copy of.
     * @param constructor A method reference to the default constructor of the map.
     * @param valCopier   A method reference to the copy constructor of the mapped values.
     * @return The deep copy of the container. Null elements will be preserved, but the
     *         copy constructor will not be called on them. Keys are not copied.
     */
    public static <K, V, C extends Map<K, V>> C deepCopyMap(
            C other, Constructor<C> constructor, Copier<V> valCopier) {
        return deepCopyMap(other, constructor, Util::byRef, valCopier);
    }

    /**
     * Performs a **shallow** copy of the provided array. This is meant for copies of arrays
     * containing immutable objects, like String or Integer, or when shallow copies are
     * actually desired. Use Util.deepCopyArray() if a deep copy of an array is desired.
     *
     * @param src The array being shallow copied.
     * @return A copy of the array with the same references to the elements of the original
     *         array.
     */
    public static <T> T[] copyArray(T[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive booleans.
     *
     * @param src The array of booleans to make a copy of.
     * @return The copied array of booleans.
     */
    public static boolean[] copyArray(boolean[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive chars.
     *
     * @param src The array of chars to make a copy of.
     * @return The copied array of chars.
     */
    public static char[] copyArray(char[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive chars.
     *
     * @param src The array of chars to make a copy of.
     * @return The copied array of chars.
     */
    public static byte[] copyArray(byte[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive shorts.
     *
     * @param src The array of shorts to make a copy of.
     * @return The copied array of shorts.
     */
    public static short[] copyArray(short[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive ints.
     *
     * @param src The array of ints to make a copy of.
     * @return The copied array of ints.
     */
    public static int[] copyArray(int[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive longs.
     *
     * @param src The array of longs to make a copy of.
     * @return The copied array of longs.
     */
    public static long[] copyArray(long[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive floats.
     *
     * @param src The array of floats to make a copy of.
     * @return The copied array of floats.
     */
    public static float[] copyArray(float[] src) {
        return Arrays.copyOf(src, src.length);
    }

    /**
     * Performs a copy of an array of primitive doubles.
     *
     * @param src The array of doubles to make a copy of.
     * @return The copied array of doubles.
     */
    public static double[] copyArray(double[] src) {
        return Arrays.copyOf(src, src.length);
    }
}
