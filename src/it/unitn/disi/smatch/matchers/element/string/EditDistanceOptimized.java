package it.unitn.disi.smatch.matchers.element.string;

import it.unitn.disi.smatch.MatchManager;
import it.unitn.disi.smatch.matchers.element.IStringBasedElementLevelSemanticMatcher;

/**
 * Optimized edit distance.
 * http://www.merriampark.com/ldjava.htm
 * Levenshtein Distance Algorithm: Java Implementation by Chas Emerick.
 *
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class EditDistanceOptimized implements IStringBasedElementLevelSemanticMatcher {

	/**
	 * Computes the relation with optimized edit distance matcher.
	 *
	 * @param str1 source input string
	 * @param str2 target input string
	 * @return synonym or IDK relation
	 */
    public char match(String str1, String str2) {
        if (null == str1 || null == str2 || 0 == str1.length() || 0 == str2.length()) {
            return MatchManager.IDK_RELATION;
        }
        float sim = 1 - (float) getLevenshteinDistance(str1, str2) / java.lang.Math.max(str1.length(), str2.length());
        if (MatchManager.ELSMthreshold <= sim) {
            return MatchManager.SYNOMYM;
        } else {
            return MatchManager.IDK_RELATION;
        }
    }

    public static int getLevenshteinDistance(String s, String t) {
        /*
          The difference between this impl. and the previous is that, rather
           than creating and retaining a matrix of size s.length()+1 by t.length()+1,
           we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
           is the 'current working' distance array that maintains the newest distance cost
           counts as we iterate through the characters of String s.  Each time we increment
           the index of String t we are comparing, d is copied to p, the second int[].  Doing so
           allows us to retain the previous cost counts as required by the algorithm (taking
           the minimum of the cost count to the left, up one, and diagonally up and to the left
           of the current cost count being calculated).  (Note that the arrays aren't really
           copied anymore, just switched...this is clearly much better than cloning an array
           or doing a System.arraycopy() each time  through the outer loop.)

           Effectively, the difference between the two implementations is this one does not
           cause an out of memory condition when calculating the LD over two very large strings.
        */

        int n = s.length(); // length of input string 1
        int m = t.length(); // length of input string 2

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        int p[] = new int[n + 1]; //'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }
}