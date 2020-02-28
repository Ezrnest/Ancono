package cn.ancono.utilities.content_generator;

import java.util.LinkedList;
import java.util.List;

/**
 * a name is consisted of several word that divided by space
 *
 * @author rw185035
 */
public class Name {
    /**
     * the first name of the name
     */
    private String first;
    /**
     * the last name of the name
     */
    private String last;

    /**
     * the middle parts of the name
     */
    private List<String> parts;

    private Name(String firstName, String lastName, List<String> parts) {
        this.first = firstName;
        this.last = lastName;
        this.parts = parts;
    }

    public static NameBuilder getBuilder() {
        return new NameBuilder();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(first).append(' ');
        for (String s : parts) {
            sb.append(s).append(' ');
        }
        sb.append(last);
        return sb.toString();
    }


    public static class NameBuilder {
        /**
         * the first name of the name
         */
        private String first;
        /**
         * the last name of the name
         */
        private String last;

        /**
         * the middle parts of the name
         */
        private List<String> parts;

        private boolean builded = false;

        /**
         * create a name builder
         */
        private NameBuilder() {
            parts = new LinkedList<String>();
        }

        ;

        public NameBuilder setFirstName(String first) {
            checkBuilding();
            this.first = first;
            return this;
        }

        public NameBuilder setLastName(String last) {
            checkBuilding();
            this.last = last;
            return this;
        }

        public NameBuilder addMiddle(String middle) {
            checkBuilding();
            this.parts.add(middle);
            return this;
        }

        private void checkBuilding() {
            if (builded) {
                throw new IllegalStateException("Builded");
            }
        }

        public Name build() {
            checkBuilding();
            if (last == null || first == null)
                throw new IllegalArgumentException("No name");

            builded = true;
            return new Name(first, last, parts);
        }
    }

}
