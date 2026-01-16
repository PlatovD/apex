package com.apex.model.geometry;

import com.apex.math.Vector2f;
import com.apex.math.Vector3f;
import com.apex.util.PolygonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Model {
    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public float[] workVertices;
    public List<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<Polygon> polygons = new ArrayList<Polygon>();

    public Model() {
    }

    public Model(List<Vector3f> vertices, List<Vector2f> textureVertices, List<Vector3f> normals, List<Polygon> polygons) {
        this.vertices = vertices;
        this.textureVertices = textureVertices;
        this.normals = normals;
        this.polygons = polygons;
    }

    public void initWorkVertices() {
        if (workVertices == null || workVertices.length != vertices.size() * 3) {
            workVertices = new float[vertices.size() * 3];
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Model model = (Model) object;
        return Objects.equals(vertices, model.vertices) && Objects.deepEquals(workVertices, model.workVertices) && Objects.equals(textureVertices, model.textureVertices) && Objects.equals(normals, model.normals) && Objects.equals(polygons, model.polygons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, Arrays.hashCode(workVertices), textureVertices, normals, polygons);
    }

    public Model copy() {
        Model model = new Model();
        model.vertices = this.vertices.stream().map(Vector3f::copy).collect(Collectors.toList());
        model.polygons = this.polygons.stream().map(PolygonUtil::deepCopyOfPolygon).collect(Collectors.toList());
        model.normals = this.normals.stream().map(Vector3f::copy).collect(Collectors.toList());
        model.textureVertices = this.textureVertices.stream().map(Vector2f::copy).collect(Collectors.toList());
        return model;
    }
}
