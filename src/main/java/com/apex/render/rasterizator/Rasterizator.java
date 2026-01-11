package com.apex.render.rasterizator;

import com.apex.buffer.RasterizationBuffer;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;

@AutoCreation
public class Rasterizator {
    @AutoInject
    private RasterizationBuffer rb;

    public void rasterize() {
        rb.rasterize();
    }
}
