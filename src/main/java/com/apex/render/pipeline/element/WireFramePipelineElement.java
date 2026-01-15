package com.apex.render.pipeline.element;

import com.apex.buffer.RasterizationBuffer;
import com.apex.core.Constants;
import com.apex.exception.RasterizationException;
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
        Model model = ro.getModel();
        float[] rawVertices = ro.getWorkVertices();
        VertexAttribute vertex0Attribute = new VertexAttribute();
        VertexAttribute vertex1Attribute = new VertexAttribute();
        VertexAttribute vertex2Attribute = new VertexAttribute();

        for (Polygon polygon : ro.getModel().polygons) {
            if (polygon.getVertexIndices().size() != 3)
                throw new RasterizationException("One of polygons is not triangle");

            int vertex1Index = polygon.getVertexIndices().get(0);
            int vertex2Index = polygon.getVertexIndices().get(1);
            int vertex3Index = polygon.getVertexIndices().get(2);

            // обновил координаты
            {
                refreshVertexAttributeForPolygon(vertex0Attribute, vertex1Index, rawVertices);
                refreshVertexAttributeForPolygon(vertex1Attribute, vertex2Index, rawVertices);
                refreshVertexAttributeForPolygon(vertex2Attribute, vertex3Index, rawVertices);
            }

            Rasterization.drawWireFrameTriangle(rb, zBuffer, vertex0Attribute, vertex1Attribute, vertex2Attribute, Constants.WIREFRAME_COLOR);
        }
    }

    private void refreshVertexAttributeForPolygon(VertexAttribute vertexAttribute, int vertexIndex, float[] rawVertices) {
        vertexAttribute.x = Math.round(rawVertices[vertexIndex * 4]);
        vertexAttribute.y = Math.round(rawVertices[vertexIndex * 4 + 1]);
        vertexAttribute.z = rawVertices[vertexIndex * 4 + 2];
        vertexAttribute.invW = rawVertices[vertexIndex * 4 + 3] == 0 ? 1 / Constants.EPS : 1 / rawVertices[vertexIndex * 4 + 3];
    }


    @Override
    public void prepare() {
    }
}
