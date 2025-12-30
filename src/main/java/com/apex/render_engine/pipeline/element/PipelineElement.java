package com.apex.render_engine.pipeline.element;

import com.apex.model.geometry.Model;

public interface PipelineElement {
    void apply(Model model);

    void prepare();
}
