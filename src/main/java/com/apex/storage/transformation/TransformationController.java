package com.apex.storage.transformation;

import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;

@AutoCreation
public class TransformationController {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private CameraStorage cameraStorage;


}
