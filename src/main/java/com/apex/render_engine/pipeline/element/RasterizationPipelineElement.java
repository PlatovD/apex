package com.apex.render_engine.pipeline.element;

import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.exception.RasterizationException;
import com.apex.model.FrameBuffer;
import com.apex.model.Model;
import com.apex.model.Polygon;
import com.apex.tool.rasterization.Rasterization;
import javafx.scene.image.PixelWriter;

@AutoCreation
public class RasterizationPipelineElement implements PipelineElement {
    @AutoInject
    private FrameBuffer frameBuffer;

    @Override
    public void apply(Model model) {
        frameBuffer.clear();
        for (Polygon polygon : model.polygons) {
            if (polygon.getVertexIndices().size() != 3)
                throw new RasterizationException("One of polygons is not triangle");
            int vertex1Index = polygon.getVertexIndices().get(0);
            int vertex2Index = polygon.getVertexIndices().get(1);
            int vertex3Index = polygon.getVertexIndices().get(2);
            float[] rawVertices = model.workVertices;
            Rasterization.drawTriangle(frameBuffer,
                    (int) Math.ceil(rawVertices[vertex1Index * 3]), (int) Math.ceil(rawVertices[vertex1Index * 3 + 1]),
                    (int) Math.ceil(rawVertices[vertex2Index * 3]), (int) Math.ceil(rawVertices[vertex2Index * 3 + 1]),
                    (int) Math.ceil(rawVertices[vertex3Index * 3]), (int) Math.ceil(rawVertices[vertex3Index * 3 + 1])
            );
        }
    }
}
