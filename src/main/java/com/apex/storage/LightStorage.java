package com.apex.storage;

import com.apex.core.Constants;
import com.apex.exception.LightStorageException;
import com.apex.math.Vector3f;
import com.apex.reflection.AutoCreation;
import com.apex.model.scene.Light;

import java.util.*;

@AutoCreation
public class LightStorage {
    private final Map<String, Light> lights = new HashMap<>();

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

        lights.put(name, light);
    }

    public void updateLightColor(String name, int newColor) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);

        Light light = lights.get(name);
        light.setColor(newColor);
    }

    public void updateLightPosition(String name, Vector3f newPosition) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);
        if (Objects.isNull(newPosition))
            throw new LightStorageException("Position can't be null");

        Light light = lights.get(name);
        light.setPosition(newPosition);
    }

    public void deleteLight(String name) {
        if (lights.isEmpty())
            return;
        if (Objects.isNull(name) || name.isBlank())
            throw new LightStorageException("Name of deleting light can't be empty");
        if (lights.containsKey(name))
            throw new LightStorageException("No light with name " + name);
        lights.remove(name);
    }

    public boolean hasLight(String name) {
        return lights.containsKey(name);
    }

    public List<String> getLightsNames() {
        return List.copyOf(lights.keySet());
    }

    public Light getLight(String name) {
        if (!lights.containsKey(name))
            throw new LightStorageException("Light not found: " + name);
        Light light = lights.get(name);
        return new Light(light.getPosition(), light.getColor());
    }

    public Collection<Light> getLights() {
        return lights.values();
    }
}