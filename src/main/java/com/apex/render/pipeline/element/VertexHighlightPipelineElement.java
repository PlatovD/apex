package com.apex.render.pipeline.element;

import com.apex.buffer.RasterizationBuffer;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;

import java.util.Set;

@AutoCreation
public class VertexHighlightPipelineElement implements PipelineElement {

    @AutoInject
    private RasterizationBuffer rb;

    private static final int HIGHLIGHT_COLOR = 0xFFFFFF00; // Yellow
    private static final int POINT_SIZE = 5;

    @Override
    public void apply(RenderObject ro) {
        Set<Integer> selectedIndices = ro.getSelectedVertexIndices();
        if (selectedIndices == null || selectedIndices.isEmpty()) {
            return;
        }

        float[] rawVertices = ro.getWorkVertices();
        for (Integer index : selectedIndices) {
            if (index < 0 || index * 4 >= rawVertices.length) {
                continue;
            }

            int x = Math.round(rawVertices[index * 4]);
            int y = Math.round(rawVertices[index * 4 + 1]);

            rb.drawPoint(x, y, POINT_SIZE, HIGHLIGHT_COLOR);
        }
    }

    @Override
    public void prepare() {
    }
}
