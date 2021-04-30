# Freenet Node wrapper

Freenet Mobile App's core component.

## Install

```
// build.gradle
repositories {
    maven { url 'https://jitpack.io' }
    maven { url 'https://mvn.freenetproject.org' }
}

dependencies {
    implementation 'com.github.freenet-mobile:node-wrapper:1.0'
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
    implementation ('org.freenetproject.mobile:node-wrapper:1.0') {
        exclude group: 'org.freenetproject', module: 'freenet-ext'
        exclude group: 'net.java.dev.jna', module: 'jna'
        exclude group: 'net.java.dev.jna', module: 'jna-platform'
    }
    implementation 'net.java.dev.jna:jna:4.5.2@aar'
    // End Freenet dependencies
```

## Usage

```java
import org.freenetproject.mobile.NodeControllerImpl;

// This method will install a freenet node at "/path/to/install/dir"
// and setup it with default configurations. If a node configuration is found in
// the given directory the configration will be picked up.
nc = new NodeControllerImpl("/path/to/install/dir");

// The node will be started with the configuration at the given directory. 
nc.start();
```
