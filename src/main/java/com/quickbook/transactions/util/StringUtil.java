package com.quickbook.transactions.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class StringUtil {

    public static int findSimilarity(String left, String right) throws IllegalArgumentException{
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return levenshteinDistance.apply(left, right);
    }

    public static boolean isPresent(String value){
        return value != null && value != "";
    }
}
