package com.apex.render_engine.pipeline.element;

import com.apex.model.Model;

public interface PipelineElement {
    void apply(Model model);
}
