package com.apex.storage;

import com.apex.cache.ModelCache;
import com.apex.cache.TextureCache;
import com.apex.core.Constants;
import com.apex.exception.SceneStorageException;
import com.apex.model.geometry.Polygon;
import com.apex.model.scene.RenderObject;
import com.apex.model.util.RenderObjectStatus;
import com.apex.tool.colorization.ColorProvider;
import com.apex.tool.colorization.DefaultColorProvider;
import com.apex.model.geometry.Model;
import com.apex.model.texture.ImageTexture;
import com.apex.model.texture.SolidTexture;
import com.apex.model.texture.Texture;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.tool.colorization.WireFrameColorProvider;
import javafx.scene.image.Image;

import java.util.*;

@AutoCreation
public class SceneStorage {
    private final Map<String, RenderObject> renderObjectsMap = new HashMap<>();
    private final List<RenderObject> visibleRenderObjects = new ArrayList<>();
    private ColorProvider cp = new DefaultColorProvider();

    @AutoInject(name = "ModelCache")
    private ModelCache modelCache;

    @AutoInject(name = "TextureCache")
    private TextureCache textureCache;

    public void addModel(String filename, Model model) {
        model = modelCache.smartCache(filename, model);

        Texture defaultTexture = textureCache.smartCache(String.valueOf(Constants.color),
                new SolidTexture(Constants.color));

        RenderObject ro = new RenderObject(filename, model, cp, defaultTexture);
        renderObjectsMap.put(filename, ro);
        visibleRenderObjects.add(ro);
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
        Texture defaultTexture = textureCache.smartCache(String.valueOf(Constants.color),
                new SolidTexture(Constants.color));
        RenderObject ro = renderObjectsMap.get(fileObjName);
        ro.setTexture(defaultTexture);
        ro.setTextured(false);
    }

    public void deleteModel(String fileObjName) {
        if (!hasAnyModels())
            throw new SceneStorageException("No models to delete");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);
        RenderObject ro = renderObjectsMap.get(fileObjName);
        renderObjectsMap.remove(ro.getFilename());
        textureCache.deleteFromCacheIfNotUsedElseDecreaseUsage(ro.getTexture().getCache());
        modelCache.deleteFromCacheIfNotUsedElseDecreaseUsage(ro.getFilename());
        visibleRenderObjects.remove(ro); // могу удалять тк ссылка одна и та же
    }

    public void enableWireframeForAll() {
        cp = new WireFrameColorProvider();
        for (RenderObject ro : renderObjectsMap.values()) {
            ro.setColorProvider(cp);
        }
    }

    public void disableWireframeAll() {
        cp = new DefaultColorProvider();
        for (RenderObject ro : renderObjectsMap.values()) {
            ro.setColorProvider(cp);
        }
    }

    public void enableLightingForAll() {
        for (RenderObject ro : renderObjectsMap.values()) {
            ro.getColorData().MIN_LIGHT_FACTOR = Constants.MIN_LIGHT_FACTOR;
        }
    }

    public void disableLightingForAll() {
        for (RenderObject ro : renderObjectsMap.values()) {
            ro.getColorData().MIN_LIGHT_FACTOR = 1;
        }
    }

    public Model getPreparedToSaveModel(String fileObjName) {
        if (!hasAnyModels())
            throw new SceneStorageException("No models to save");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);
        return renderObjectsMap.get(fileObjName).getModel();
    }

    public Collection<RenderObject> getRenderObjects() {
        return renderObjectsMap.values();
    }

    public RenderObject getRenderObject(String name) {
        return renderObjectsMap.get(name);
    }

    public boolean hasAnyModels() {
        return !renderObjectsMap.isEmpty();
    }

    public List<RenderObject> getActiveRenderObjects() {
        List<RenderObject> renderObjects = new ArrayList<>();
        for (RenderObject ro : renderObjectsMap.values()) {
            if (!Objects.equals(ro.getStatus(), RenderObjectStatus.ACTIVE)) continue;
            renderObjects.add(ro);
        }
        return renderObjects;
    }

    public void makeActive(String fileObjName) {
        if (!hasAnyModels())
            throw new SceneStorageException("No models to update status");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);

        RenderObject ro = renderObjectsMap.get(fileObjName);
        ro.setStatus(RenderObjectStatus.ACTIVE);
    }

    public void makeUnactive(String fileObjName) {
        if (!hasAnyModels())
            throw new SceneStorageException("No models to update status");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);

        RenderObject ro = renderObjectsMap.get(fileObjName);
        ro.setStatus(RenderObjectStatus.UNACTIVE);
    }

    public void makeVisible(String fileObjName) {
        if (!hasAnyModels())
            throw new SceneStorageException("No models to make visible");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);

        RenderObject ro = renderObjectsMap.get(fileObjName);
        if (ro.isVisible()) return;
        ro.setVisibility(true);
        visibleRenderObjects.add(ro);
    }

    public void makeUnVisible(String fileObjName) {
        if (!hasAnyModels())
            throw new SceneStorageException("No models to make visible");
        if (!renderObjectsMap.containsKey(fileObjName))
            throw new SceneStorageException("No render object with name=" + fileObjName);

        RenderObject ro = renderObjectsMap.get(fileObjName);
        if (!ro.isVisible()) return;
        ro.setVisibility(false);
        visibleRenderObjects.remove(ro);
    }

    public List<RenderObject> getVisibleRenderObjects() {
        return visibleRenderObjects; // мог бы тут делать как в getActiveRenderObjects, но этот метод вызывается чаще и он вызывается при отрисовке, так что оптимизирую
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
     * todo Вызывается тогда, когда пользователь захочет поменять цвет сплошной
     * заливки
     */
    public void updateColors() {
    }
}
