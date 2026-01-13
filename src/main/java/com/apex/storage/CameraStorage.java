package com.apex.storage;

import com.apex.core.Constants;
import com.apex.exception.CameraStorageException;
import com.apex.model.scene.Camera;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.util.ActiveCameraWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@AutoCreation
public class CameraStorage {
    private final Map<String, Camera> cameras = new HashMap<>();
    private String activeCamera = Constants.DEFAULT_CAMERA_NAME;
    @AutoInject
    private ActiveCameraWrapper activeCameraWrapper;

    public void addCamera(String name, Camera camera) {
        if (Objects.isNull(camera)) throw new CameraStorageException("Camera can't be null");
        if (Objects.isNull(name)) throw new CameraStorageException("Camera name can't be null");
        if (cameras.containsKey(name))
            throw new CameraStorageException("Duplicated camera name. Unable to create a camera. Change the name");
        cameras.put(name, camera);
    }

    public void deleteCamera(String name) {
        if (cameras.isEmpty()) return;
        if (Objects.equals(name, Constants.DEFAULT_CAMERA_NAME)) return;
        if (name.equals(activeCamera))
            setActiveCamera(Constants.DEFAULT_CAMERA_NAME);
        cameras.remove(name);
    }

    public boolean hasCamera(String name) {
        return cameras.containsKey(name);
    }

    public List<String> getCamerasNames() {
        return List.copyOf(cameras.keySet());
    }

    public void setActiveCamera(String name) {
        if (!cameras.containsKey(name)) return;
        activeCamera = name;
        activeCameraWrapper.setActiveCamera(cameras.get(name));
    }

    public Camera getActiveCamera() {
        if (!cameras.containsKey(activeCamera))
            setActiveCamera(Constants.DEFAULT_CAMERA_NAME);
        return activeCameraWrapper.getActiveCamera();
    }
}
