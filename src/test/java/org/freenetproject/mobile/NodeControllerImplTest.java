package org.freenetproject.mobile;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.io.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NodeControllerImplTest {
    @Test
    public void installToPath(@TempDir Path path) throws IOException {
        NodeController nc = new NodeControllerImpl(path);
        Files.exists(Paths.get(path.toString(), "defaults/freenet.ini"));
    }

    @Test
    public void resourceDefault(@TempDir Path path) throws IOException {
       NodeControllerImpl nc = new NodeControllerImpl(path);
       assertEquals(path.toString(), nc.getConfig("node.install.cfgDir"));
       assertEquals(path.toString(), nc.getConfig("node.install.cfgDir", ""));
    }

    @Test
    public void setConfig(@TempDir Path path) throws IOException {
        Connector mockConnector = Mockito.mock(Connector.class);
        NodeControllerImpl nc = new NodeControllerImpl(path, new Config(), mockConnector);
        nc.setConfig("node.install.cfgDir", "/path/to/config");
        assertEquals("/path/to/config", nc.getConfig("node.install.cfgDir"));
    }

    @Test
    public void setConfigFile(@TempDir Path path, @TempDir File src) throws IOException {
        Connector mockConnector = Mockito.mock(Connector.class);
        NodeControllerImpl nc = new NodeControllerImpl(path, new Config(), mockConnector);
        assertEquals(path.toString(), nc.getConfig("node.install.cfgDir"));

        File seednodes = new File(src, "seednodes.fref");
        List<String> content = Arrays.asList("A", "B", "C");
        Files.write(seednodes.toPath(), content) ;

        nc.setConfig("seednodes.fref", seednodes);

        Path destPath = Paths.get(path.toString(), "seednodes.fref");
        assertTrue(Files.exists(destPath));

        assertEquals(content, Files.readAllLines(destPath));
    }

    @Test
    public void setConfigEscaping(@TempDir Path path) throws IOException {
        Config config = new Config();
        config.set("node.install.cfgDir", path.toString());
        config.set("node.install.testDir", "/path/to/test:/another/path/to/test");
        config.persist();

        List<String> expected = new ArrayList<>();
        expected.add("node.install.cfgDir=" + path);
        expected.add("node.install.testDir=/path/to/test:/another/path/to/test");
        expected.add("End");

        Path file = Paths.get(path.toString(), "freenet.ini");
        assertEquals(expected, Files.readAllLines(file));

        Config read = new Config();
        read.loadOrDefault(path);
        assertEquals(path.toString(), read.get("node.install.cfgDir"));
        assertEquals("/path/to/test:/another/path/to/test", read.get("node.install.testDir"));
    }

}