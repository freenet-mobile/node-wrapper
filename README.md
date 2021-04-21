# Freenet Mobile wrapper

Freenet Mobile App's core component.

## Install

```
// build.gradle

dependencies {
    // Freenet dependencies
    implementation 'org.freenetproject.mobile:mobile-wrapper:0.2'
}
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
/**
 * Starts the node through NodeStarter unless it's already started.
 *
 * @param args Arguments to pass to the NodeStarter
 * @return -1 if the node is running or in transition
 *          -2 if the node is already running
 *          0 if the node could be started
 */
 try {
  ret = runner.start(new String[]{Installer.getInstance().getFreenetIniPath()});
  if (ret == 0) {
      status.postValue(Status.STARTED);
  } else if (ret == -1) {
      // Already running
      status.postValue(Status.STARTED);
  } else {
      status.postValue(Status.ERROR);
  }
  ```
