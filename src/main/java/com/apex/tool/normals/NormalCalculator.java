package com.apex.tool.normals;

import com.apex.math.Vector3f;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.reflection.AutoCreation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apex.math.MathUtil.EPSILON;

@AutoCreation
public class NormalCalculator {

    public static void calculateVerticesNormals(Model model) {
        List<Vector3f> vertices = model.vertices;
        model.normals.clear();
        List<Vector3f> normals = model.normals;

        // Суммируем нормали по вершинам
        Map<Integer, Vector3f> sumNormals = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            sumNormals.put(i, new Vector3f()); // нулевой вектор
        }

        for (Polygon polygon : model.polygons) {
            List<Integer> indices = polygon.getVertexIndices();
            if (indices.size() < 3) continue;

            // Получаем три вершины
            Vector3f v0 = vertices.get(indices.get(0));
            Vector3f v1 = vertices.get(indices.get(1));
            Vector3f v2 = vertices.get(indices.get(2));

            // Вычисляем рёбра: v1 - v0, v2 - v0
            Vector3f edge1 = v1.subtract(v0);  // v1 - v0
            Vector3f edge2 = v2.subtract(v0);  // v2 - v0

            // Вычисляем нормаль грани: edge1 × edge2
            Vector3f faceNormal = edge1.cross(edge2);
            faceNormal = faceNormal.normalize(); // нормализуем

            // Добавляем нормаль грани к каждой вершине
            for (int idx : indices) {
                sumNormals.get(idx).addLocal(faceNormal); // sumNormals[idx] += faceNormal
            }

            // Обновляем нормальные индексы
            List<Integer> normalIndices = polygon.getNormalIndices();
            normalIndices.clear();
            normalIndices.addAll(indices);
        }

        // Нормализуем суммированные нормали и добавляем в модель
        for (int i = 0; i < vertices.size(); i++) {
            Vector3f n = sumNormals.get(i);
            if (n.lengthSquared() < EPSILON * EPSILON) {
                normals.add(new Vector3f(0, 0, 0));
            } else {
                normals.add(n.normalize()); // возвращаем нормализованную копию
            }
        }
    }

    public static Vector3f calculatePolygonNormal(Vector3f v0, Vector3f v1, Vector3f v2) {
        Vector3f edge1 = v1.subtract(v0);
        Vector3f edge2 = v2.subtract(v0);
        Vector3f normal = edge1.cross(edge2);
        return normal.normalize();
    }
}