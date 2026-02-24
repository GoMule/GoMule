package gomule.util;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

public class AppPaths {
    private static String getAppHome() {
        return System.getProperty("app.home", ".");
    }
    
    public static File getBaseDir() {
        return new File(getAppHome());
    }

    public static Path getLocationForClass(Class<?> clazz) {
        try {
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            if (codeSource != null && codeSource.getLocation() != null) {
                URI uri = codeSource.getLocation().toURI();
                return Paths.get(uri);
            }
        } catch (Exception e) {
            System.err.println("Could not convert identification URL to URI: " + e.getMessage());
        }
        return null;
    }
}
