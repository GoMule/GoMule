package gomule.util;

import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Version {
    private static String cachedVersion;

    private Version() {
    }

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

    /**
     * Compare version strings (e.g., "R5.1" vs "R5.2")
     *
     * @return true if proposedVersion is newer than currentVersion
     */
    public static boolean isNewerVersion(String proposedVersion, String currentVersion) {
        // Simple version comparison - handles format like "R5.1", "R5.2-BETA", etc.
        String proposed = proposedVersion.replaceAll("[^0-9.]", "");
        String current = currentVersion.replaceAll("[^0-9.]", "");

        String[] proposedParts = proposed.split("\\.");
        String[] currentParts = current.split("\\.");

        int maxLength = Math.max(proposedParts.length, currentParts.length);

        for (int i = 0; i < maxLength; i++) {
            int proposedPart = i < proposedParts.length ? parseVersionPart(proposedParts[i]) : 0;
            int currentPart = i < currentParts.length ? parseVersionPart(currentParts[i]) : 0;

            if (proposedPart > currentPart) {
                return true;
            } else if (proposedPart < currentPart) {
                return false;
            }
        }
        return false; // Versions are equal
    }

    private static int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
