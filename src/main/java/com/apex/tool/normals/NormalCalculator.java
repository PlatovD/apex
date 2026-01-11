package com.apex.tool.normals;

import com.apex.math.Vector3f;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.reflection.AutoCreation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoCreation
public class NormalCalculator {
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

            Vector3f edge1 = Vector3f.subtract(v1, v0);
            Vector3f edge2 = Vector3f.subtract(v2, v0);

            Vector3f faceNormal = Vector3f.cross(edge1, edge2);

            for (int idx : indices) {
                sumNormals.get(idx).sum(faceNormal);
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            Vector3f n = sumNormals.get(i);

            if (n.length() == 0) {
                normals.add(new Vector3f(0, 0, 0));
            } else {
                n.normalize();
                normals.add(n);
            }
        }
    }

    public static Vector3f calculatePolygonNormal(
            Vector3f v0, Vector3f v1, Vector3f v2) {

        Vector3f edge1 = Vector3f.subtract(v1, v0);
        Vector3f edge2 = Vector3f.subtract(v2, v0);

        Vector3f normal = Vector3f.cross(edge1, edge2);
        normal.normalize();

        return normal;
    }
}

