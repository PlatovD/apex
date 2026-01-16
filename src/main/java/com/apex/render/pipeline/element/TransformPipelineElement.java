package com.apex.render.pipeline.element;

import com.apex.core.RuntimeStates;
import com.apex.model.scene.RenderObject;
import com.apex.model.geometry.Model;
import com.apex.math.Vector3f;
import com.apex.math.Vector4f;
import com.apex.math.Matrix4x4;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.util.ActiveCameraWrapper;
import com.apex.util.ScreenSpaceUtils;

@AutoCreation
public class TransformPipelineElement implements PipelineElement {

    @AutoInject
    private ActiveCameraWrapper activeCameraWrapper;

    @AutoInject
    private RuntimeStates runtimeStates;

    @Override
    public void apply(RenderObject ro) {
        Model model = ro.getModel();
        int vertexCount = model.vertices.size();

        if (ro.getWorkVertices() == null || ro.getWorkVertices().length != vertexCount * 4) {
            ro.setWorkVertices(new float[vertexCount * 4]);
        }
        float[] workVertices = ro.getWorkVertices();

        int normalCount = model.normals.size();
        if (ro.getWorkNormals() == null || ro.getWorkNormals().length != normalCount * 3) {
            ro.setWorkNormals(new float[normalCount * 3]);
        }
        float[] workNormals = ro.getWorkNormals();

        Matrix4x4 worldMatrix = ro.getWorldMatrix();
        Matrix4x4 viewMatrix = activeCameraWrapper.getActiveCamera().getViewMatrix();
        Matrix4x4 projMatrix = activeCameraWrapper.getActiveCamera()
                .getProjectionMatrix(runtimeStates.SCENE_WIDTH, runtimeStates.SCENE_HEIGHT);

        Matrix4x4 mvpMatrix = projMatrix.multiply(viewMatrix).multiply(worldMatrix);

        for (int i = 0; i < vertexCount; i++) {
            Vector3f v = model.vertices.get(i);
            Vector4f v4 = new Vector4f(v.getX(), v.getY(), v.getZ(), 1.0f);

            Vector4f transformed = mvpMatrix.multiply(v4);

            float w = transformed.getW();
            if (Math.abs(w) < com.apex.math.MathUtil.EPSILON) {
                w = 1.0f;
            }

            float xNdc = transformed.getX() / w;
            float yNdc = transformed.getY() / w;
            float zNdc = transformed.getZ() / w;

            ScreenSpaceUtils.ScreenPoint screenPoint =
                    ScreenSpaceUtils.toScreenPoint(
                            xNdc, yNdc, zNdc,
                            runtimeStates.SCENE_WIDTH,
                            runtimeStates.SCENE_HEIGHT
                    );

            int offset = i * 4;
            workVertices[offset]     = screenPoint.x;
            workVertices[offset + 1] = screenPoint.y;
            workVertices[offset + 2] = screenPoint.z;
            workVertices[offset + 3] = w;
        }
        // worldMatrix * normal (w = 0) + normalize
        for (int i = 0; i < normalCount; i++) {
            Vector3f n = model.normals.get(i);
            Vector4f n4 = new Vector4f(n.getX(), n.getY(), n.getZ(), 0.0f);

            Vector4f transformed = worldMatrix.multiply(n4);

            Vector3f worldNormal = new Vector3f(
                    transformed.getX(),
                    transformed.getY(),
                    transformed.getZ()
            ).normalizeLocal();

            int offset = i * 3;
            workNormals[offset]     = worldNormal.getX();
            workNormals[offset + 1] = worldNormal.getY();
            workNormals[offset + 2] = worldNormal.getZ();
        }
    }

    @Override
    public void prepare() {
    }
}
