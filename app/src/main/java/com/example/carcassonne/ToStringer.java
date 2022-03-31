package com.example.carcassonne;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Helper class for creating toString() methods. Instantiate ToStringer with the name
 * of the class being converted to a string, and then add each field in the class
 * directly with the add() method. Calling toString() will cause it to be formatted and
 * returned as a string.
 *
 * For example, for an example class Player with a String name, int score, and
 * ArrayList<String> hand (array of cards), this is the code:
 *
 *     public String toString() {
 *         ToStringer toStr = new ToStringer("Player"):
 *
 *         toStr.add("name", this.name);
 *         toStr.add("score", this.score);
 *         toStr.add("hand", this.hand);
 *
 *         return toStr.toString();
 *     }
 *
 * And this is an example output:
 *
 *     Player {
 *         name = "Bob";
 *         score = 10;
 *         hand = ArrayList {
 *             "King of Hearts",
 *             "Jack of Clubs",
 *             "Three of Spades"
 *         };
 *     }
 *
 * See stringify() for more details on how add() converts its argument to a string.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class ToStringer {
    /** The internal string currently being built by the add() calls. */
    private StringBuilder string;

    /**
     * Create a new instance of the ToStringer class for the specified class.
     *
     * @param name The name of the class to make a toString() for.
     */
    public ToStringer(String name) {
        this.string = new StringBuilder(name);
        this.string.append(" {\n");
    }

    /**
     * Add a field to the toString() of this class.
     *
     * @param fieldName The name of the field to add.
     * @param val       The field to add. Primitives are also taken here.
     */
    public void add(String fieldName, Object val) {
        this.string.append("    ");
        this.string.append(fieldName);
        this.string.append(" = ");
        this.string.append(indent(stringify(val)));
        this.string.append(";\n");
    }

    /**
     * Converts this ToStringer to its final string form to be returned by the using
     * toString() method.
     *
     * @return The class and field names provided earlier converted to a string.
     */
    @Override
    public String toString() {
        this.string.append("}");
        return this.string.toString();
    }

    /**
     * The heavy lifter for the add() method which takes an object and converts it to a
     * string form. It formats things as follows:
     *
     * - null: Formatted as "null"
     * - String: Adds quotes around the string and escapes internal quotes with backslashes.
     * - char: Adds single quotes around the character and escapes internal single quotes
     *   with backslashes.
     * - Arrays: Formatted as "Type[] {elem1, elem2, ..., elemN}" for primitives, or:
     *       Type[] {
     *           elem1,
     *           elem2,
     *           ...,
     *           elemN
     *       }
     *   for objects, calling stringify() on the contained objects.
     * - Collections: Formatted as:
     *       Type {
     *           elem1,
     *           elem2,
     *           ...,
     *           elemN
     *       }
     *   All elements have stringify() called on them. Generic parameters are not included:
     *   for instance, in Type<T>, the <T> is not included.
     * - Maps: Formatted as:
     *       Type {
     *           [key1] = val1,
     *           [key2] = val2,
     *           ...,
     *           [keyN] = valN
     *       }
     *   All keys and values have stringify() called on them. Generic parameters are not
     *   included: for instance, in Type<T>, the <T> is not included.
     * - Map.Entry: Formatted as "[key] = val".
     * - Everything else: toString() is simply called on the object.
     *
     * This method is public to allow objects to be converted to a string outside of the
     * context of a class being toString()ed.
     *
     * @param object The object to convert to a string.
     * @return The object converted to a string.
     */
    public static String stringify(Object object) {
        /* There is an unfortunate amount of code duplication and instanceofs in this method,
         * especially between Object[], Collection, and Map, because of Java's distinctions
         * between primitives, objects, and between arrays, Collections, and Maps. Generics
         * can't help us here due to type erasure, so this is the best possible solution.
         */

        if (object == null) {
            // null is just null
            return "null";
        } else if (object instanceof String) {
            // Quote the string and escape internal quotes
            return "\"" + ((String)object).replace("\"", "\\\"") + "\"";
        } else if (object instanceof Character) {
            // Quote the character and escape internal quotes
            Character c = (Character)object;
            return (c == '\'') ? "'\\''" : "'" + c + "'";
        } else if (object instanceof boolean[]) {
            // All primitive arrays use Arrays.toString() so we don't have to duplicate
            // loops for each of these, and then use formatArray() to convert it to our
            // preferred format for toString()ing.
            return formatArray("boolean", Arrays.toString((boolean[])object));
        } else if (object instanceof char[]) {
            return formatArray("char", Arrays.toString((char[])object));
        } else if (object instanceof byte[]) {
            return formatArray("byte", Arrays.toString((byte[])object));
        } else if (object instanceof short[]) {
            return formatArray("short", Arrays.toString((short[])object));
        } else if (object instanceof int[]) {
            return formatArray("int", Arrays.toString((int[])object));
        } else if (object instanceof long[]) {
            return formatArray("long", Arrays.toString((long[])object));
        } else if (object instanceof float[]) {
            return formatArray("float", Arrays.toString((float[])object));
        } else if (object instanceof double[]) {
            return formatArray("double", Arrays.toString((double[])object));
        } else if (object.getClass().isArray()) {
            // We can't use Arrays.toString() here because we need to call stringify()
            // on the contained elements, and elements should be separated by newlines.

            /*
             * External Citation
             * Date: 24 March 2022
             * Problem: Required a way to know if some Object is an array or not.
             * Resource:
             *     https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#isArray--
             * Solution: Used isArray() on the Class of the object.
             */

            // Casting to an Object[] is safe for any non-primitive array.
            Object[] arr = (Object[])object;

            /*
             * External Citation
             * Date: 23 March 2022
             * Problem: Required a way to get the name of an unknown Object.
             * Resource:
             *     https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getSimpleName--
             * Solution: Used this method on the Class of the object.
             */
            StringBuilder str = new StringBuilder(arr.getClass().getSimpleName());
            str.append(" {\n");

            // Indicates whether the array has any elements or not.
            boolean hasElement = false;

            for (Object elem : arr) {
                hasElement = true;

                // Add the string representation of the object, indented since it's in an array,
                // plus the comma and newline after it.
                str.append("    ");
                str.append(indent(stringify(elem)));
                str.append(",\n");
            }

            /*
             * External Citation
             * Date: 23 March 2022
             * Problem: Wanted a clean way to remove characters from the end of
             *          a StringBuilder without having to resort to copying it
             *          into a substring.
             * Resource:
             *     https://docs.oracle.com/javase/7/docs/api/java/lang/StringBuilder.html#setLength(int)
             * Solution: Used this to simply set the length to trim the final
             *           characters.
             */

            // If there are any elements in the array, remove the trailing comma and newline.
            if (hasElement) {
                str.setLength(str.length() - 2);
            }

            str.append("\n}");
            return str.toString();
        } else if (object instanceof Collection) {
            // Besides the type, this code is identical to that of Object[].
            Collection col = (Collection)object;
            StringBuilder str = new StringBuilder(col.getClass().getSimpleName());
            str.append(" {\n");

            boolean hasElement = false;

            for (Object elem : col) {
                hasElement = true;

                str.append("    ");
                str.append(indent(stringify(elem)));
                str.append(",\n");
            }

            if (hasElement) {
                str.setLength(str.length() - 2);
            }

            str.append("\n}");
            return str.toString();
        } else if (object instanceof Map) {
            // Besides the type and iteration, this code is identical to that of Collection.
            Map map = (Map)object;
            StringBuilder str = new StringBuilder(map.getClass().getSimpleName());
            str.append(" {\n");

            boolean hasElement = false;

            for (Object entry : map.entrySet()) {
                hasElement = true;

                // Making a string representation of a Map.Entry is handled in stringify()
                // as another case.
                str.append("    ");
                str.append(indent(stringify(entry)));
                str.append(",\n");
            }

            if (hasElement) {
                str.setLength(str.length() - 2);
            }

            str.append("\n}");
            return str.toString();
        } else if (object instanceof Map.Entry) {
            // We make this a separate case in case a raw Map.Entry is passed to stringify()
            // from the public interface.
            Map.Entry entry = (Map.Entry)object;
            return "[" + stringify(entry.getKey()) + "] = " + stringify(entry.getValue());
        }

        // Otherwise, just return the object converted to a string.
        return object.toString();
    }

    /**
     * Formats an array received from Arrays.toString() according to ToStringer's standards:
     * use {} instead of [] and append Type[] to the beginning.
     *
     * @param type  The name of the type of the elements contained in the array.
     * @param array The string received from Arrays.toString().
     * @return The formatted array.
     */
    private static String formatArray(String type, String array) {
        return type + "[] {" + array.substring(1, array.length() - 1) + "}";
    }

    /**
     * Indents everything except the first line in a string by four spaces.
     *
     * @param str The string to indent.
     * @return The indented string.
     */
    private static String indent(String str) {
        return str.replace("\n", "\n    ");
    }
}
