package com.apex.model.scene;


import com.apex.tool.colorization.ColorProvider;
import com.apex.model.geometry.Model;
import com.apex.model.texture.Texture;

import javax.vecmath.Matrix4f;
import java.util.List;

/**
 * Объект-обертка, содержащий все необходимые для рендеринга данные
 */
public class RenderObject {
    private final Model model;
    private Matrix4f worldMatrix;
    private Texture texture;
    private boolean textured = false;
    private ColorProvider colorProvider;
    private float[] workVertices;

    public RenderObject(Model model, ColorProvider colorProvider, Texture texture) {
        this.model = model;
        this.colorProvider = colorProvider;
        this.texture = texture;
        this.worldMatrix = new Matrix4f();
        this.worldMatrix.setIdentity();
        this.workVertices = new float[model.vertices.size() * 3];
    }

    public Model getModel() {
        return model;
    }


    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public void setWorldMatrix(Matrix4f worldMatrix) {
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
}

