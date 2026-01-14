package com.apex.storage.transformation;

import com.apex.math.Matrix4x4;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

@AutoCreation
public class TransformationController {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private CameraStorage cameraStorage;

    public void updateWorldMatrixForObject(RenderObject ro) {
//        ro.setWorldMatrix(Matrix4x4.transform(ro.getPosition(), ro.getRotation(), ro.getScale())); // todo: Свят, перевести на Ромины афинки. Лучше поворот через квады.
    }

    // todo: сделать нормально и не хранить то, что получили из контроллера в объекте. Хранить только его WorldMatrix
    public void updateWorldMatrixForActiveObjects(
            float scaleX, float scaleY, float scaleZ,
            float rotationX, float rotationY, float rotationZ,
            float transitionX, float transitionY, float transitionZ
    ) {
        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            updateWorldMatrixForObject(ro);
        }
    }

    private void buildAffine(
            float scaleX, float scaleY, float scaleZ,
            float rotationX, float rotationY, float rotationZ,
            float transitionX, float transitionY, float transitionZ
    ) {
        // здесь билдить афинки
    }


    // todo: сделать управление камерой здесь
    public void moveCameraOnVector(Vector3f moveVector) {
    }
}
