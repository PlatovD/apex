package com.apex.storage;

import com.apex.core.Constants;
import com.apex.exception.LightStorageException;
import com.apex.math.Vector3f;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.util.ActiveLightWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@AutoCreation
public class LightStorage {
    private final Map<String, Vector3f> lights = new HashMap<>();
    private String activeLight = Constants.DEFAULT_LIGHT_NAME;

    @AutoInject
    private ActiveLightWrapper activeLightWrapper;

    public LightStorage() {
        addLight(Constants.DEFAULT_LIGHT_NAME, Constants.DEFAULT_LIGHT);
    }

    public void addLight(String name, Vector3f light) {
        if (Objects.isNull(light))
            throw new LightStorageException("Light can't be null");
        if (Objects.isNull(name))
            throw new LightStorageException("Light name can't be null");
        if (lights.containsKey(name))
            throw new LightStorageException("Duplicated light name. Unable to create a light. Change the name");

        lights.put(name, light.copy());
    }

    public void updateLightColor(String name, Vector3f newColor) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);
        if (Objects.isNull(newColor))
            throw new LightStorageException("Light color can't be null");

        lights.put(name, newColor.copy());

        if (name.equals(activeLight)) {
            activeLightWrapper.setActiveLight(newColor.copy());
        }
    }

    public void deleteLight(String name) {
        if (lights.isEmpty())
            return;
        if (Objects.equals(name, Constants.DEFAULT_LIGHT_NAME))
            return;
        if (name.equals(activeLight))
            setActiveLight(Constants.DEFAULT_LIGHT_NAME);
        lights.remove(name);
    }

    public boolean hasLight(String name) {
        return lights.containsKey(name);
    }

    public List<String> getLightsNames() {
        return List.copyOf(lights.keySet());
    }

    public void setActiveLight(String name) {
        if (!lights.containsKey(name))
            return;
        activeLight = name;
        activeLightWrapper.setActiveLight(lights.get(name).copy());
    }

    public Vector3f getActiveLight() {
        if (!lights.containsKey(activeLight))
            setActiveLight(Constants.DEFAULT_LIGHT_NAME);
        return activeLightWrapper.getActiveLight();
    }

    public String getActiveLightName() {
        return activeLight;
    }

    public Vector3f getLight(String name) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);
        return lights.get(name).copy();
    }

    public void setLightColor(String name, float r, float g, float b) {
        updateLightColor(name, new Vector3f(r, g, b));
    }
}