package com.apex.render.pipeline.element;

import com.apex.buffer.RasterizationBuffer;
import com.apex.core.Constants;
import com.apex.exception.RasterizationException;
import com.apex.math.Vector3f;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.model.scene.RenderObject;
import com.apex.model.scene.ZBuffer;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.tool.rasterization.Rasterization;
import com.apex.tool.rasterization.VertexAttribute;
import com.apex.util.ActiveCameraWrapper;

@AutoCreation
public class WireFramePipelineElement implements PipelineElement {
    @AutoInject
    private RasterizationBuffer rb;

    @AutoInject
    private ZBuffer zBuffer;

    @AutoInject
    private ActiveCameraWrapper activeCameraWrapper;

    @Override
    public void apply(RenderObject ro) {
        double[] coefficients = new double[2];
        Model model = ro.getModel();
        float[] rawVertices = ro.getWorkVertices();
        VertexAttribute vertex0Attribute = new VertexAttribute();
        VertexAttribute vertex1Attribute = new VertexAttribute();
        VertexAttribute vertex2Attribute = new VertexAttribute();

        Vector3f firstVertex;
        Vector3f secondVertex;
        Vector3f thirdVertex;

        for (Polygon polygon : ro.getModel().polygons) {
            if (polygon.getVertexIndices().size() != 3)
                throw new RasterizationException("One of polygons is not triangle");

            int vertex1Index = polygon.getVertexIndices().get(0);
            int vertex2Index = polygon.getVertexIndices().get(1);
            int vertex3Index = polygon.getVertexIndices().get(2);

            firstVertex = model.vertices.get(vertex1Index);
            secondVertex = model.vertices.get(vertex2Index);
            thirdVertex = model.vertices.get(vertex3Index);

            // обновил координаты
            {
                refreshVertexAttributeForPolygon(vertex0Attribute, vertex1Index, rawVertices);
                refreshVertexAttributeForPolygon(vertex1Attribute, vertex2Index, rawVertices);
                refreshVertexAttributeForPolygon(vertex2Attribute, vertex3Index, rawVertices);
            }
            if (!isOnScreen(vertex0Attribute, vertex1Attribute, vertex2Attribute, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT))
                continue;
            if (!isVisibleByNormal(firstVertex, secondVertex, thirdVertex, activeCameraWrapper.getActiveCamera().getTarget().subtract(activeCameraWrapper.getActiveCamera().getPosition()).normalizeLocal())) {
                continue;
            }
            if (!isVisibleByZBuffer(vertex0Attribute, vertex1Attribute, vertex2Attribute)) {
                continue;
            }
            Rasterization.drawWireFrameTriangle2D(rb, vertex0Attribute, vertex1Attribute, vertex2Attribute, Constants.WIREFRAME_COLOR);
        }
    }

    private void refreshVertexAttributeForPolygon(VertexAttribute vertexAttribute, int vertexIndex, float[] rawVertices) {
        vertexAttribute.x = Math.round(rawVertices[vertexIndex * 4]);
        vertexAttribute.y = Math.round(rawVertices[vertexIndex * 4 + 1]);
        vertexAttribute.z = rawVertices[vertexIndex * 4 + 2];
        vertexAttribute.invW = rawVertices[vertexIndex * 4 + 3] == 0 ? 1 / Constants.EPS : 1 / rawVertices[vertexIndex * 4 + 3];
    }

    private boolean isOnScreen(VertexAttribute v0, VertexAttribute v1, VertexAttribute v2, int screenWidth, int screenHeight) {
        boolean lefter = v0.x < 0 && v1.x < 0 && v2.x < 0;
        boolean righter = v0.x >= screenWidth && v1.x >= screenWidth && v2.x >= screenWidth;
        boolean toper = v0.y < 0 && v1.y < 0 && v2.y < 0;
        boolean downer = v0.y >= screenHeight && v1.y >= screenHeight && v2.y >= screenHeight;
        boolean nearer = v0.z < -1;
        boolean fairer = v0.z > 1;
        return !(lefter || righter || toper || downer || nearer | fairer);
    }

    private boolean isVisibleByNormal(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f camera) {
        Vector3f u = vertex1.subtract(vertex2);
        Vector3f v = vertex3.subtract(vertex2);
        u.crossLocal(v);
        u.normalizeLocal();
        return u.dot(camera) > Constants.WIREFRAME_GAP;
    }

    private boolean isVisibleByZBuffer(VertexAttribute v0, VertexAttribute v1, VertexAttribute v2) {
        return zBuffer.canSetPixel(v0.x, v0.y, v0.z - Constants.EPS) && zBuffer.canSetPixel(v1.x, v1.y, v1.z - Constants.EPS) && zBuffer.canSetPixel(v2.x, v2.y, v2.z - Constants.EPS);
    }

    @Override
    public void prepare() {
    }
}
