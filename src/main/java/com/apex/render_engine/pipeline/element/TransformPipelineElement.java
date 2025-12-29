package com.apex.render_engine.pipeline.element;

import com.apex.core.Constants;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.math.Vector3f;
import com.apex.model.Camera;
import com.apex.model.Model;
import com.apex.render_engine.GraphicConveyor;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;

import static com.apex.render_engine.GraphicConveyor.*;

@AutoCreation
public class TransformPipelineElement implements PipelineElement {

    @AutoInject
    private Camera camera;

    @Override
    public void apply(Model model) {
        if (model.workVertices == null || model.workVertices.length != model.vertices.size() * 3) {
            model.workVertices = new float[model.vertices.size() * 3];
        }

        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        for (int i = 0; i < model.vertices.size(); i++) {
            Vector3f v = model.vertices.get(i);

            javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(v.getX(), v.getY(), v.getZ());
            Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

            int offset = i * 3;
            model.workVertices[offset] = resultPoint.x;
            model.workVertices[offset + 1] = resultPoint.y;
            model.workVertices[offset + 2] = 0;
        }
    }
}

