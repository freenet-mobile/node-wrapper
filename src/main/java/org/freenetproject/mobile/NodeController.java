package org.freenetproject.mobile;

import java.io.*;
import java.nio.file.*;

/**
 * Controls fred node (create and modify configuration, start/stop/pause node).
 */
public class NodeController {
    private final Config config = new Config();

    /**
     * Loads node configuration or creates one from default
     * values under the given path.
     *
     * @param path Path to fred installation directory
     */
    public NodeController(Path path) throws IOException {
        config.loadOrDefault(path);
    }


    /**
     * Stores the given value under key.
     *
     * @param key Configuration name.
     * @param value Related value.
     */
    public void setConfig(String key, String value) {
        config.set(key, value);
    }

    /**
     * Copy file into the node directory as "filename"
     * @param filename File name to save as.
     * @param file Actual file to copy.
     */
    public void setConfig(String filename, File file) throws IOException {
        String nodeDir = config.get("node.install.cfgDir");
        Files.copy(file.toPath(), Path.of(nodeDir, filename));
    }

    /**
     * Returns the value of the configuration key, defaults to ""
     * if the key is not present.
     *
     * @param key Configuration name.
     * @return Related value.
     */
    public String getConfig(String key) {
        return getConfig(key, "");
    }

    /**
     * Returns the value of the configuration key, defaults to defaultValue
     * if the key is not present.
     *
     * @param key Configuration name.
     * @param defaultValue Default value to use.
     * @return Related value.
     */
    public String getConfig(String key, String defaultValue) {
        return config.get(key, defaultValue);
    }

    public void start() {
    }

    public void stop() {
    }

    public void pause() {
    }
}
