package com.apex.render.pipeline;

import com.apex.reflection.AutoCreation;
import com.apex.exception.PipelineException;
import com.apex.render.pipeline.element.PipelineElement;

import java.util.ArrayList;
import java.util.List;

@AutoCreation
public class PipelineConfigurer {
    private final List<PipelineElement> elements = new ArrayList<>();
    private final List<PipelineElement> disabled = new ArrayList<>();

    public PipelineConfigurer addElement(PipelineElement element) {
        elements.add(element);
        return this;
    }

    public PipelineConfigurer addDisabledElement(PipelineElement pipelineElement) {
        disabled.add(pipelineElement);
        return this;
    }

    public List<PipelineElement> getElements() {
        return elements;
    }

    public PipelineElement getElement(int index) {
        if (index + 1 >= elements.size()) throw new PipelineException("Pipeline has no element with index " + index);
        return elements.get(index);
    }

    public void disableLast() {
        if (elements.isEmpty()) return;
        PipelineElement el = elements.remove(elements.size() - 1);
        disabled.add(el);
    }

    public void enableFirstReserved() {
        if (disabled.isEmpty()) return;
        PipelineElement el = disabled.remove(0);
        elements.add(el);
    }

    public int pipelineConfigElementsCount() {
        return elements.size();
    }
}
