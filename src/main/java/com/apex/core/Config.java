package com.apex.core;

import com.apex.bunch.AutoCreation;
import com.apex.bunch.AutoInject;
import com.apex.render_engine.pipeline.RenderPipeline;

@AutoCreation
public class Config {
    public void pipeline(@AutoInject RenderPipeline pipeline) {
    }
}
