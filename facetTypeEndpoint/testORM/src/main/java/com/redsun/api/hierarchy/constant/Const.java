package com.redsun.api.hierarchy.constant;

/**
 * This class contains constant values used in the hierarchy API.
 * These constants represent keys used for various facets and hierarchy values.
 */

public class Const {
        public static final String FACETTYPE = "facetType";
        public static final String FACETVALUE = "facetValue";
        public static final String FACETTYPEBASE36ID = "facetTypebase36Id";
        public static final String FACETBASE36ID = "base36Id";
        public static final String FACETVALUES = "facetValues";
        public static final String DISPLAYNAME = "displayName";
        public static final String BASE36ID = "base36Id";
        public static final String CLASSCODE = "classCode";
        public static final String PARENTBASE36ID = "parentBase36Id";
        public static final String HIERARCHYVALUES = "hierarchyValues";

        /**
         * Private constructor to prevent instantiation of this constant class.
         */
        private Const() {
                throw new UnsupportedOperationException("This is a constant class and cannot be instantiated");
        }

}
