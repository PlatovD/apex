package com.apex.render_engine.pipeline.element;

import com.apex.model.scene.RenderObject;
import com.apex.model.scene.ZBuffer;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.exception.RasterizationException;
import com.apex.model.scene.FrameBuffer;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.tool.rasterization.Rasterization;

@AutoCreation
public class RasterizationPipelineElement implements PipelineElement {
    @AutoInject
    private FrameBuffer frameBuffer;

    @AutoInject
    private ZBuffer zBuffer;

    @Override
    public void apply(RenderObject ro) {
        for (Polygon polygon : ro.getModel().polygons) {
            if (polygon.getVertexIndices().size() != 3)
                throw new RasterizationException("One of polygons is not triangle");
            int vertex1Index = polygon.getVertexIndices().get(0);
            int vertex2Index = polygon.getVertexIndices().get(1);
            int vertex3Index = polygon.getVertexIndices().get(2);
            float[] rawVertices = ro.getWorkVertices();
            Rasterization.drawTriangle(frameBuffer, zBuffer,
                    Math.round(rawVertices[vertex1Index * 3]), Math.round(rawVertices[vertex1Index * 3 + 1]), rawVertices[vertex1Index * 3 + 2],
                    Math.round(rawVertices[vertex2Index * 3]), Math.round(rawVertices[vertex2Index * 3 + 1]), rawVertices[vertex2Index * 3 + 2],
                    Math.round(rawVertices[vertex3Index * 3]), Math.round(rawVertices[vertex3Index * 3 + 1]), rawVertices[vertex3Index * 3 + 2]
            );
        }
    }

    @Override
    public void prepare() {
        zBuffer.clear();
        frameBuffer.clear();
    }
}
