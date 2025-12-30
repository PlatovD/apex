package com.apex.model.scene;

import com.apex.exception.SceneStorageException;
import com.apex.io.util.IOProcessor;
import com.apex.model.geometry.Model;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;

import java.util.ArrayList;
import java.util.List;

@AutoCreation
public class SceneStorage {
    @AutoInject(name = "ReadIOProcessor")
    private IOProcessor inputProcessor;
    @AutoInject(name = "WriteIOProcessor")
    private IOProcessor writeProcessor;

    private final List<Model> models = new ArrayList<>();
    private final List<Camera> cameras = new ArrayList<>();

    public void addModel(Model model) {
        try {
            inputProcessor.process(model);
        } catch (Exception e) {
            // todo здесь Лехе надо придумать, как бы обработать ошибку и сказать пользователю, что с моделью что то не то
        }
        models.add(model);
    }

    public void deleteModel() {
        if (!hasAnyModels()) throw new SceneStorageException("No models to delete");
        // todo когда будет нормальная обертка над моделями в теории можно удалять их по названию пришедшему с фронта например
    }

    public Model getPreparedToSaveModel() {
        if (!hasAnyModels()) throw new SceneStorageException("No models to save");
        // todo тоже надо будет как то понимать, какую из моделей хочет сохранить пользователь
        Model model = models.get(0);
        writeProcessor.process(model);
        return model;
    }

    public List<Model> getModels() {
        // todo метод который бы отдавал модели на рисование. То есть оборачивал во что то и отдавал
        return models;
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public boolean hasAnyModels() {
        return !models.isEmpty();
    }
}
