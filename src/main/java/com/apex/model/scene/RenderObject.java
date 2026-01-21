package com.apex.model.scene;

import com.apex.math.Vector4f;
import com.apex.model.util.RenderObjectStatus;
import com.apex.shader.Shader;
import com.apex.model.geometry.Model;
import com.apex.model.texture.Texture;
import com.apex.math.Matrix4x4;
import com.apex.math.Vector3f;

import java.util.HashSet;
import java.util.Set;

/**
 * Объект-обертка, содержащий все необходимые для рендеринга данные
 */
public class RenderObject {
    private final RenderObjectMetadata metadata;
    private final Model model;

    private BoundingData boundingData = new BoundingData();

    private Matrix4x4 worldMatrix;
    private Texture texture;
    private boolean textured = false;
    private Shader shader;
    private float[] workVertices;
    private Set<Integer> selectedVertexIndices = new HashSet<>();
    private Set<Integer> selectedPolygonIndices = new HashSet<>();
    private float[] workNormals;

    public RenderObject(String filename, Model model, Shader shader, Texture texture) {
        metadata = new RenderObjectMetadata(filename, true, RenderObjectStatus.ACTIVE);
        this.model = model;
        this.shader = shader;
        this.texture = texture;
        this.worldMatrix = new Matrix4x4(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1},
        });
        this.workVertices = new float[model.vertices.size() * 4];
        this.workNormals = new float[model.normals.size() * 3];
    }

    public void refreshBounding(float scaleX, float scaleY, float scaleZ) {
        calcCenterPosition(scaleX, scaleY, scaleZ);
        calculateBoundingRadius(scaleX, scaleY, scaleZ);

        boundingData.centerOfObject.setX(boundingData.centerOfObject.getX() * scaleX);
        boundingData.centerOfObject.setY(boundingData.centerOfObject.getY() * scaleY);
        boundingData.centerOfObject.setZ(boundingData.centerOfObject.getZ() * scaleZ);

        float maxScale = Math.max(Math.abs(scaleX), Math.max(Math.abs(scaleY), Math.abs(scaleZ)));
        boundingData.boundingRadius *= maxScale;
    }

    private void calculateBoundingRadius(float scaleX, float scaleY, float scaleZ) {
        boundingData.boundingRadius = 0;
        float maxDist = 0;

        for (Vector3f vertex : model.vertices) {
            Vector3f distVector = boundingData.centerOfObject.subtract(vertex);
            maxDist = Math.max(distVector.length(), maxDist);
        }

        boundingData.boundingRadius = maxDist;
    }

    public void calcCenterPosition(float scaleX, float scaleY, float scaleZ) {
        Vector3f center = new Vector3f();
        boundingData.centerOfObject = center;
        for (Vector3f vertex : model.vertices) {
            center.addLocal(vertex);
        }
        center.multiplyLocal(1f / model.vertices.size());
    }

    public Model getModel() {
        return model;
    }

    public Matrix4x4 getWorldMatrix() {
        return worldMatrix;
    }

    public void setWorldMatrix(Matrix4x4 worldMatrix) {
        this.worldMatrix = worldMatrix;
    }

    public float[] getWorkVertices() {
        return workVertices;
    }

    public void setWorkVertices(float[] workVertices) {
        this.workVertices = workVertices;
    }

    public float[] getWorkNormals() {
        return workNormals;
    }

    public void setWorkNormals(float[] workNormals) {
        this.workNormals = workNormals;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean isTextured() {
        return textured;
    }

    public void setTextured(boolean textured) {
        this.textured = textured;
    }

    public String getFilename() {
        return metadata.name;
    }

    public RenderObjectStatus getStatus() {
        return metadata.status;
    }

    public void setStatus(RenderObjectStatus newStatus) {
        metadata.status = newStatus;
    }

    public void setVisibility(boolean visibility) {
        metadata.isVisible = visibility;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

    public boolean isVisible() {
        return metadata.isVisible;
    }

    public static class RenderObjectMetadata {
        public String name;
        public boolean isVisible;
        public RenderObjectStatus status;

        public RenderObjectMetadata() {
        }

        public RenderObjectMetadata(String name, boolean isVisible, RenderObjectStatus status) {
            this.name = name;
            this.isVisible = isVisible;
            this.status = status;
        }
    }

    public RenderObjectMetadata getMetadata() {
        return metadata;
    }

    public class BoundingData {
        public Vector3f centerOfObject;
        public float boundingRadius;
    }

    public BoundingData getBoundingData() {
        return boundingData;
    }

    public java.util.Set<Integer> getSelectedVertexIndices() {
        return selectedVertexIndices;
    }

    public void setSelectedVertexIndices(java.util.Set<Integer> selectedVertexIndices) {
        this.selectedVertexIndices = selectedVertexIndices;
    }

    public java.util.Set<Integer> getSelectedPolygonIndices() {
        return selectedPolygonIndices;
    }

    public void setSelectedPolygonIndices(java.util.Set<Integer> selectedPolygonIndices) {
        this.selectedPolygonIndices = selectedPolygonIndices;
    }

    public Vector3f getWorldCenter() {
        if (boundingData == null || boundingData.centerOfObject == null) {
            return null;
        }

        Vector4f localCenter = Vector4f.fromVector3(
                boundingData.centerOfObject,
                1.0f
        );

        Vector4f worldCenter4 = worldMatrix.multiply(localCenter);

        return worldCenter4.toVector3Projected();
    }
}