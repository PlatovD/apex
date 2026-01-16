package com.apex.modification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RemovalUtils {
    public static <T> void removeElements(List<T> list, Set<Integer> indicesToRemove) {
        if (indicesToRemove != null && !indicesToRemove.isEmpty()) {
            List<Integer> sortedIndices = new ArrayList<>(indicesToRemove);
            Collections.sort(sortedIndices, Collections.reverseOrder());
            java.util.Iterator<Integer> var3 = sortedIndices.iterator();

            while (var3.hasNext()) {
                Integer index = var3.next();
                if (index >= 0 && index < list.size()) {
                    list.remove((int) index);
                }
            }

        }
    }

}
