package org.tagsys.tagsee.utils;

import java.util.HashSet;
import java.util.Set;

public class FilterUtils {

    public static int tags_len = 104;

    public static boolean isWantedTag(String epc) {

        if (epc.substring(0, 20).equalsIgnoreCase("300833B2DDD901400000")) {
            int index = Integer.parseInt(epc.substring(20, 24));
            return index > 0 && index <= tags_len;

        }
        return false;
    }

}
