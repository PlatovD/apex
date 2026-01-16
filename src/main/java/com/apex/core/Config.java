package com.apex.core;

import com.apex.buffer.JavaFXBasedRasterizationBuffer;
import com.apex.buffer.RasterizationBuffer;
import com.apex.model.scene.Camera;
import com.apex.model.scene.ZBuffer;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.render.pipeline.Pipeline;
import com.apex.render.pipeline.element.PipelineElement;

import com.apex.render.pipeline.element.PolygonHighlightPipelineElement;
import com.apex.render.rasterizator.Rasterizator;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;
import com.apex.tool.colorization.DefaultColorProvider;
import com.apex.tool.light.PointLightProvider;

@AutoCreation
public class Config {
    public void pipelineConfig(
            @AutoInject Pipeline pipeline,
            @AutoInject(name = "TransformPipelineElement") PipelineElement transformEl,
            @AutoInject(name = "RasterizationPipelineElement") PipelineElement rasterizationEl,
            @AutoInject(name = "VertexHighlightPipelineElement") PipelineElement vertexHighlightEl,
            @AutoInject(name = "PolygonHighlightPipelineElement") PolygonHighlightPipelineElement polygonHighlightEl,
            @AutoInject(name = "WireFramePipelineElement") PipelineElement wireframePipelineElement) {
        pipeline.configure()
                .addElement(transformEl)
                .addElement(rasterizationEl)
                .addElement(vertexHighlightEl)
                .addElement(polygonHighlightEl)
                .addDisabledElement(wireframePipelineElement);
    }

    public void initialCameraConfig(
            @AutoInject Camera camera,
            @AutoInject CameraStorage storage) {
        storage.addCamera(Constants.DEFAULT_CAMERA_NAME, camera);
    }

    public void initialRasterizationBufferConfig(@AutoInject JavaFXBasedRasterizationBuffer rasterizationBuffer) {
        rasterizationBuffer.initBuffer();
    }

    public void initZBufferConfig(@AutoInject ZBuffer zBuffer) {
        zBuffer.initZBuffer();
    }

    public void initialSceneStorageConfig(
            @AutoInject SceneStorage sceneStorage,
            @AutoInject RuntimeStates runtimeStates
    ) {
        sceneStorage.setCp(new DefaultColorProvider(runtimeStates));
        sceneStorage.setLp(new PointLightProvider());
    }
}
