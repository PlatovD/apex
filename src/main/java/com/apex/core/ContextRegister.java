package com.apex.core;

import com.apex.controller.BaseGuiController;
import com.apex.buffer.CustomIntArrayBasedRasterizationBuffer;
import com.apex.buffer.JavaFXBasedRasterizationBuffer;
import com.apex.buffer.RasterizationBuffer;
import com.apex.controller.JavaFXRasterizationBufferGUIController;
import com.apex.exception.BadPropertiesFileException;
import com.apex.exception.ContextRegisterException;
import com.apex.reflection.ReflectionScanner;
import com.apex.model.scene.Camera;
import com.apex.util.PropertiesReader;

import javax.vecmath.Vector3f;

public class ContextRegister {
    public static void register() {
        PropertiesReader.load();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100);
        ReflectionScanner.registerBean("", camera.getClass(), camera);

        registerController();
    }

    private static void registerController() {
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
            default:
                throw new ContextRegisterException("Wrong property type: " + ConstantsPropertiesNames.BUFFER_TYPE);
        }
    }
}
