package capstone.bwa.demo.utils;

import com.sun.xml.bind.v2.util.EditDistance;

public class StringCompareUtils {
    public static double calculateSimilarity(String s1, String s2) {
        String longer = s1.toLowerCase(), shorter = s2.toLowerCase();
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2.toLowerCase();
            shorter = s1.toLowerCase();
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
        return (longerLength - EditDistance.editDistance(longer, shorter)) / (double) longerLength;
    }
}
