package com.apex.storage.transformation;

import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;

import javax.vecmath.Matrix4f;

@AutoCreation
public class TransformationController {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private CameraStorage cameraStorage;

    public void updateWorldMatrixForObject(RenderObject ro) {
        Matrix4f t = new Matrix4f();
        t.setIdentity();
        t.setTranslation(new javax.vecmath.Vector3f(ro.getPosition().getX(), ro.getPosition().getY(), ro.getPosition().getZ()));

        Matrix4f rX = new Matrix4f();
        rX.rotX((float) Math.toRadians(ro.getPosition().getX()));

        Matrix4f rY = new Matrix4f();
        rY.rotY((float) Math.toRadians(ro.getPosition().getY()));

        Matrix4f rZ = new Matrix4f();
        rZ.rotZ((float) Math.toRadians(ro.getPosition().getZ()));

        Matrix4f s = new Matrix4f();
        s.setIdentity();
        s.m00 = ro.getScale().getX();
        s.m11 = ro.getScale().getY();
        s.m22 = ro.getScale().getZ();

        // Order: T * R * S
        Matrix4f rot = new Matrix4f();
        rot.setIdentity();
        rot.mul(rZ); // Z
        rot.mul(rY); // Y
        rot.mul(rX); // X - convention varies, but this is a reasonable start

        // Final = T * R * S
        ro.getWorldMatrix().setIdentity();
        ro.getWorldMatrix().mul(t);
        ro.getWorldMatrix().mul(rot);
        ro.getWorldMatrix().mul(s);
    }

    // todo: сделать нормально и не хранить то, что получили из контроллера в объекте. Хранить только его WorldMatrix
    public void updateWorldMatrixForActiveObjects() {
        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            updateWorldMatrixForObject(ro);
        }
    }
}
