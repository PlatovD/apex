package com.apex.render_engine.rasterizator;

import com.apex.core.Constants;
import com.apex.model.scene.FrameBuffer;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.util.PixelWriterWrapper;
import javafx.scene.image.PixelFormat;

@AutoCreation
public class Rasterizator {
    @AutoInject
    private FrameBuffer frameBuffer;

    @AutoInject
    private PixelWriterWrapper pixelWriterWrapper;

    public void rasterize() {
        int[] rawPixels = frameBuffer.getRawData();
        int width = Constants.SCENE_WIDTH;
        int height = Constants.SCENE_HEIGHT;


        pixelWriterWrapper.getPixelWriter().setPixels(
                0, 0,
                width, height,
                PixelFormat.getIntArgbInstance(),
                rawPixels,
                0,
                rawPixels.length / height
        );
    }

    public PixelWriterWrapper getPixelWriterWrapper() {
        return pixelWriterWrapper;
    }
}
