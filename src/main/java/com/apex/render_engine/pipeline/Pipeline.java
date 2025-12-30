package com.apex.render_engine.pipeline;

import com.apex.model.geometry.Model;

public interface Pipeline {
    void applyAll(Model model);

    void applyNext(Model model);

    boolean hasNext();

    PipelineConfigurer configure();

    void prepare();
}
