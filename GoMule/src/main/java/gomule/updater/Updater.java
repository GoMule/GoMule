package gomule.updater;

import gomule.GoMule;
import gomule.util.Version;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static gomule.util.Analytics.*;
import static gomule.util.AppPaths.getLocationForClass;
import static gomule.util.Version.isNewerVersion;

public class Updater {
    public static void checkAndSwapUpdater() {
        try {
            Path appJarPath = getLocationForClass(GoMule.class);
            if (appJarPath == null) return;

            Path parentDir = appJarPath.getParent();
            Path updaterJarNew = appJarPath.resolveSibling("GoMule.jar.new");
            Path updaterJar = parentDir.getParent().resolve("GoMule.jar");

            if (!Files.exists(updaterJarNew)) return;

            String currentVersion = Version.getVersionFromJar(updaterJar);
            String newVersion = Version.getVersionFromJar(updaterJarNew);
            if (currentVersion != null && newVersion != null && isNewerVersion(newVersion, currentVersion)) {
                trackLoaderUpdateCheck(currentVersion, true);
                System.out.println("Updater update detected: " + currentVersion + " -> " + newVersion);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        trackLoaderUpdateStart(currentVersion, newVersion);
                        Thread.sleep(500);
                        if (Files.exists(updaterJarNew)) {
                            Files.copy(updaterJarNew, updaterJar, StandardCopyOption.REPLACE_EXISTING);
                            Files.delete(updaterJarNew);
                            System.out.println("Updater updated to: " + newVersion);
                            trackLoaderUpdateSuccess(currentVersion, newVersion);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to update GoMule.jar: " + e.getMessage());
                        trackLoaderUpdateFailed(currentVersion, newVersion, e.getMessage());
                    }
                }));
            } else {
                Files.delete(updaterJarNew);
            }
        } catch (Exception e) {
            System.err.println("Error checking for updater update: " + e.getMessage());
        }
    }
}
