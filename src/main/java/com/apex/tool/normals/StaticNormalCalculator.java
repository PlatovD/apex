package com.apex.tool.normals;

import com.apex.math.Vector3f;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticNormalCalculator {


    /**
     * Высчитывает нормали вершин объекта
     * (при необходимости пересчитывает)
     *
     * @param model объект
     */
    public static void calculateVerticesNormals(Model model) {
        List<Vector3f> vertices = model.vertices;
        model.normals.clear();

        List<Vector3f> normals = model.normals;
        Map<Integer, Vector3f> sumNormals = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {
            sumNormals.put(i, new Vector3f());
        }

        for (Polygon polygon : model.polygons) {
            List<Integer> indices = polygon.getVertexIndices();

            List<Integer> normalIndices = polygon.getNormalIndices();
            normalIndices.clear();
            normalIndices.addAll(indices);

            if (indices.size() < 3) continue;

            Vector3f v0 = vertices.get(indices.get(0));
            Vector3f v1 = vertices.get(indices.get(1));
            Vector3f v2 = vertices.get(indices.get(2));

            Vector3f edge1 = v1.subtract(v0);
            Vector3f edge2 = v2.subtract(v0);

            Vector3f polygonNormal = edge1.cross(edge2);

            for (int idx : indices) {
                sumNormals.get(idx).addLocal(polygonNormal);
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            Vector3f normal = sumNormals.get(i);

            if (normal.length() == 0) {
                normals.add(new Vector3f(0, 0, 0));
            } else {
                normal.normalize();
                normals.add(normal);
            }
        }
    }
}
