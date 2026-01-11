package com.apex.model.scene;

import com.apex.tool.colorization.ColorProvider;
import com.apex.model.geometry.Model;
import com.apex.model.texture.Texture;

import javax.vecmath.Matrix4f;

/**
 * Объект-обертка, содержащий все необходимые для рендеринга данные
 */
public class RenderObject {
    private final String filename;
    private final Model model;
    private Matrix4f worldMatrix;
    private Texture texture;
    private boolean textured = false;
    private ColorProvider colorProvider;
    private float[] workVertices;

    private com.apex.math.Vector3f position;
    private com.apex.math.Vector3f rotation;
    private com.apex.math.Vector3f scale;

    public RenderObject(String filename, Model model, ColorProvider colorProvider, Texture texture) {
        this.filename = filename;
        this.model = model;
        this.colorProvider = colorProvider;
        this.texture = texture;
        this.worldMatrix = new Matrix4f();
        this.worldMatrix.setIdentity();
        this.workVertices = new float[model.vertices.size() * 3];

        this.position = new com.apex.math.Vector3f(0f, 0f, 0f);
        this.rotation = new com.apex.math.Vector3f(0f, 0f, 0f);
        this.scale = new com.apex.math.Vector3f(1f, 1f, 1f);
    }

    public void updateWorldMatrix() {
        Matrix4f t = new Matrix4f();
        t.setIdentity();
        t.setTranslation(new javax.vecmath.Vector3f(position.getX(), position.getY(), position.getZ()));

        Matrix4f rX = new Matrix4f();
        rX.rotX((float) Math.toRadians(rotation.getX()));

        Matrix4f rY = new Matrix4f();
        rY.rotY((float) Math.toRadians(rotation.getY()));

        Matrix4f rZ = new Matrix4f();
        rZ.rotZ((float) Math.toRadians(rotation.getZ()));

        Matrix4f s = new Matrix4f();
        s.setIdentity();
        s.m00 = scale.getX();
        s.m11 = scale.getY();
        s.m22 = scale.getZ();

        // Order: T * R * S
        Matrix4f rot = new Matrix4f();
        rot.setIdentity();
        rot.mul(rZ); // Z
        rot.mul(rY); // Y
        rot.mul(rX); // X - convention varies, but this is a reasonable start

        // Final = T * R * S
        this.worldMatrix.setIdentity();
        this.worldMatrix.mul(t);
        this.worldMatrix.mul(rot);
        this.worldMatrix.mul(s);
    }

    public com.apex.math.Vector3f getPosition() {
        return position;
    }

    public com.apex.math.Vector3f getRotation() {
        return rotation;
    }

    public com.apex.math.Vector3f getScale() {
        return scale;
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

    public String getFilename() {
        return filename;
    }
}
