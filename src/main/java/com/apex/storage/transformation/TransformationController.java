package com.apex.storage.transformation;

import com.apex.math.Matrix4x4;
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
        ro.setWorldMatrix(Matrix4x4.transform(ro.getPosition(), ro.getRotation(), ro.getScale()));
    }

    // todo: сделать нормально и не хранить то, что получили из контроллера в объекте. Хранить только его WorldMatrix
    public void updateWorldMatrixForActiveObjects() {
        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            updateWorldMatrixForObject(ro);
        }
    }

    // todo: сделать управление камерой здесь
}
