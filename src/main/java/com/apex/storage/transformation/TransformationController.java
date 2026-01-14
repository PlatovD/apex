package com.apex.storage.transformation;

import com.apex.affine.AffineBuilder;
import com.apex.affine.transformations.Axis;
import com.apex.math.Matrix4x4;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.Map;

@AutoCreation
public class TransformationController {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private CameraStorage cameraStorage;

    // Кэш трансформаций для каждого объекта — чтобы хранить position, rotation, scale
    private final Map<RenderObject, TransformState> transformStateMap = new HashMap<>();

    // Получить состояние трансформации для объекта
    private TransformState getState(RenderObject ro) {
        return transformStateMap.computeIfAbsent(ro, k -> new TransformState());
    }

    public void updateWorldMatrixForObject(RenderObject ro) {
        TransformState state = getState(ro);
        AffineBuilder builder = new AffineBuilder();

        // Масштаб
        builder.scale(state.scale.x, state.scale.y, state.scale.z);

        // Поворот — через кватернионы (XYZ порядок)
        builder.rotateQuat(Axis.X, (float) Math.toRadians(state.rotation.x));
        builder.rotateQuat(Axis.Y, (float) Math.toRadians(state.rotation.y));
        builder.rotateQuat(Axis.Z, (float) Math.toRadians(state.rotation.z));

        // Перемещение
        builder.translate(state.position.x, state.position.y, state.position.z);

        // Собираем матрицу и конвертируем Matrix4d → Matrix4x4
        Matrix4d affineMatrix = builder.build().getMatrix();
        ro.setWorldMatrix(toMatrix4x4(affineMatrix));
    }

    public void updateWorldMatrixForActiveObjects(
            float scaleX, float scaleY, float scaleZ,
            float rotationX, float rotationY, float rotationZ,
            float transitionX, float transitionY, float transitionZ
    ) {
        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            TransformState state = getState(ro);
            state.position.set(transitionX, transitionY, transitionZ);
            state.rotation.set(rotationX, rotationY, rotationZ);
            state.scale.set(scaleX, scaleY, scaleZ);
            updateWorldMatrixForObject(ro);
        }
    }

    public void moveCameraOnVector(javax.vecmath.Vector3f moveVector) {
        // TODO: Позже
    }

    // Костыль для конвертации Matrix4d → Matrix4x4
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

    private static class TransformState {
        Vector3f position = new Vector3f(0, 0, 0);
        Vector3f rotation = new Vector3f(0, 0, 0); // в градусах
        Vector3f scale = new Vector3f(1, 1, 1);
    }
}