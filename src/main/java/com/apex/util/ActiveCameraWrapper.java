package com.apex.util;

import com.apex.exception.NoActiveCameraException;
import com.apex.model.scene.Camera;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;

@AutoCreation
public class ActiveCameraWrapper {
    @AutoInject
    private Camera activeCamera;

    public Camera getActiveCamera() {
        if (activeCamera == null) throw new NoActiveCameraException("No active camera exists");
        return activeCamera;
    }

    public void setActiveCamera(Camera camera) {
        if (camera == null) return;
        activeCamera = camera;
    }
}
