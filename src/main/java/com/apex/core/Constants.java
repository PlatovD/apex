package com.apex.core;

import com.apex.math.Vector3f;

public class Constants {
    // todo вынести динамически меняющиеся настройки в отдельный класс по типу
    // States
    public static float EPS = 1e-5F;
    public static String PACKAGE_FOR_SCAN = "com.apex";
    public static final float TRANSLATION = 1F;
    public static final float ROTATION_SPEED = 10F;
    public static int backgroundColor = 0xFF1C1C1C;
    public static String PATH_TO_PROPERTIES = "/application.properties";
    public static float MIN_LIGHT_FACTOR = 0.3f;
    public static String DEFAULT_CAMERA_NAME = "Default";
    public static int WIREFRAME_COLOR = 0xFFDFFF00;
    public static final Vector3f DEFAULT_UP = new Vector3f(0.0f, 0.1f, 0.0f);
    public static final float MIN_CAMERA_DIST = 3f;
    public static final float WIREFRAME_GAP = 0.1f;
}
