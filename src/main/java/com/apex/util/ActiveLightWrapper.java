package com.apex.util;

import com.apex.math.Vector3f;
import com.apex.reflection.AutoCreation;

@AutoCreation
public class ActiveLightWrapper {
    private Vector3f activeLight;

    public ActiveLightWrapper() {
        this.activeLight = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public Vector3f getActiveLight() {
        return activeLight.copy();
    }

    public void setActiveLight(Vector3f light) {
        this.activeLight = light.copy();
    }
}
