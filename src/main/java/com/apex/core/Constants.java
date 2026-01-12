package com.apex.core;

public class Constants {
    // todo вынести динамически меняющиеся настройки в отдельный класс по типу States
    public static float EPS = 1e-5F;
    public static String PACKAGE_FOR_SCAN = "com.apex";
    public static int SCENE_WIDTH = 1280;
    public static int SCENE_HEIGHT = 720;
    public static final float TRANSLATION = 1F;
    public static int color = 0xFF0000;
    public static int backgroundColor = 0xFF1C1C1C;
    public static String PATH_TO_PROPERTIES = "/application.properties";
    public static float MIN_LIGHT_FACTOR = 0.2f;
    public static String DEFAULT_CAMERA_NAME = "Default";
    public static int WIREFRAME_COLOR = 0xFFDFFF00;
}
