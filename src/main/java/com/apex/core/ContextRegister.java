package com.apex.core;

import com.apex.gui.controller.BaseGuiController;
import com.apex.buffer.CustomIntArrayBasedRasterizationBuffer;
import com.apex.buffer.JavaFXBasedRasterizationBuffer;
import com.apex.buffer.RasterizationBuffer;
import com.apex.gui.controller.JavaFXRasterizationBufferGUIController;
import com.apex.exception.BadPropertiesFileException;
import com.apex.reflection.ReflectionScanner;
import com.apex.model.scene.Camera;
import com.apex.tool.triangulator.EarCuttingTriangulator;
import com.apex.tool.triangulator.SimpleTriangulator;
import com.apex.tool.triangulator.Triangulator;
import com.apex.util.PropertiesReader;

import com.apex.math.Vector3f;

public class ContextRegister {
    public static void register() {
        PropertiesReader.load();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                60.0F, (float) Constants.SCENE_WIDTH / Constants.SCENE_HEIGHT, 0.01F, 1000);
        ReflectionScanner.registerBean("", camera.getClass(), camera);

        registerController();
        registerTriangulator();
    }

    public static void registerTriangulator() {
        String val = PropertiesReader.getProperty(ConstantsPropertiesNames.TRIANGULATOR_TYPE, null);
        if (val == null)
            throw new BadPropertiesFileException("Undefined property: " + ConstantsPropertiesNames.TRIANGULATOR_TYPE);
        switch (val.strip()) {
            case "EarCuttingTriangulator": {
                Triangulator triangulator = new EarCuttingTriangulator();
                ReflectionScanner.registerBean("", Triangulator.class, triangulator);
                break;
            }
            case "SimpleTriangulator": {
                Triangulator triangulator = new SimpleTriangulator();
                ReflectionScanner.registerBean("", Triangulator.class, triangulator);
                break;
            }
            default: {
                Triangulator triangulator = new SimpleTriangulator();
                ReflectionScanner.registerBean("", Triangulator.class, triangulator);
            }
        }
    }

    public static void registerController() {
        String val = PropertiesReader.getProperty(ConstantsPropertiesNames.BUFFER_TYPE, null);
        if (val == null)
            throw new BadPropertiesFileException("Undefined property: " + ConstantsPropertiesNames.BUFFER_TYPE);
        switch (val.strip()) {
            case "JavaFXBasedRasterizationBuffer": {
                RasterizationBuffer buffer = new JavaFXBasedRasterizationBuffer();
                ReflectionScanner.registerBean("", buffer.getClass(), buffer);

                // todo: еще надо регистрировать контроллер
                JavaFXRasterizationBufferGUIController controller = new JavaFXRasterizationBufferGUIController();
                ReflectionScanner.registerBean("", controller.getClass(), controller);
                break;
            }
            case "CustomIntArrayBasedRasterizationBuffer": {
                RasterizationBuffer buffer = new CustomIntArrayBasedRasterizationBuffer();
                ReflectionScanner.registerBean("", buffer.getClass(), buffer);

                BaseGuiController controller = new BaseGuiController();
                ReflectionScanner.registerBean("", controller.getClass(), controller);
                break;
            }
            default: {
                RasterizationBuffer buffer = new CustomIntArrayBasedRasterizationBuffer();
                ReflectionScanner.registerBean("", buffer.getClass(), buffer);

                BaseGuiController controller = new BaseGuiController();
                ReflectionScanner.registerBean("", controller.getClass(), controller);
            }
        }
    }
}
