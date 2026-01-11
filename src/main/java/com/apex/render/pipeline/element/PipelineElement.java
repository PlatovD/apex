package com.apex.render.pipeline.element;

import com.apex.model.scene.RenderObject;

public interface PipelineElement {
    void apply(RenderObject renderObject);

    void prepare();
}
