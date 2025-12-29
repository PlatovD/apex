package com.apex.tool.triangulator;


import com.apex.reflection.AutoCreation;
import com.apex.model.Model;
import com.apex.model.Polygon;
import com.apex.util.PolygonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoCreation
public class SimpleTriangulator implements Triangulator {
    @Override
    public List<Polygon> triangulatePolygon(Model model, Polygon polygon) {
        // получаю вершины полигона в виде точек
        List<Integer> verticesIndexes = polygon.getVertexIndices();
        int indexOfVertexInPolygon = 0;
        Map<Integer, Integer> textureIndexesMap = new HashMap<>();
        Map<Integer, Integer> normalsIndexesMap = new HashMap<>();
        for (Integer vertexIndex : verticesIndexes) {
            if (vertexIndex < polygon.getTextureVertexIndices().size())
                textureIndexesMap.put(vertexIndex, polygon.getTextureVertexIndices().get(indexOfVertexInPolygon));
            if (vertexIndex < polygon.getNormalIndices().size())
                normalsIndexesMap.put(vertexIndex, polygon.getNormalIndices().get(indexOfVertexInPolygon));
            indexOfVertexInPolygon++;
        }

        // начинаю обработку вершин и создание новых полигонов
        List<Polygon> newPolygons = new ArrayList<>();
        int n = verticesIndexes.size();
        int firstVertexIndex = 0;
        int secondVertexIndex = 1;
        int thirdVertexIndex = 2;
        while (thirdVertexIndex < n) {
            Polygon newPolygon = PolygonUtil.createNewPolygon(List.of(
                    verticesIndexes.get(firstVertexIndex),
                    verticesIndexes.get(secondVertexIndex),
                    verticesIndexes.get(thirdVertexIndex)
            ), textureIndexesMap, normalsIndexesMap);
            newPolygons.add(newPolygon);
            secondVertexIndex++;
            thirdVertexIndex++;
        }
        return newPolygons;
    }
}
