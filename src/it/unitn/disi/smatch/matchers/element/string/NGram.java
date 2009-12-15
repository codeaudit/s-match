package it.unitn.disi.smatch.matchers.element.string;

import it.unitn.disi.smatch.MatchManager;
import it.unitn.disi.smatch.matchers.element.IStringBasedElementLevelSemanticMatcher;

import java.util.ArrayList;

/**
 * implements NGram matcher
 * see Element Level Semantic matchers paper for more details
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class NGram implements IStringBasedElementLevelSemanticMatcher {
    private static int gramlength = 3;

    public char match(String str1, String str2) {
        if (null == str1 || null == str2 || 0 == str1.length() || 0 == str2.length()) {
            return MatchManager.IDK_RELATION;
        }
        String[] grams1 = generateNGrams(str1, gramlength);
        String[] grams2 = generateNGrams(str2, gramlength);
        int count = 0;
        for (String aGrams1 : grams1)
            for (String aGrams2 : grams2) {
                if (aGrams1.equals(aGrams2)) {
                    count++;
                    break;
                }
            }
        float sim = (float) 2 * count / (grams1.length + grams2.length); // Dice-Coefficient
        if (MatchManager.ELSMthreshold <= sim) {
            return MatchManager.SYNOMYM;
        } else {
            return MatchManager.IDK_RELATION;
        }
    }

    /**
     * produces nGrams for nGram matcher
     *
     * @param str source string
     * @param gramlength gram length
     * @return ngrams
     */
    private static String[] generateNGrams(String str, int gramlength) {
        if (str == null || str.length() == 0) return null;
        ArrayList<String> grams = new ArrayList<String>();
        int length = str.length();
        String gram;
        if (length < gramlength) {
            for (int i = 1; i <= length; i++) {
                gram = str.substring(0, i);
                if (grams.indexOf(gram) == -1) grams.add(gram);
            }
            gram = str.substring(length - 1, length);
            if (grams.indexOf(gram) == -1) grams.add(gram);
        } else {
            for (int i = 1; i <= gramlength - 1; i++) {
                gram = str.substring(0, i);
                if (grams.indexOf(gram) == -1) grams.add(gram);
            }
            for (int i = 0; i < length - gramlength + 1; i++) {
                gram = str.substring(i, i + gramlength);
                if (grams.indexOf(gram) == -1) grams.add(gram);
            }
            for (int i = length - gramlength + 1; i < length; i++) {
                gram = str.substring(i, length);
                if (grams.indexOf(gram) == -1) grams.add(gram);
            }
        }
        return grams.toArray(new String[grams.size()]);
    }
}
