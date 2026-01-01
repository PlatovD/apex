package com.apex.render_engine.pipeline;

import com.apex.model.scene.RenderObject;

public interface Pipeline {
    void applyAll(RenderObject renderObject);

    void applyNext(RenderObject renderObject);

    boolean hasNext();

    PipelineConfigurer configure();

    void prepare();
}
