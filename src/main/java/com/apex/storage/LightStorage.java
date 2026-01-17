package com.apex.storage;

import com.apex.core.Constants;
import com.apex.exception.LightStorageException;
import com.apex.math.Vector3f;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.util.ActiveLightWrapper;
import com.apex.tool.light.Light;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@AutoCreation
public class LightStorage {
    private final Map<String, Light> lights = new HashMap<>();
    private String activeLight = Constants.DEFAULT_LIGHT_NAME;

    @AutoInject
    private ActiveLightWrapper activeLightWrapper;

    public LightStorage() {
        addLight(Constants.DEFAULT_LIGHT_NAME, new Light(Constants.DEFAULT_LIGHT, 0xFFFFFFFF));
    }

    public void addLight(String name, Light light) {
        if (Objects.isNull(light))
            throw new LightStorageException("Light can't be null");
        if (Objects.isNull(name))
            throw new LightStorageException("Light name can't be null");
        if (lights.containsKey(name))
            throw new LightStorageException("Duplicated light name. Unable to create a light. Change the name");

        lights.put(name, new Light(light.getPosition(), light.getColor()));
    }

    public void updateLightColor(String name, int newColor) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);

        Light light = lights.get(name);
        Light updated = new Light(light.getPosition(), newColor);
        lights.put(name, updated);

        if (name.equals(activeLight)) {
            activeLightWrapper.setActiveLight(updated.getPosition().copy());
        }
    }

    public void updateLightPosition(String name, Vector3f newPosition) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);
        if (Objects.isNull(newPosition))
            throw new LightStorageException("Position can't be null");

        Light light = lights.get(name);
        Light updated = new Light(newPosition, light.getColor());
        lights.put(name, updated);

        if (name.equals(activeLight)) {
            activeLightWrapper.setActiveLight(newPosition.copy());
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
        activeLightWrapper.setActiveLight(lights.get(name).getPosition().copy());
    }

    public Vector3f getActiveLight() {
        if (!lights.containsKey(activeLight))
            setActiveLight(Constants.DEFAULT_LIGHT_NAME);
        return activeLightWrapper.getActiveLight();
    }

    public String getActiveLightName() {
        return activeLight;
    }

    public Light getLight(String name) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);
        Light light = lights.get(name);
        return new Light(light.getPosition(), light.getColor());
    }

}