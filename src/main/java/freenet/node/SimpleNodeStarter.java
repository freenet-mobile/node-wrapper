package freenet.node;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;

import freenet.config.FreenetFilePersistentConfig;
import freenet.config.InvalidConfigValueException;
import freenet.config.SubConfig;
import freenet.crypt.SSL;
import freenet.support.PooledExecutor;

/**
 * This class is a striped out version of freenet.node.NodeStarter.
 * The aims of this class is to:
 *  - Give access to Node on SimpleNodeStarter#getNode() method.
 *  - Start the node with SimpleNodeStarter#start(String[])
 *  - Stop the node with SimpleNodeStarter#stop(int)
 *  - Pause and resume with SimpleNodeStarter#pause and SimpleNodeStarter#resume
 */
public class SimpleNodeStarter {
    private Node node;

    private static boolean isStarted;

    /**
     * The start method is called when the WrapperManager is signaled by the
     *	native wrapper code that it can start its application.  This
     *	method call is expected to return, so a new thread should be launched
     *	if necessary.
     *
     * @param config Path to configuration.
     *
     * @return Any error code if the application should exit on completion
     *         of the start method.  If there were no problems then this
     *         method should return null.
     */
    public int start(String config) {
        synchronized(SimpleNodeStarter.class) {
            if(isStarted) throw new IllegalStateException();
            isStarted = true;
        }
        File configFilename = new File(config);

        // set Java's DNS cache not to cache forever, since many people
        // use dyndns hostnames
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");
        java.security.Security.setProperty("networkaddress.cache.negative.ttl", "0");

        FreenetFilePersistentConfig cfg;
        try {
            System.out.println("Creating config from "+configFilename);
            cfg = FreenetFilePersistentConfig.constructFreenetFilePersistentConfig(configFilename);
        } catch(IOException e) {
            System.out.println("Error : " + e);
            e.printStackTrace();
            return -1;
        }

        // First, set up logging. It is global, and may be shared between several nodes.
        SubConfig loggingConfig = cfg.createSubConfig("logger");

        PooledExecutor executor = new PooledExecutor();

        LoggingConfigHandler logConfigHandler;
        try {
            System.out.println("Creating logger...");
            logConfigHandler = new LoggingConfigHandler(loggingConfig, executor);
        } catch(InvalidConfigValueException e) {
            System.err.println("Error: could not set up logging: " + e.getMessage());
            e.printStackTrace();
            return -2;
        }

        System.out.println("Starting executor...");
        executor.start();

        // Initialize SSL
        SubConfig sslConfig = cfg.createSubConfig("ssl");
        SSL.init(sslConfig);

        try {
            Field isStarted = NodeStarter.class.getDeclaredField("isStarted");
            isStarted.setAccessible(true);
            isStarted.setBoolean(NodeStarter.class, true);

            Field isTestingVM = NodeStarter.class.getDeclaredField("isTestingVM");
            isTestingVM.setAccessible(true);
            isTestingVM.setBoolean(NodeStarter.class, false);

            node = new Node(
                cfg,
                null,
                null,
                logConfigHandler,
                null, // Risky
                executor
            );

            node.start(false);


            System.out.println("Node initialization completed.");
        } catch(NodeInitException | NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to load node: " + e.getMessage());
        }

        return 0;
    }
    /**
     * Starts opennet if it's not running.
     */
    public void resume() {
        if (node.getOpennet() != null && isStarted) {
            node.getOpennet().start();
        }
    }

    /**
     * Stops opennet to simulate a shutdown.
     */
    public void pause() {
        if (!isStarted) {
            return;
        }

        node.getOpennet().stop(false);
    }

    /**
     * Called when the application is shutting down.
     * @param exitCode The suggested exit code that will be returned to the OS
     *                 when the JVM exits.
     *
     * @return The exit code to actually return to the OS.  In most cases, this
     *         should just be the value of exitCode, however the user code has
     *         the option of changing the exit code if there are any problems
     *         during shutdown.
     */
    public int stop(int exitCode) {
        isStarted = false;
        System.err.println("Shutting down with exit code " + exitCode);
        node.park();

        return exitCode;
    }

    /**
     * Returns the node started with this class.
     *
     * @return Node instance running.
     */
    public Node getNode() {
        return node;
    }

    /**
     * @return Whether the node has been starter.
     */
    public boolean hasStarted() {
        return isStarted;
    }
}
