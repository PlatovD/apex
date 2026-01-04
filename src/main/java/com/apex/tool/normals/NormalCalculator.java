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
        List<Vector3f> normals = model.normals;
        normals.clear();

        Map<Integer, Vector3f> sumNormals = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            sumNormals.put(i, new Vector3f());
        }

        for (Polygon polygon : model.polygons) {
            List<Integer> indices = polygon.getVertexIndices();
            if (indices.size() < 3) continue;

            Vector3f v0 = vertices.get(indices.get(0));
            Vector3f v1 = vertices.get(indices.get(1));
            Vector3f v2 = vertices.get(indices.get(2));

            Vector3f edge1 = v1.subtract(v0);
            Vector3f edge2 = v2.subtract(v0);

            Vector3f faceNormal = edge1.cross(edge2);

            if (faceNormal.lengthSquared() < 1e-12) continue;

            faceNormal = faceNormal.normalize();

            for (int idx : indices) {
                sumNormals.get(idx).addLocal(faceNormal);
            }
        }

        normals.clear();
        for (int i = 0; i < vertices.size(); i++) {
            Vector3f sumNormal = sumNormals.get(i);
            if (sumNormal.lengthSquared() < 1e-12) {
                normals.add(new Vector3f(0, 0, 0));
            } else {
                normals.add(sumNormal.normalize());
            }
        }
    }

    public static Vector3f calculatePolygonNormal(Vector3f v0, Vector3f v1, Vector3f v2) {
        Vector3f edge1 = v1.subtract(v0);
        Vector3f edge2 = v2.subtract(v0);
        Vector3f normal = edge1.cross(edge2);

        if (normal.lengthSquared() < 1e-12) {
            return new Vector3f(0, 0, 0);
        }

        return normal.normalize();
    }
}