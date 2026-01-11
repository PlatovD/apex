package com.apex.render.pipeline.element;

import com.apex.core.Constants;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.math.Vector3f;
import com.apex.model.scene.Camera;
import com.apex.model.geometry.Model;
import com.apex.render.GraphicConveyor;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;

import static com.apex.render.GraphicConveyor.vertexToPoint;

@AutoCreation
public class TransformPipelineElement implements PipelineElement {

    @AutoInject
    private Camera camera;

    @Override
    public void apply(RenderObject ro) {
        if (ro.getWorkVertices() == null || ro.getWorkVertices().length != ro.getModel().vertices.size() * 3) {
            ro.setWorkVertices(new float[ro.getModel().vertices.size() * 3]);
        }

        Model model = ro.getModel();
        float[] workVertices = ro.getWorkVertices();

        Matrix4f modelMatrix = ro.getWorldMatrix();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        for (int i = 0; i < ro.getModel().vertices.size(); i++) {
            Vector3f v = model.vertices.get(i);

            javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(v.getX(), v.getY(), v.getZ());
            javax.vecmath.Vector3f projectionPoint = GraphicConveyor.multiplyMatrix4ByVector3(modelViewProjectionMatrix,
                    vertexVecmath);
            Point2f resultPoint = vertexToPoint(projectionPoint, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

            int offset = i * 3;
            workVertices[offset] = resultPoint.x;
            workVertices[offset + 1] = resultPoint.y;
            workVertices[offset + 2] = projectionPoint.z;
        }
    }

    @Override
    public void prepare() {
    }
}
