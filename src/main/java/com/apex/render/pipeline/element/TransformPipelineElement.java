package com.apex.render.pipeline.element;

import com.apex.core.Constants;
import com.apex.model.scene.RenderObject;
import com.apex.model.scene.Camera;
import com.apex.model.geometry.Model;
import com.apex.math.Vector3f;
import com.apex.math.Vector4f;
import com.apex.math.Matrix4x4;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.render.ScreenSpaceUtils;

@AutoCreation
public class TransformPipelineElement implements PipelineElement {

    @AutoInject
    private Camera camera;

    @Override
    public void apply(RenderObject ro) {
        Model model = ro.getModel();
        int vertexCount = model.vertices.size();

        if (ro.getWorkVertices() == null || ro.getWorkVertices().length != vertexCount * 3) {
            ro.setWorkVertices(new float[vertexCount * 3]);
        }
        float[] workVertices = ro.getWorkVertices();

        Matrix4x4 worldMatrix = ro.getWorldMatrix();
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projMatrix = camera.getProjectionMatrix();

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
                    ScreenSpaceUtils.toScreenPoint(xNdc, yNdc, zNdc, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

            int offset = i * 3;
            workVertices[offset]     = screenPoint.x;
            workVertices[offset + 1] = screenPoint.y;
            workVertices[offset + 2] = screenPoint.z;
        }
    }

    @Override
    public void prepare() {
    }
}