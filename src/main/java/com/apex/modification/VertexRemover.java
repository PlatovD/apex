package com.apex.modification;

import com.apex.model.geometry.Model;
import com.apex.reflection.AutoCreation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AutoCreation
public class VertexRemover {
    private final PolygonDataExtractor polygonDataExtractor;

    public VertexRemover() {
        this.polygonDataExtractor = ModelUtils::collectPolygonIndices;
    }

    public VertexRemovalResult removeVertices(Model model, Set<Integer> vertexIndices, boolean clearUnused) {
        // Find polygons dealing with these vertices
        Set<Integer> polygonsToRemove = ModelUtils.findPolygonsContainingVertices(model, vertexIndices);

        Set<Integer> allVerticesToRemove = new HashSet<>(vertexIndices);
        Set<Integer> textureIndicesToRemove = new HashSet<>();
        Set<Integer> normalIndicesToRemove = new HashSet<>();

        if (clearUnused) {
            this.processUnusedElements(model, polygonsToRemove, allVerticesToRemove, textureIndicesToRemove,
                    normalIndicesToRemove);
        } else {
            // Even if not clearing all unused, we must remove the polygons that contain the
            // deleted vertices
            ModelUtils.removePolygonsFromModel(model, polygonsToRemove);
        }

        ModelUtils.removeVerticesFromModel(model, allVerticesToRemove);

        // Reindex everything
        this.performCleanupAndReindex(model, allVerticesToRemove, textureIndicesToRemove, normalIndicesToRemove,
                clearUnused);

        return new VertexRemovalResult(allVerticesToRemove.size(), polygonsToRemove.size(), polygonsToRemove);
    }

    public VertexRemovalResult removePolygons(Model model, Set<Integer> polygonIndices, boolean clearUnused) {
        Set<Integer> allVerticesToRemove = new HashSet<>();
        Set<Integer> textureIndicesToRemove = new HashSet<>();
        Set<Integer> normalIndicesToRemove = new HashSet<>();

        if (clearUnused) {
            this.processUnusedElements(model, polygonIndices, allVerticesToRemove, textureIndicesToRemove,
                    normalIndicesToRemove);
        } else {
            ModelUtils.removePolygonsFromModel(model, polygonIndices);
        }

        ModelUtils.removeVerticesFromModel(model, allVerticesToRemove);

        // Reindex correctly
        this.performCleanupAndReindex(model, allVerticesToRemove, textureIndicesToRemove, normalIndicesToRemove,
                clearUnused);

        return new VertexRemovalResult(allVerticesToRemove.size(), polygonIndices.size(), polygonIndices);
    }

    private void processUnusedElements(Model model, Set<Integer> polygonsToRemove, Set<Integer> allVerticesToRemove,
            Set<Integer> textureIndicesToRemove, Set<Integer> normalIndicesToRemove) {
        // Collect all indices from the polygons we are about to remove
        this.polygonDataExtractor.extract(model, polygonsToRemove, allVerticesToRemove, textureIndicesToRemove,
                normalIndicesToRemove);
        // Remove the polygons themselves
        ModelUtils.removePolygonsFromModel(model, polygonsToRemove);
    }

    private void performCleanupAndReindex(Model model, Set<Integer> removedVertices, Set<Integer> removedTextureIndices,
            Set<Integer> removedNormalIndices, boolean clearUnused) {
        int initialVertexCount = model.vertices.size() + removedVertices.size();
        // Create mapping for vertices (old index -> new index)
        Map<Integer, Integer> vertexMapping = IndexUtils.createIndexMappingExcluding(
                IndexUtils.calculateUsedIndices(initialVertexCount, removedVertices), // effectively all remaining
                null // The removedVertices are already physically removed from list, so the list
                     // size is smaller.
                     // Wait, CreateIndexMappingExcluding logic expects "usedIndices" which are old
                     // indices that act as source.
                     // Let's refine: We need to map old indices to new ones.
        );

        // Actually, the standard logic:
        // 1. We removed elements from the list.
        // 2. Any index pointing to an element AFTER the removed one needs to be
        // decremented.
        // IndexUtils.createFullIndexMapping is simpler for full reindexing if we know
        // what was removed.

        vertexMapping = IndexUtils.createFullIndexMapping(initialVertexCount, removedVertices);

        Map<Integer, Integer> textureMapping;
        Map<Integer, Integer> normalMapping;

        if (clearUnused) {
            ModelUtils.removeUnusedTextureVertices(model, removedTextureIndices);
            ModelUtils.removeUnusedNormals(model, removedNormalIndices);
            textureMapping = IndexUtils.createFullIndexMapping(
                    model.textureVertices.size() + removedTextureIndices.size(), removedTextureIndices);
            normalMapping = IndexUtils.createFullIndexMapping(model.normals.size() + removedNormalIndices.size(),
                    removedNormalIndices);
        } else {
            // If not clearing unused, mappings are identity or handled differently?
            // For simple vertex removal without clearing unused texture/normals, those
            // lists stay same size.
            // But vertices list CHANGED size. So vertexMapping is mandatory.
            textureMapping = IndexUtils.createFullIndexMapping(model.textureVertices.size(), Set.of());
            normalMapping = IndexUtils.createFullIndexMapping(model.normals.size(), Set.of());
        }

        ModelUtils.reindexModel(model, vertexMapping, textureMapping, normalMapping);
    }
}
