package com.apex.core;

import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.render_engine.pipeline.Pipeline;
import com.apex.render_engine.pipeline.element.PipelineElement;

@AutoCreation
public class Config {
    public void pipelineConfig(
            @AutoInject Pipeline pipeline,
            @AutoInject(name = "TransformPipelineElement") PipelineElement transformEl,
            @AutoInject(name = "RasterizationPipelineElement") PipelineElement rasterizationEl
    ) {
        pipeline.configure().addElement(transformEl).addElement(rasterizationEl);
    }
}
