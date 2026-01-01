package com.apex.model.scene;

import com.apex.cache.ModelCache;
import com.apex.cache.TextureCache;
import com.apex.core.Constants;
import com.apex.exception.SceneStorageException;
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
    private final Map<String, RenderObject> renderObjectsMap = new HashMap<>();
    private final List<Camera> cameras = new ArrayList<>();
    private final ColorProvider cp = new DefaultColorProvider();


    @AutoInject(name = "ModelCache")
    private ModelCache modelCache;

    @AutoInject(name = "TextureCache")
    private TextureCache textureCache;

    public void addModel(String filename, Model model) {
        model = modelCache.smartCache(filename, model);

        Texture defaultTexture = textureCache.smartCache(String.valueOf(Constants.color), new SolidTexture(Constants.color));

        RenderObject ro = new RenderObject(filename, model, cp, defaultTexture);
        renderObjectsMap.put(filename, ro);
    }

    public void addTexture(String fileObjName, String fileTextureName, Image image) {
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);
        Texture oldTexture = renderObjectsMap.get(fileObjName).getTexture();
        textureCache.deleteFromCacheIfNotUsedElseDecreaseUsage(oldTexture.getCache());
        Texture texture = textureCache.smartCache(fileTextureName, new ImageTexture(fileTextureName, image));
        RenderObject ro = renderObjectsMap.get(fileObjName);
        if (ro.getModel().textureVertices.isEmpty())
            throw new SceneStorageException("Model has no texture vertices. name=" + fileObjName);
        checkTextureVertices(fileObjName);
        ro.setTexture(texture);
        ro.setTextured(true);
    }

    public void deleteTexture(String fileObjName) {
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);
        Texture oldTexture = renderObjectsMap.get(fileObjName).getTexture();
        textureCache.deleteFromCacheIfNotUsedElseDecreaseUsage(oldTexture.getCache());
        Texture defaultTexture = textureCache.smartCache(String.valueOf(Constants.color), new SolidTexture(Constants.color));
        RenderObject ro = renderObjectsMap.get(fileObjName);
        ro.setTexture(defaultTexture);
        ro.setTextured(false);
    }

    public void deleteModel(String fileObjName) {
        if (!hasAnyModels()) throw new SceneStorageException("No models to delete");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);
        RenderObject ro = renderObjectsMap.get(fileObjName);
        renderObjectsMap.remove(ro.getFilename());
        textureCache.deleteFromCacheIfNotUsedElseDecreaseUsage(ro.getTexture().getCache());
        modelCache.deleteFromCacheIfNotUsedElseDecreaseUsage(ro.getFilename());
    }

    public Model getPreparedToSaveModel(String fileObjName) {
        if (!hasAnyModels()) throw new SceneStorageException("No models to save");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);
        return renderObjectsMap.get(fileObjName).getModel();
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

    private void checkTextureVertices(String filename) {
        if (!renderObjectsMap.containsKey(filename))
            throw new SceneStorageException("No render object with name=" + filename);
        Model model = renderObjectsMap.get(filename).getModel();
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
     * todo Вызывается тогда, когда пользователь захочет поменять цвет сплошной заливки
     */
    public void updateColors() {
    }
}
