package me.thfour.effortlogger;

import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {

    /***
     * Load a resource from a given url
     * @param path where the resource is
     * @return the path of the resource
     */
    public static URL loadURL(String path) {
        return ResourceLoader.class.getResource(path);
    }

    public static String load(String path) {
        return loadURL(path).toString();
    }

    public static InputStream loadStream(String path) {
        return ResourceLoader.class.getResourceAsStream(path);
    }
}
