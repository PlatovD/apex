package com.apex;

import com.apex.controller.BaseGuiController;
import com.apex.controller.JavaFXRasterizationBufferGUIController;
import com.apex.reflection.ReflectionScanner;
import com.apex.core.Constants;
import com.apex.core.ContextRegister;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class ApexApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/gui.fxml"));
        Properties properties = new Properties();
        try {
            properties.load(ApexApp.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String renderBufferType = properties.getProperty("render.buffer.type");
        Class<?> controllerClass;
        if ("JavaFXBasedRasterizationBuffer".equals(renderBufferType)) {
            controllerClass = JavaFXRasterizationBufferGUIController.class;
        } else {
            controllerClass = BaseGuiController.class;
        }

        Object controller = ReflectionScanner.findAssignableBeanByClass(controllerClass);
        loader.setController(controller);
        AnchorPane viewport = loader.load();

        viewport.setPrefSize(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

        Scene scene = new Scene(viewport, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // регистрация хардово заданных руками бинов, которые не можем собрать из-за
        // того,
        // что большой конструктор и много параметров
        ContextRegister.register();
        // Авто создание бинов, тех, что можем
        ReflectionScanner.autocreationResolver();
        // поиск полей с аннотацией @AutoInject и внедрение зависимостей + вызов функций
        // у которых @AutoInject для аргумента
        try {
            ReflectionScanner.wireContext();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        launch();
    }
}