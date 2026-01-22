package com.apex.render.pipeline.element;

import com.apex.buffer.RasterizationBuffer;
import com.apex.model.geometry.Model;
import com.apex.model.geometry.Polygon;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.tool.rasterization.Rasterizator;
import com.apex.tool.rasterization.VertexAttribute;

import java.util.Set;

@AutoCreation
public class PolygonHighlightPipelineElement implements PipelineElement {

    @AutoInject
    private RasterizationBuffer rb;

    @AutoInject
    private Rasterizator rasterizator;

    private static final int HIGHLIGHT_COLOR = 0xFFFFFF00;

    @Override
    public void apply(RenderObject ro) {
        Set<Integer> selectedIndices = ro.getSelectedPolygonIndices();
        if (selectedIndices == null || selectedIndices.isEmpty()) {
            return;
        }

        Model model = ro.getModel();
        float[] rawVertices = ro.getWorkVertices();

        VertexAttribute v0 = new VertexAttribute();
        VertexAttribute v1 = new VertexAttribute();
        VertexAttribute v2 = new VertexAttribute();

        for (Integer index : selectedIndices) {
            if (index < 0 || index >= model.polygons.size()) {
                continue;
            }

            Polygon polygon = model.polygons.get(index);
            if (polygon.getVertexIndices().size() != 3)
                continue;

            int i0 = polygon.getVertexIndices().get(0);
            int i1 = polygon.getVertexIndices().get(1);
            int i2 = polygon.getVertexIndices().get(2);

            refreshVertex(v0, i0, rawVertices);
            refreshVertex(v1, i1, rawVertices);
            refreshVertex(v2, i2, rawVertices);

            // Draw outlines of the triangle
            rasterizator.drawWireFrameTriangle2D(rb, v0, v1, v2, HIGHLIGHT_COLOR);
        }
    }

    private void refreshVertex(VertexAttribute v, int index, float[] raw) {
        v.x = Math.round(raw[index * 4]);
        v.y = Math.round(raw[index * 4 + 1]);
        v.z = raw[index * 4 + 2];
    }

    @Override
    public void prepare() {
    }
}
