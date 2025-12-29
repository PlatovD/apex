package com.apex.render_engine.pipeline;

import com.apex.model.Model;

public interface RenderPipeline {
    void applyToModel(Model model);

    RenderPipelineConfigurer configure();
}
