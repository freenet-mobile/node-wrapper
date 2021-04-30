package org.freenetproject.mobile;

import net.pterodactylus.fcp.*;
import trikita.log.*;

import java.io.*;
import java.net.*;

class Connector {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 9481;
    private final FcpConnection fcpConnection;

    public Connector(FcpConnection fcpConnection) {
        this.fcpConnection = fcpConnection;
    }

    public Connector(String host, int port) throws UnknownHostException {
        this(new FcpConnection(host, port));
    }

    public Connector() throws UnknownHostException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public void connect() throws IOException {
        try {
            fcpConnection.connect();
            fcpConnection.sendMessage(new ClientHello("freenet-mobile"));
        } catch (Exception e) {
            Log.i("Freenet", "Failed to connect through FCP. Node shutdown or wrong port: " + e.getMessage());
            throw e;
        }
    }

    public void modifyConfiguration(String key, String value) throws IOException {
        ModifyConfig modifyConfig = new ModifyConfig("identifier");
        modifyConfig.setOption(key, value);
        fcpConnection.sendMessage(modifyConfig);
    }
}
