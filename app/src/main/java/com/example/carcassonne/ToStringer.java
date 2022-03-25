package com.example.carcassonne;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ToStringer {
    private StringBuilder string;

    public ToStringer(String name) {
        this.string = new StringBuilder(name);
        this.string.append(" {\n");
    }

    public <T> void add(String fieldName, T val) {
        this.string.append("    ");
        this.string.append(fieldName);
        this.string.append(" = ");
        this.string.append(stringify(val));
        this.string.append(";\n");
    }

    @Override
    public String toString() {
        this.string.append("}");
        return this.string.toString();
    }

    public static String stringify(Object object) {
        if (object == null) {
            return "null";
        } else if (object instanceof String) {
            return "\"" + ((String)object).replace("\"", "\\\"") + "\"";
        } else if (object instanceof Character) {
            Character c = (Character)object;
            return (c == '\'') ? "'\\''" : "'" + c + "'";
        } else if (object instanceof boolean[]) {
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
            /*
             * External Citation
             * Date: 24 March 2022
             * Problem: Required a way to know if some Object is an array or not.
             * Resource:
             *     https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#isArray--
             * Solution: Used this method on the Class of the object.
             */

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

            for (Object elem : arr) {
                str.append("        ");
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
            if (str.charAt(str.length() - 1) != '[') {
                str.setLength(str.length() - 2);
            }

            str.append("\n    }");
            return str.toString();
        } else if (object instanceof Collection) {
            Collection col = (Collection)object;
            StringBuilder str = new StringBuilder(col.getClass().getSimpleName());
            str.append(" {\n");

            for (Object elem : col) {
                str.append("        ");
                str.append(indent(stringify(elem)));
                str.append(",\n");
            }

            if (str.charAt(str.length() - 1) != '[') {
                str.setLength(str.length() - 2);
            }

            str.append("\n    }");
            return str.toString();
        } else if (object instanceof Map) {
            Map map = (Map)object;
            StringBuilder str = new StringBuilder(map.getClass().getSimpleName());
            str.append(" {\n");

            for (Object entry : map.entrySet()) {
                str.append("        ");
                str.append(indent(stringify(entry)));
                str.append(",\n");
            }

            if (str.charAt(str.length() - 1) != '[') {
                str.setLength(str.length() - 2);
            }

            str.append("\n    }");
            return str.toString();
        } else if (object instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry)object;
            return "[" + stringify(entry.getKey()) + "] = " + stringify(entry.getValue());
        }

        return indent(object.toString());
    }

    private static String formatArray(String type, String array) {
        return type + "[] {" + array.substring(1, array.length() - 1) + "}";
    }

    private static String indent(String str) {
        return str.replace("\n", "\n    ");
    }
}
