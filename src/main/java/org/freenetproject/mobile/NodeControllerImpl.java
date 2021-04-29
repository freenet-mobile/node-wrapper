package org.freenetproject.mobile;

import freenet.node.*;
import org.bouncycastle.jce.provider.*;

import java.io.*;
import java.nio.file.*;
import java.security.*;

/**
 * Controls fred node (create and modify configuration, start/stop/pause node).
 */
public class NodeControllerImpl implements NodeController {
    private final Config config;
    private final Connector connector;

    /**
     * Loads node configuration or creates one from default
     * values under the given path.
     *
     * @param path Path to fred installation directory
     * @param config Configuration wrapper
     * @param connector FCP connection wrapper
     * @throws IOException Fails to open configuration.
     */
    public NodeControllerImpl(Path path, Config config, Connector connector) throws IOException {
        this.config = config;
        this.connector = connector;
        config.loadOrDefault(path);
    }

    /**
     * Loads node configuration or creates one from default
     * values under the given path.
     *
     * @param path Path to fred installation directory
     * @throws IOException Fails to open configuration.
     */
    public NodeControllerImpl(Path path) throws IOException {
        this(path, new Config(), new Connector());
    }

    /**
     * Sends a ModifyConfiguration messages through FCP, if accepted it stores the given value under key.
     *
     * @param key Configuration name.
     * @param value Related value.
     * @throws IOException Fails to set configuration.
     */
    public void setConfig(String key, String value) throws IOException {
        connector.modifyConfiguration(key, value);
        config.set(key, value);
    }

    /**
     * Copy file into the node directory as "filename".
     *
     * @param filename File name to save as.
     * @param file Actual file to copy.
     */
    public void setConfig(String filename, File file) throws IOException {
        String nodeDir = config.get("node.install.cfgDir");
        Files.copy(file.toPath(), Paths.get(nodeDir, filename));
    }

    /**
     * Copy file into the node directory as "filename".
     *
     * @param filename File name to save as.
     * @param file Actual file to copy.
     */
    public void setConfig(String filename, InputStream file) throws IOException {
        String nodeDir = config.get("node.install.cfgDir");
        byte[] buffer = new byte[file.available()];
        file.read(buffer);

        File dest = new File(Paths.get(nodeDir, filename).toString());
        OutputStream outStream = new FileOutputStream(dest);
        outStream.write(buffer);
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
        if (isRunning()) {
            return;
        }

        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        String[] args = {
            Paths.get(
                this.config.get("node.install.cfgDir"),
                "freenet.ini"
            ).toString()
        };

        NodeStarter.start_osgi(args);
    }

    public boolean isRunning() {
        try {
            connector.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void shutdown() {
        if (!isRunning()) {
            return;
        }
        NodeStarter.stop_osgi(0);
    }

    public void pause() throws IOException {
        setConfig("node.opennet.enabled", String.valueOf(false));
    }

    public void resume() throws IOException {
        setConfig("node.opennet.enabled", String.valueOf(true));
    }
}
