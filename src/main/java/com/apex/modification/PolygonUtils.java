package com.apex.modification;

import com.apex.model.geometry.Polygon;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PolygonUtils {
    public static boolean polygonContainsAnyVertex(Polygon polygon, Set<Integer> vertexIndices) {
        for (Integer vertexIndex : polygon.getVertexIndices()) {
            if (vertexIndices.contains(vertexIndex)) {
                return true;
            }
        }
        return false;
    }

    public static void updatePolygonIndices(Polygon polygon, Map<Integer, Integer> vertexMapping,
            Map<Integer, Integer> textureMapping, Map<Integer, Integer> normalMapping) {
        List<Integer> newVertexIndices = mapIndices(polygon.getVertexIndices(), vertexMapping);
        List<Integer> newTextureIndices = mapIndices(polygon.getTextureVertexIndices(), textureMapping);
        List<Integer> newNormalIndices = mapIndices(polygon.getNormalIndices(), normalMapping);
        polygon.setVertexIndices(new ArrayList<>(newVertexIndices));
        polygon.setTextureVertexIndices(new ArrayList<>(newTextureIndices));
        polygon.setNormalIndices(new ArrayList<>(newNormalIndices));
    }

    private static List<Integer> mapIndices(List<Integer> indices, Map<Integer, Integer> mapping) {
        List<Integer> newIndices = new ArrayList<>();
        for (Integer index : indices) {
            if (mapping.containsKey(index)) {
                newIndices.add(mapping.get(index));
            } else {
                newIndices.add(index);
            }
        }
        return newIndices;
    }
}
