package com.apex.model;

import com.apex.math.Vector2f;
import com.apex.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

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
}
