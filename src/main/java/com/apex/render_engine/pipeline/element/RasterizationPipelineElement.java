package com.apex.render_engine.pipeline.element;

import com.apex.buffer.RasterizationBuffer;
import com.apex.math.Vector2f;
import com.apex.model.scene.RenderObject;
import com.apex.model.scene.ZBuffer;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.exception.RasterizationException;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.tool.rasterization.Rasterization;
import com.apex.tool.rasterization.VertexAttribute;

import java.util.List;

@AutoCreation
public class RasterizationPipelineElement implements PipelineElement {
    @AutoInject
    private RasterizationBuffer rb;

    @AutoInject
    private ZBuffer zBuffer;

    @Override
    public void apply(RenderObject ro) {
        VertexAttribute vertex1Attribute = new VertexAttribute();
        VertexAttribute vertex2Attribute = new VertexAttribute();
        VertexAttribute vertex3Attribute = new VertexAttribute();

        Vector2f textureVertex1;
        Vector2f textureVertex2;
        Vector2f textureVertex3;

        Model model;
        List<Integer> textureIndices;
        for (Polygon polygon : ro.getModel().polygons) {
            if (polygon.getVertexIndices().size() != 3)
                throw new RasterizationException("One of polygons is not triangle");

            int vertex1Index = polygon.getVertexIndices().get(0);
            int vertex2Index = polygon.getVertexIndices().get(1);
            int vertex3Index = polygon.getVertexIndices().get(2);
            float[] rawVertices = ro.getWorkVertices();

            textureIndices = polygon.getTextureVertexIndices();
            model = ro.getModel();
            // надо передавать текстурные вершины и смотреть, чтобы они были
            if (ro.isTextured()) {
                textureVertex1 = model.textureVertices.get(textureIndices.get(0));
                textureVertex2 = model.textureVertices.get(textureIndices.get(1));
                textureVertex3 = model.textureVertices.get(textureIndices.get(2));

                vertex1Attribute.u = textureVertex1.getX();
                vertex1Attribute.v = textureVertex1.getY();

                vertex2Attribute.u = textureVertex2.getX();
                vertex2Attribute.v = textureVertex2.getY();

                vertex3Attribute.u = textureVertex3.getX();
                vertex3Attribute.v = textureVertex3.getY();
            }

            refreshVertexAttributeForPolygon(vertex1Attribute, vertex1Index, rawVertices);

            refreshVertexAttributeForPolygon(vertex2Attribute, vertex2Index, rawVertices);

            refreshVertexAttributeForPolygon(vertex3Attribute, vertex3Index, rawVertices);

            Rasterization.drawTriangle(rb, zBuffer, ro.getColorProvider(), ro.getTexture(),
                    vertex1Attribute, vertex2Attribute, vertex3Attribute
            );
        }
    }

    private void refreshVertexAttributeForPolygon(VertexAttribute vertex1Attribute, int vertex1Index, float[] rawVertices) {
        vertex1Attribute.x = Math.round(rawVertices[vertex1Index * 3]);
        vertex1Attribute.y = Math.round(rawVertices[vertex1Index * 3 + 1]);
        vertex1Attribute.z = Math.round(rawVertices[vertex1Index * 3 + 2]);
    }

    @Override
    public void prepare() {
        zBuffer.clear();
        rb.clear();
    }
}
