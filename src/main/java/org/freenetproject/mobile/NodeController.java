package org.freenetproject.mobile;

import java.io.*;

public interface NodeController {
    /**
     * Sends a ModifyConfiguration messages through FCP, if accepted it stores the given value under key.
     *
     * @param key Configuration name.
     * @param value Related value.
     * @throws IOException Fails to write config.
     */
    void setConfig(String key, String value) throws IOException;

    /**
     * Copy file into the node directory as "filename".
     *
     * @param filename File name to save as.
     * @param file Actual file to copy.
     * @throws IOException Fails to write config.
     */
    void setConfig(String filename, File file) throws IOException;

    /**
     * Returns the value of the configuration key, defaults to ""
     * if the key is not present.
     *
     * @param key Configuration name.
     * @return Related value.
     */
    String getConfig(String key);

    /**
     * Returns the value of the configuration key, defaults to defaultValue
     * if the key is not present.
     *
     * @param key Configuration name.
     * @param defaultValue Default value to use.
     * @return Related value.
     */
    String getConfig(String key, String defaultValue);

    void start();

    /**
     * @throws IOException Fails to persist configuration.
     */
    void shutdown() throws IOException;

    /**
     * @throws IOException Fails to communicate with FCP.
     */
    void pause() throws IOException;

    /**
     * @throws IOException Fails to communicate with FCP.
     */
    void resume() throws IOException;

    /**
     * @return Whether the node is up or not.
     */
    boolean isRunning();
}
