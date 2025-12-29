package com.apex.render_engine.pipeline;

import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.model.Model;
import com.apex.render_engine.pipeline.element.PipelineElement;

import java.util.List;

@AutoCreation
public class DefaultPipeline implements Pipeline {
    @AutoInject
    private PipelineConfigurer configurer;
    private int currentStep = 0;

    @Override
    public void applyAll(Model model) {
        reset();
        while (hasNext()) {
            applyNext(model);
        }
    }

    @Override
    public void applyNext(Model model) {
        List<PipelineElement> elements = configurer.getElements();
        if (currentStep < elements.size()) {
            elements.get(currentStep).apply(model);
            currentStep++;
        }
    }

    public void reset() {
        currentStep = 0;
    }

    @Override
    public boolean hasNext() {
        return currentStep < configurer.pipelineConfigElementsCount();
    }

    @Override
    public PipelineConfigurer configure() {
        return this.configurer;
    }
}
