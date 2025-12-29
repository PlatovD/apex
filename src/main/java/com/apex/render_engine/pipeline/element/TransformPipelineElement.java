package com.apex.render_engine.pipeline.element;

import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.math.Vector3f;
import com.apex.model.Camera;
import com.apex.model.Model;
import com.apex.render_engine.GraphicConveyor;

import javax.vecmath.Matrix4f;

@AutoCreation
public class TransformPipelineElement implements PipelineElement {

    @AutoInject
    private Camera camera;

    @Override
    public void apply(Model model) {
        if (model.workVertices == null || model.workVertices.length != model.vertices.size() * 3) {
            model.workVertices = new float[model.vertices.size() * 3];
        }

        Matrix4f modelMatrix = GraphicConveyor.rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f mvp = new Matrix4f();
        mvp.setIdentity();
        mvp.mul(projectionMatrix);
        mvp.mul(viewMatrix);
        mvp.mul(modelMatrix);

        for (int i = 0; i < model.vertices.size(); i++) {
            Vector3f v = model.vertices.get(i);

            javax.vecmath.Vector4f vertexVec = new javax.vecmath.Vector4f(v.getX(), v.getY(), v.getZ(), 1.0f);

            mvp.transform(vertexVec);

            if (vertexVec.w != 0) {
                vertexVec.x /= vertexVec.w;
                vertexVec.y /= vertexVec.w;
                vertexVec.z /= vertexVec.w;
            }

            int offset = i * 3;
            model.workVertices[offset] = vertexVec.x;
            model.workVertices[offset + 1] = vertexVec.y;
            model.workVertices[offset + 2] = vertexVec.z;
        }
    }
}

