package com.apex.storage;

import com.apex.core.Constants;
import com.apex.exception.CameraStorageException;
import com.apex.model.scene.Camera;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CameraStorageTest {
    @Test
    void shouldThrowWhenAddingNull() {
        CameraStorage storage = new CameraStorage();
        assertThrows(CameraStorageException.class,
                () -> storage.addCamera(null, new Camera()));
        assertThrows(CameraStorageException.class,
                () -> storage.addCamera("test", null));
    }

    @Test
    void shouldFallbackToDefaultWhenActiveDeleted() {
        CameraStorage storage = new CameraStorage();
        Camera defaultCamera = new Camera();
        storage.addCamera(Constants.DEFAULT_CAMERA_NAME, defaultCamera);
        storage.addCamera("custom", new Camera());
        storage.setActiveCamera("custom");
        storage.deleteCamera("custom");
        assertEquals(defaultCamera, storage.getActiveCamera());
    }
}
