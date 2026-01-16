package com.apex.modification;

import java.util.*;

public class IndexUtils {
    public static Map<Integer, Integer> createIndexMappingExcluding(Set<Integer> usedIndices, Set<Integer> removedIndices) {
        if (usedIndices != null && !usedIndices.isEmpty()) {
            List<Integer> sortedUsedIndices = new ArrayList(usedIndices);
            Collections.sort(sortedUsedIndices);
            Map<Integer, Integer> mapping = new HashMap();
            int newIndex = 0;

            for(Integer oldIndex : sortedUsedIndices) {
                if (removedIndices == null || !removedIndices.contains(oldIndex)) {
                    mapping.put(oldIndex, newIndex++);
                }
            }

            return mapping;
        } else {
            return Collections.emptyMap();
        }
    }

    public static Map<Integer, Integer> createFullIndexMapping(int totalCount, Set<Integer> removedIndices) {
        Map<Integer, Integer> mapping = new HashMap();
        int newIndex = 0;

        for(int oldIndex = 0; oldIndex < totalCount; ++oldIndex) {
            if (removedIndices == null || !removedIndices.contains(oldIndex)) {
                mapping.put(oldIndex, newIndex++);
            }
        }

        return mapping;
    }

    public static Set<Integer> calculateUsedIndices(int totalCount, Set<Integer> removedIndices) {
        Set<Integer> usedIndices = new HashSet();

        for(int i = 0; i < totalCount; ++i) {
            if (removedIndices == null || !removedIndices.contains(i)) {
                usedIndices.add(i);
            }
        }

        return usedIndices;
    }
}
