package com.apex.core;

import com.apex.model.scene.Camera;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.render.pipeline.Pipeline;
import com.apex.render.pipeline.element.PipelineElement;

import com.apex.render.pipeline.element.PolygonHighlightPipelineElement;
import com.apex.render.rasterizator.Rasterizator;
import com.apex.storage.CameraStorage;

@AutoCreation
public class Config {
    public void pipelineConfig(
            @AutoInject Pipeline pipeline,
            @AutoInject(name = "TransformPipelineElement") PipelineElement transformEl,
            @AutoInject(name = "RasterizationPipelineElement") PipelineElement rasterizationEl,
            @AutoInject(name = "VertexHighlightPipelineElement") PipelineElement vertexHighlightEl,
            @AutoInject(name = "PolygonHighlightPipelineElement") PolygonHighlightPipelineElement polygonHighlightEl) {
        pipeline.configure()
                .addElement(transformEl)
                .addElement(rasterizationEl)
                .addElement(vertexHighlightEl)
                .addElement(polygonHighlightEl);
    }

    public void initialCameraConfig(
            @AutoInject Camera camera,
            @AutoInject CameraStorage storage) {
        storage.addCamera(Constants.DEFAULT_CAMERA_NAME, camera);
    }

    public void initialRasterizationConfig(@AutoInject Rasterizator rasterizator) {
    }
}
