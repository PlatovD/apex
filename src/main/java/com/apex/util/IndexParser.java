package com.apex.util;

import java.util.HashSet;
import java.util.Set;

public class IndexParser {
    public static Set<Integer> parseIndices(String input) {
        Set<Integer> indices = new HashSet<>();
        if (input == null || input.isBlank()) {
            return indices;
        }

        String[] parts = input.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.contains("-")) {
                String[] range = part.split("-");
                if (range.length == 2) {
                    try {
                        int start = Integer.parseInt(range[0].trim());
                        int end = Integer.parseInt(range[1].trim());
                        for (int i = Math.min(start, end); i <= Math.max(start, end); i++) {
                            indices.add(i);
                        }
                    } catch (NumberFormatException e) {
                        // ignore invalid ranges
                    }
                }
            } else {
                try {
                    indices.add(Integer.parseInt(part));
                } catch (NumberFormatException e) {
                    // ignore invalid numbers
                }
            }
        }
        return indices;
    }
}
