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

    public RenderObject(String filename, Model model, ColorProvider colorProvider, Texture texture) {
        metadata = new RenderObjectMetadata(filename, true, RenderObjectStatus.ACTIVE);
        this.model = model;
        this.colorProvider = colorProvider;
        this.texture = texture;
        this.worldMatrix = new Matrix4x4(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1},
        });
        this.workVertices = new float[model.vertices.size() * 4];
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

    public RenderObjectMetadata getMetadata() {
        return metadata;
    }
}