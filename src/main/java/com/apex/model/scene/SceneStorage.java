package com.apex.model.scene;

import com.apex.core.Constants;
import com.apex.exception.SceneStorageException;
import com.apex.io.util.IOProcessor;
import com.apex.model.geometry.Polygon;
import com.apex.tool.colorization.ColorProvider;
import com.apex.tool.colorization.DefaultColorProvider;
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
    static class Usage<T> {
        int cntUsage;
        T cached;

        public Usage(int cntUsage, T cached) {
            this.cntUsage = cntUsage;
            this.cached = cached;
        }
    }

    @AutoInject(name = "ReadIOProcessor")
    private IOProcessor inputProcessor;
    @AutoInject(name = "WriteIOProcessor")
    private IOProcessor writeProcessor;

    private int id = 1;
    private final Map<Integer, RenderObject> renderObjectsMap = new HashMap<>();
    private final List<Camera> cameras = new ArrayList<>();
    private final ColorProvider cp = new DefaultColorProvider();

    // caches
    private HashMap<Integer, Usage<Texture>> textureCache = new HashMap<>();
    private HashMap<Integer, Usage<Model>> modelCache = new HashMap<>(); // todo

    public void addModel(Model model) {
        try {
            inputProcessor.process(model);
        } catch (Exception e) {
            // todo здесь Лехе надо придумать, как бы обработать ошибку и сказать пользователю, что с моделью что то не то
        }
        Texture texture;
        if (textureCache.containsKey(Constants.color)) {
            texture = textureCache.get(Constants.color).cached;
            textureCache.get(Constants.color).cntUsage++;
        } else {
            texture = new SolidTexture(Constants.color);
            cacheTexture(Constants.color, texture);
        }
        RenderObject ro = new RenderObject(model, cp, texture);
        renderObjectsMap.put(id++, ro);
    }

    public void cacheTexture(int hash, Texture texture) {
        Usage<Texture> usage = new Usage<>(1, texture);
        textureCache.put(hash, usage);
    }

    public void addTexture(Image image, int id) {
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        Texture oldTexture = renderObjectsMap.get(id).getTexture();
        deleteFromCache(oldTexture);
        Texture texture;
        if (textureCache.containsKey(image.hashCode())) {
            texture = textureCache.get(image.hashCode()).cached;
            textureCache.get(image.hashCode()).cntUsage++;
        } else {
            texture = new ImageTexture(image);
            cacheTexture(image.hashCode(), texture);
        }
        RenderObject ro = renderObjectsMap.get(id);
        if (ro.getModel().textureVertices.isEmpty())
            throw new SceneStorageException("Model has no texture vertices. id=" + id);
        checkTextureVertices(id);
        ro.setTexture(texture);
        ro.setTextured(true);
    }

    public void deleteTexture(int id) {
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        Texture oldTexture = renderObjectsMap.get(id).getTexture();
        deleteFromCache(oldTexture);
        Texture texture;
        if (textureCache.containsKey(Constants.color)) {
            texture = textureCache.get(Constants.color).cached;
        } else {
            texture = new SolidTexture(Constants.color);
            cacheTexture(Constants.color, texture);
        }
        RenderObject ro = renderObjectsMap.get(id);
        ro.setTexture(texture);
        ro.setTextured(false);
    }

    public void deleteModel(int id) {
        if (!hasAnyModels()) throw new SceneStorageException("No models to delete");
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        RenderObject ro = renderObjectsMap.get(id);
        deleteFromCache(ro.getTexture());
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

    private void checkTextureVertices(int id) {
        if (!renderObjectsMap.containsKey(id)) throw new SceneStorageException("No render object with id=" + id);
        Model model = renderObjectsMap.get(id).getModel();
        for (Polygon polygon : model.polygons) {
            List<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            if (textureVertexIndices.size() != 3)
                throw new SceneStorageException("Bad model texture vertices. Unable to add texture");
            for (Integer textureVertexIndex : textureVertexIndices) {
                if (textureVertexIndex < 0 || textureVertexIndex >= model.textureVertices.size())
                    throw new SceneStorageException("Bad model texture vertices. Wrong indices");
            }
        }
    }

    /**
     * Внимание! Не забыть вызвать при изменении цвета заливки, чтобы убрать старый из кеша. Причем это не подойдет для удаления картинки.
     * Мы теряем ее кеш и без картинки не можем по нему обратиться. Сначала надо обновить цвет, а только потом это вызвать, иначе не удалится, тк используется
     * @param texture - текстура
     */
    public void deleteFromCache(Texture texture) {
        if (!textureCache.containsKey(texture.getCache())) return;
        Usage<Texture> usage = textureCache.get(texture.getCache());
        usage.cntUsage--;
        if (usage.cntUsage != 0) return;
        textureCache.remove(texture.getCache());
    }

    /**
     * todo Вызывается тогда, когда пользователь захочет поменять цвет сплошной заливки
     */
    public void updateColors() {
    }
}
