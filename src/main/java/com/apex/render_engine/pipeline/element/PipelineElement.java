package com.apex.render_engine.pipeline.element;

import com.apex.model.scene.RenderObject;

public interface PipelineElement {
    void apply(RenderObject renderObject);

    void prepare();
}
