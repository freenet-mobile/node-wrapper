package org.freenetproject.mobile;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NodeControllerTest {
    @Test
    public void installToPath(@TempDir Path path) throws IOException {
        NodeController nc = new NodeController(path);
        Files.exists(Path.of(path.toString(), "freenet.ini"));
    }

    @Test
    public void resourceDefault(@TempDir Path path) throws IOException {
       NodeController nc = new NodeController(path);
       assertEquals(path.toString(), nc.getConfig("node.install.cfgDir"));
       assertEquals(path.toString(), nc.getConfig("node.install.cfgDir", ""));
    }

    @Test
    public void setConfig(@TempDir Path path) throws IOException {
        NodeController nc = new NodeController(path);
        nc.setConfig("node.install.cfgDir", "/path/to/config");
        assertEquals("/path/to/config", nc.getConfig("node.install.cfgDir"));
    }

    @Test
    public void setConfigFile(@TempDir Path path, @TempDir File src) throws IOException {
        NodeController nc = new NodeController(path);
        assertEquals(path.toString(), nc.getConfig("node.install.cfgDir"));

        File seednodes = new File(src, "seednodes.fref");
        List<String> content = Arrays.asList("A", "B", "C");
        Files.write(seednodes.toPath(), content) ;

        nc.setConfig("seednodes.fref", seednodes);

        Path destPath = Path.of(path.toString(), "seednodes.fref");
        assertTrue(Files.exists(destPath));

        assertEquals(content, Files.readAllLines(destPath));
    }
}