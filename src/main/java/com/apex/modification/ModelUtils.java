package com.apex.modification;

import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;

import java.util.*;

public class ModelUtils {
    public static Set<Integer> findPolygonsContainingVertices(Model model, Set<Integer> vertexIndices) {
        Set<Integer> polygonsToRemove = new HashSet<>();

        for (int polygonIndex = 0; polygonIndex < model.polygons.size(); ++polygonIndex) {
            Polygon polygon = model.polygons.get(polygonIndex);
            boolean contains = PolygonUtils.polygonContainsAnyVertex(polygon, vertexIndices);
            if (contains) {
                polygonsToRemove.add(polygonIndex);
            }
        }

        return polygonsToRemove;
    }

    public static void removePolygonsFromModel(Model model, Set<Integer> polygonsToRemove) {
        RemovalUtils.removeElements(model.polygons, polygonsToRemove);
    }

    public static void removeVerticesFromModel(Model model, Set<Integer> verticesToRemove) {
        RemovalUtils.removeElements(model.vertices, verticesToRemove);
    }

    public static void collectPolygonIndices(Model model, Set<Integer> polygonIndices, Set<Integer> vertices,
            Set<Integer> textureVertices, Set<Integer> normals) {
        for (Integer polygonIndex : polygonIndices) {
            if (polygonIndex >= 0 && polygonIndex < model.polygons.size()) {
                Polygon polygon = model.polygons.get(polygonIndex);
                vertices.addAll(polygon.getVertexIndices());
                textureVertices.addAll(polygon.getTextureVertexIndices());
                normals.addAll(polygon.getNormalIndices());
            }
        }
    }

    public static void reindexModel(Model model, Map<Integer, Integer> vertexMapping,
            Map<Integer, Integer> textureMapping, Map<Integer, Integer> normalMapping) {
        for (Polygon polygon : model.polygons) {
            PolygonUtils.updatePolygonIndices(polygon, vertexMapping, textureMapping, normalMapping);
        }
    }

    public static void removeUnusedTextureVertices(Model model, Set<Integer> textureIndicesToRemove) {
        if (!textureIndicesToRemove.isEmpty()) {
            RemovalUtils.removeElements(model.textureVertices, textureIndicesToRemove);
        }
    }

    public static void removeUnusedNormals(Model model, Set<Integer> normalIndicesToRemove) {
        if (!normalIndicesToRemove.isEmpty()) {
            RemovalUtils.removeElements(model.normals, normalIndicesToRemove);
        }
    }
}
