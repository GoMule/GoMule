package gomule.util;

import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Version {
    private static String cachedVersion;

    public static String getCurrentVersion() {
        if (cachedVersion == null) {
            cachedVersion = loadVersion();
        }
        return cachedVersion;
    }

    private static String loadVersion() {
        // 1. Try system property (for IDE)
        String prop = System.getProperty("gomule.version");
        if (prop != null && !prop.isEmpty()) {
            return prop;
        }

        // 2. Try generated BuildConfig class (production)
        try {
            return gomule.BuildConfig.VERSION;
        } catch (Throwable e) {
            // Not available
        }

        // 3. Fallback to properties file
        try {
            java.io.InputStream is = Version.class.getResourceAsStream("/gomule.properties");
            if (is != null) {
                java.util.Properties props = new java.util.Properties();
                props.load(is);
                is.close();
                return props.getProperty("version", "R5.1");
            }
        } catch (Exception e) {
            System.err.println("Could not load version: " + e.getMessage());
        }

        return "R5.1";
    }

    public static String getVersionFromJar(Path jarPath) {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                String version = manifest.getMainAttributes().getValue("Implementation-Version");
                if (version != null && !version.isEmpty()) {
                    return version;
                }
            }
        } catch (Exception e) {
            // Fall through to return null
        }
        return null;
    }
}
