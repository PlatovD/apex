package com.apex.storage.transformation;

import com.apex.affine.AffineBuilder;
import com.apex.affine.transformations.Axis;
import com.apex.math.Matrix4x4;
import com.apex.math.Vector3f;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;
import com.apex.util.ActiveCameraWrapper;
import com.apex.model.scene.Camera;

import javax.vecmath.Matrix4d;

@AutoCreation
public class TransformationController {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private CameraStorage cameraStorage;

    @AutoInject
    private ActiveCameraWrapper activeCameraWrapper;

    public void updateWorldMatrixForActiveObjects(
            float scaleX, float scaleY, float scaleZ,
            float rotationX, float rotationY, float rotationZ,
            float positionX, float positionY, float positionZ) {

        Matrix4x4 worldMatrix = createAffineMatrix(
                new Vector3f(positionX, positionY, positionZ),
                new Vector3f(rotationX, rotationY, rotationZ),
                new Vector3f(scaleX, scaleY, scaleZ));

        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            ro.setWorldMatrix(worldMatrix.copy());
        }
    }

    private Matrix4x4 createAffineMatrix(Vector3f position,
            Vector3f rotationDegrees,
            Vector3f scale) {

        AffineBuilder builder = new AffineBuilder();

        builder.scale(scale.getX(), scale.getY(), scale.getZ());

        builder.rotateQuat(Axis.X, (float) Math.toRadians(rotationDegrees.getX()));
        builder.rotateQuat(Axis.Y, (float) Math.toRadians(rotationDegrees.getY()));
        builder.rotateQuat(Axis.Z, (float) Math.toRadians(rotationDegrees.getZ()));

        builder.translate(position.getX(), position.getY(), position.getZ());

        Matrix4d affineMatrix = builder.build().getMatrix();
        return toMatrix4x4(affineMatrix);
    }

    public void moveCameraOnVector(Vector3f mouseDelta) {
        if (mouseDelta == null || (mouseDelta.getX() == 0 && mouseDelta.getY() == 0))
            return;

        Camera camera = activeCameraWrapper.getActiveCamera();
        if (camera == null)
            return;

        camera.rotateAroundTarget(mouseDelta.getX(), mouseDelta.getY());
    }

    // панорамирование камеры (движение с зажатой средней кнопкой)
    public void panCamera(Vector3f mouseDelta) {
        if (mouseDelta == null)
            return;

        Camera camera = activeCameraWrapper.getActiveCamera();
        if (camera == null)
            return;

        camera.pan(mouseDelta.getX(), mouseDelta.getY());
    }

    // метод для зума камеры (колесико мыши)
    public void zoomCamera(float amount) {
        Camera camera = activeCameraWrapper.getActiveCamera();
        if (camera == null)
            return;

        camera.zoom(amount);
    }

    // методы для клавиатурного управления
    public void moveCameraForward(float amount) {
        Camera camera = activeCameraWrapper.getActiveCamera();
        if (camera == null)
            return;

        camera.moveForwardBackward(amount);
    }

    public void moveCameraRight(float amount) {
        Camera camera = activeCameraWrapper.getActiveCamera();
        if (camera == null)
            return;

        camera.moveRightLeft(amount);
    }

    public void moveCameraUp(float amount) {
        Camera camera = activeCameraWrapper.getActiveCamera();
        if (camera == null)
            return;

        camera.moveUpDown(amount);
    }

    public void rotateCamera(float deltaX, float deltaY) {
        Camera camera = activeCameraWrapper.getActiveCamera();
        if (camera == null)
            return;

        camera.rotateAroundTarget(deltaX, deltaY);
    }

    // Костыль для преобразования из Matrix4d в Matrix4x4
    private Matrix4x4 toMatrix4x4(Matrix4d m) {
        float[][] matrix = new float[4][4];
        for (int i = 0; i < 4; i++) {
            matrix[i][0] = (float) m.getElement(i, 0);
            matrix[i][1] = (float) m.getElement(i, 1);
            matrix[i][2] = (float) m.getElement(i, 2);
            matrix[i][3] = (float) m.getElement(i, 3);
        }
        return new Matrix4x4(matrix);
    }
}