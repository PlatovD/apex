package com.apex.tool.normals;

import com.apex.math.Vector3f;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.reflection.AutoCreation;

import java.util.List;

@AutoCreation
public class FaceNormalCalculator implements NormalCalculator {
    @Override
    public void calculateNormals(Model model) {
        model.normals.clear();

        for (Polygon polygon : model.polygons) {
            List<Integer> vertexIndeces = polygon.getVertexIndices();

            Vector3f v0 = model.vertices.get(vertexIndeces.get(0));
            Vector3f v1 = model.vertices.get(vertexIndeces.get(1));
            Vector3f v2 = model.vertices.get(vertexIndeces.get(2));

            Vector3f normal = calculateTriangleNormal(v0, v1, v2);

            model.normals.add(normal);

            int normalIndex = model.normals.size() - 1;
            polygon.setNormalIndices(List.of(normalIndex, normalIndex, normalIndex));
        }
    }

    public Vector3f calculateTriangleNormal(Vector3f v0, Vector3f v1, Vector3f v2) {
        Vector3f u = v1.subtract(v0);
        Vector3f v = v2.subtract(v0);
        Vector3f n = Vector3f.crossProduct(u, v);
        n.normalize();
        return n;
    }
}
