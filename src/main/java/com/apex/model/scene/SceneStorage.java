package com.apex.model.scene;

import com.apex.core.Constants;
import com.apex.exception.SceneStorageException;
import com.apex.io.util.IOProcessor;
import com.apex.model.color.DefaultColorProvider;
import com.apex.model.geometry.Model;
import com.apex.model.texture.ImageTexture;
import com.apex.model.texture.SolidTexture;
import com.apex.model.texture.Texture;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import javafx.scene.image.Image;

import java.util.*;

@AutoCreation
public class SceneStorage {
    @AutoInject(name = "ReadIOProcessor")
    private IOProcessor inputProcessor;
    @AutoInject(name = "WriteIOProcessor")
    private IOProcessor writeProcessor;

    private int id = 1;
    private final Map<Integer, RenderObject> renderObjectsMap = new HashMap<>();
    private final List<Camera> cameras = new ArrayList<>();

    // caches
    private HashMap<Integer, Texture> textureCache = new HashMap<>();

    public void addModel(Model model) {
        try {
            inputProcessor.process(model);
        } catch (Exception e) {
            // todo здесь Лехе надо придумать, как бы обработать ошибку и сказать пользователю, что с моделью что то не то
        }
        Texture texture;
        if (textureCache.containsKey(Constants.color)) {
            texture = textureCache.get(Constants.color);
        } else {
            texture = new SolidTexture(Constants.color);
            cacheTexture(Constants.color, texture);
        }
        RenderObject ro = new RenderObject(model, new DefaultColorProvider(), texture);
        renderObjectsMap.put(id++, ro);
    }

    public void cacheTexture(int hash, Texture texture) {
        textureCache.put(hash, texture);
    }

    public void addTexture(Image image, int id) {
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        Texture texture;
        if (textureCache.containsKey(image.hashCode())) {
            texture = textureCache.get(image.hashCode());
        } else {
            texture = new ImageTexture(image);
            cacheTexture(image.hashCode(), texture);
        }
        renderObjectsMap.get(id).setTexture(texture);
    }

    public void deleteTexture(int id) {
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        Texture texture;
        if (textureCache.containsKey(Constants.color)) {
            texture = textureCache.get(Constants.color);
        } else {
            texture = new SolidTexture(Constants.color);
            cacheTexture(Constants.color, texture);
        }
        renderObjectsMap.get(id).setTexture(texture);
    }

    public void deleteModel(int id) {
        if (!hasAnyModels()) throw new SceneStorageException("No models to delete");
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        renderObjectsMap.remove(id);
    }

    public Model getPreparedToSaveModel(int id) {
        if (!hasAnyModels()) throw new SceneStorageException("No models to save");
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        Model model = renderObjectsMap.get(id).getModel();
        writeProcessor.process(model);
        return model;
    }

    public Collection<RenderObject> getRenderObjects() {
        return renderObjectsMap.values();
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public boolean hasAnyModels() {
        return !renderObjectsMap.isEmpty();
    }

    /**
     * Внимание! Не забыть вызвать при изменении цвета заливки, чтобы убрать старый из кеша
     * @param cache - кеш
     */
    public void deleteFromCache(int cache) {
        textureCache.remove(cache);
    }

    /**
     * todo Вызывается тогда, когда пользователь захочет поменять цвет сплошной заливки
     */
    public void updateColors() {
    }
}
