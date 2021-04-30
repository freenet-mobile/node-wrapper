package org.freenetproject.mobile;

import org.apache.commons.lang3.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Small abstraction over java.util.Properties class.
 */
class Config {
    private final String properties = "freenet.ini";
    private Properties config = new Properties();

    /**
     * Set a given value under key.
     *
     * @param key Key name.
     * @param val Value.
     */
    public void set(String key, String val) {
        config.setProperty(key, val);
    }

    /**
     * Gets the value for configuration key, defaults to ""
     * if the configuration key is not present.
     *
     * @param key Configuration key.
     * @return Value.
     */
    public String get(String key) {
        return config.getProperty(key, "");
    }

    /**
     * Gets the value for configuration key, defaults to defaultValue
     * if the configuration key is not present.
     *
     * @param key Configuration key.
     * @return Value.
     */
    public String get(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    /**
     * Loads node configuration or creates one under path with default values.
     *
     * @param path Path where the node is located.
     */
    public void loadOrDefault(Path path) throws IOException {
        Path config = Paths.get(path.toString(), properties);

        if (Files.exists(config)) {
            InputStream is = new FileInputStream(config.toFile());
            this.config = new Properties();
            this.config.load(is);
            return;
        }

        this.config = getDefaultConfig(path);
        persist();
    }

    /**
     * Stores the current configuration to filesystem.
     *
     * This method saves the configuration options without using the Properties#store method.
     * Thus avoiding the escaping of : (used for IPv6 for example) and other especial characters
     * that freenet configuration contains.
     */
    public void persist() throws IOException {
        File dest = Paths.get(this.config.getProperty("node.install.cfgDir"), properties).toFile();
        FileWriter writer = new FileWriter(dest);
        this.config.forEach((k, v) -> {
            try {
                writer.write(String.format("%s=%s%n", k, v));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.write(String.format("%s%n", "End"));
        writer.close();
    }

    /**
     * Gets the default configuration and update the paths before returning it.
     *
     * @param path Node path.
     * @return Configured node.
     */
    private Properties getDefaultConfig(Path path) throws IOException {
        InputStream defaultConfig = getClass()
            .getClassLoader()
            .getResourceAsStream(
                Paths.get("defaults", properties).toString()
            );

        Properties config = new Properties();
        config.load(defaultConfig);
        String dir = path.toString();
        config.setProperty("node.install.cfgDir",  dir);
        config.setProperty("node.install.userDir", dir);
        config.setProperty("node.install.nodeDir", dir);
        config.setProperty("node.install.runDir", dir);
        config.setProperty("node.install.storeDir", dir + "/pathstore");
        config.setProperty("node.install.tempDir", dir + "/temp");
        config.setProperty("node.install.pluginDir", dir + "/plugins");
        config.setProperty("node.install.pluginStoresDir", dir + "/plugin-path");
        config.setProperty("node.install.persistentTempDir", dir + "/persistent-temp");

        config.setProperty("node.masterKeyFile", dir + "/master.keys");
        config.setProperty("node.downloadsDir", dir + "/downloads");

        config.setProperty("ssl.sslKeyStorePass", RandomStringUtils.randomAlphanumeric(64));
        config.setProperty("ssl.sslKeyPass", RandomStringUtils.randomAlphanumeric(64));

        config.setProperty("logger.dirname", dir + "/logs");

        return config;
    }
}