package com.apex.util;

import com.apex.core.Constants;
import com.apex.exception.PropertiesReaderException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static Properties properties;

    public static void load() {
        try (InputStream input = PropertiesReader.class.getResourceAsStream(Constants.PATH_TO_PROPERTIES)) {
            Properties prop = new Properties();
            prop.load(input);
            properties = prop;
        } catch (IOException ex) {
            throw new PropertiesReaderException("Unable to read the properties file");
        }
    }

    public static String getProperty(String s, String def) {
        if (properties == null) throw new PropertiesReaderException("No properties. Are you forget to load it first?");
        return properties.getProperty(s, def);
    }
}
