# Freenet Mobile wrapper

Freenet Mobile App's core component.

## Install

```
// build.gradle
repositories {
    maven { url 'https://jitpack.io' }
    maven { url 'https://mvn.freenetproject.org' }
}

dependencies {
    implementation 'com.github.freenet-mobile:node-wrapper:0.5'
}
```

Android:

```
// build.gradle
repositories {
    maven { url 'https://jitpack.io' }
    maven { url 'https://mvn.freenetproject.org' }
}

    // Freenet dependencies
    implementation ('com.github.freenet-mobile:node-wrapper:0.5') {
        exclude group: 'org.freenetproject', module: 'freenet-ext'
        exclude group: 'net.java.dev.jna', module: 'jna'
        exclude group: 'net.java.dev.jna', module: 'jna-platform'
    }
    implementation 'net.java.dev.jna:jna:4.5.2@aar'
    // End Freenet dependencies
```

## Usage

### Setup a node for the first time

```java
import org.freenetproject.mobile.Installer;

/**
 * Install the node with default configuration and seed nodes both from resources. Some configuration
 * properties are dynamically calculated at runtime on the first startup (mostly related to directory paths).
 *
 * @param path Path to installation location.
 * @param seeds Input stream of seeds file.
 * @param config Input stream of default configuration.
 * @param bookmarks Input stream of default bookmarks
 *
 * @throws FileNotFoundException
 */
Installer.getInstance().install(
        context.getDir("data", Context.MODE_PRIVATE).getAbsolutePath(),
        res.openRawResource(R.raw.seednodes),
        res.openRawResource(R.raw.freenet),
        res.openRawResource(R.raw.bookmarks),
        Locale
);
                
```

Notes:
- Seednodes configuration should point to a seednodes.fref file. It could be the fred default seednodes.
- Config is the freenet.ini configuration, with this config you control the node behaviour.
- Bookmarks: Default fproxy bookmarks.

### Starting a node

```java
import org.freenetproject.mobile.Runner;

/**
 * Starts the node through NodeStarter unless it's already started.
 *
 * @param args Arguments to pass to the NodeStarter
 * @return -1 if the node is running or in transition
 *          -2 if the node is already running
 *          0 if the node could be started
 */
 Runner runner = Runner.getInstance();
 String[] args = { Installer.getInstance().getFreenetIniPath() };
 int ret = runner.start(args);
  ```
