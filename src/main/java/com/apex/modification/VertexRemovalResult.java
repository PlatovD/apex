package com.apex.modification;

import java.util.Set;

public record VertexRemovalResult(int removedVerticesCount, int removedPolygonsCount,
        Set<Integer> removedPolygonIndices) {
}
