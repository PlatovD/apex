package com.apex;

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

public class ApexApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/gui.fxml"));
        // Loader читает fxml и видит "com.apex.GuiController". После этого он передает в этот метод именно этот класс
        loader.setControllerFactory(ReflectionScanner::findAssignableBeanByClass);
        AnchorPane viewport = loader.load();
        Scene scene = new Scene(viewport);
        stage.setWidth(Constants.SCENE_WIDTH);
        stage.setHeight(Constants.SCENE_HEIGHT);
        viewport.prefWidthProperty().bind(scene.widthProperty());
        viewport.prefHeightProperty().bind(scene.heightProperty());
        stage.setTitle("Apex");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        // регистрация хардово заданных руками бинов, которые не можем собрать из-за того,
        // что большой конструктор и много параметров
        ContextRegister.register();
        // Авто создание бинов, тех, что можем
        ReflectionScanner.autocreationResolver();
        // поиск полей с аннотацией @AutoInject и внедрение зависимостей + вызов функций у которых @AutoInject для аргумента
        try {
            ReflectionScanner.wireContext();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        launch();
    }
}