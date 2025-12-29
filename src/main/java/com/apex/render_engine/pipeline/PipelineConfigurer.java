package com.apex.render_engine.pipeline;

import com.apex.reflection.AutoCreation;
import com.apex.exception.PipelineException;
import com.apex.render_engine.pipeline.element.PipelineElement;

import java.util.ArrayList;
import java.util.List;

@AutoCreation
public class PipelineConfigurer {
    private final List<PipelineElement> elements = new ArrayList<>();

    public PipelineConfigurer addElement(PipelineElement element) {
        elements.add(element);
        return this;
    }

    public List<PipelineElement> getElements() {
        return elements;
    }

    public PipelineElement getElement(int index) {
        if (index + 1 >= elements.size()) throw new PipelineException("Pipeline has no element with index " + index);
        return elements.get(index);
    }

    public int pipelineConfigElementsCount() {
        return elements.size();
    }
}
