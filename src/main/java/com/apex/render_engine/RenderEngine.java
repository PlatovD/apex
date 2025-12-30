package com.apex.render_engine;

import com.apex.model.geometry.Model;
import com.apex.model.scene.SceneStorage;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.render_engine.pipeline.Pipeline;
import com.apex.render_engine.rasterizator.Rasterizator;
import javafx.scene.image.PixelWriter;

@AutoCreation
public class RenderEngine {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private Pipeline pipeline;

    @AutoInject
    private Rasterizator rasterizator;

    public void initialize(PixelWriter pixelWriter) {
        rasterizator.getPixelWriterWrapper().setPixelWriter(pixelWriter);
    }

    public void render() {
        preparePipeline();
        // todo: здесь надо прокидывать уже не модель, а обертку над ней. Обертка должна знать больше, чем сама модель и позволять нам ее рисовать
        for (Model model : sceneStorage.getModels())
            pipeline.applyAll(model);
        rasterizator.rasterize();
    }

    private void preparePipeline() {
        pipeline.prepare();
    }

    public Rasterizator getRasterizator() {
        return rasterizator;
    }
}