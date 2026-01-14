package com.apex.storage.collision;

import com.apex.core.Constants;
import com.apex.math.Vector3f;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.storage.SceneStorage;

@AutoCreation
public class CollisionManager {
    @AutoInject
    private SceneStorage sceneStorage;

    public boolean checkCollisions(Vector3f newPosition) {
        for (RenderObject renderObject : sceneStorage.getVisibleRenderObjects()) {
            if (renderObject.getBoundingData().centerOfObject.subtract(newPosition).length() < renderObject.getBoundingData().boundingRadius + Constants.MIN_CAMERA_DIST)
                return true;
        }
        return false;
    }
}
