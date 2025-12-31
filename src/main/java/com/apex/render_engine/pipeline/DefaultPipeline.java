package com.apex.render_engine.pipeline;

import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.model.geometry.Model;
import com.apex.render_engine.pipeline.element.PipelineElement;

import java.util.List;

@AutoCreation
public class DefaultPipeline implements Pipeline {
    @AutoInject
    private PipelineConfigurer configurer;
    private int currentStep = 0;

    @Override
    public void applyAll(RenderObject ro) {
        reset();
        while (hasNext()) {
            applyNext(ro);
        }
    }

    @Override
    public void applyNext(RenderObject ro) {
        List<PipelineElement> elements = configurer.getElements();
        if (currentStep < elements.size()) {
            elements.get(currentStep).apply(ro);
            currentStep++;
        }
    }

    public void prepare() {
        for (PipelineElement element : configurer.getElements()) element.prepare();
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
