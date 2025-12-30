package com.apex.core;

import com.apex.GuiController;
import com.apex.reflection.ReflectionScanner;
import com.apex.model.scene.Camera;

import javax.vecmath.Vector3f;

public class ContextRegister {
    public static void register() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100);
        ReflectionScanner.registerBean("", camera.getClass(), camera);

        GuiController controller = new GuiController();
        ReflectionScanner.registerBean("", controller.getClass(), controller);
    }
}
