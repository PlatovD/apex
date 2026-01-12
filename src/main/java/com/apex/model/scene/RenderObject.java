package com.apex.model.scene;

import com.apex.model.util.RenderObjectStatus;
import com.apex.tool.colorization.ColorData;
import com.apex.tool.colorization.ColorProvider;
import com.apex.model.geometry.Model;
import com.apex.model.texture.Texture;
import com.apex.math.Matrix4x4;
import com.apex.math.Vector3f;

/**
 * Объект-обертка, содержащий все необходимые для рендеринга данные
 */
public class RenderObject {
    private final RenderObjectMetadata metadata;
    private final Model model;
    private Matrix4x4 worldMatrix;
    private Texture texture;
    private boolean textured = false;
    private ColorProvider colorProvider;
    private final ColorData colorData = new ColorData();
    private float[] workVertices;

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public RenderObject(String filename, Model model, ColorProvider colorProvider, Texture texture) {
        metadata = new RenderObjectMetadata(filename, true, RenderObjectStatus.ACTIVE);
        this.model = model;
        this.colorProvider = colorProvider;
        this.texture = texture;
        this.worldMatrix = new Matrix4x4();
        this.workVertices = new float[model.vertices.size() * 3];

        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);
    }

    /**
     * Обновляет мировую матрицу объекта на основе позиции, вращения и масштаба.
     * Использует предварительно определённый метод transform.
     */
    public void updateWorldMatrix() {
        this.worldMatrix = Matrix4x4.transform(position, rotation, scale);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
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

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float[] getWorkVertices() {
        return workVertices;
    }

    public void setWorkVertices(float[] workVertices) {
        this.workVertices = workVertices;
    }

    public ColorProvider getColorProvider() {
        return colorProvider;
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

    public void setColorProvider(ColorProvider colorProvider) {
        this.colorProvider = colorProvider;
    }

    public ColorData getColorData() {
        return colorData;
    }
}