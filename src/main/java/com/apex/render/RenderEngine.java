package com.apex.render;

import com.apex.model.scene.RenderObject;
import com.apex.storage.SceneStorage;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.render.pipeline.Pipeline;
import com.apex.render.rasterizator.Rasterizator;

@AutoCreation
public class RenderEngine {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private Pipeline pipeline;

    @AutoInject
    private Rasterizator rasterizator;

    public void render() {
        preparePipeline();
        // todo: здесь надо прокидывать уже не модель, а обертку над ней. Обертка должна знать больше, чем сама модель и позволять нам ее рисовать
        for (RenderObject ro : sceneStorage.getRenderObjects())
            pipeline.applyAll(ro);
        rasterizator.rasterize();
    }

    private void preparePipeline() {
        pipeline.prepare();
    }

    public Rasterizator getRasterizator() {
        return rasterizator;
    }
}