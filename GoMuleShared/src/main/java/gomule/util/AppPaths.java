package gomule.util;

import java.io.File;

public class AppPaths {
    private static String getAppHome() {
        return System.getProperty("app.home", ".");
    }
    
    public static File getBaseDir() {
        return new File(getAppHome());
    }
}
