package gomule.updater;

import javax.swing.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static gomule.updater.GoMuleUpdater.GOMULE_APP_JAR;
import static gomule.util.AppPaths.getLocationForClass;

public class GoMuleLauncher {

    public static void launchGoMule() {
        try {
            Class<?> currentClass = new Object() {
            }.getClass().getEnclosingClass();
            if (currentClass == null) {
                currentClass = Class.forName(Thread.currentThread().getStackTrace()[1].getClassName());
            }

            Path parentDir;
            Path jarPath;
            Path launcherPath = getLocationForClass(currentClass);
            if (launcherPath != null) {
                parentDir = launcherPath.getParent();
                jarPath = parentDir.resolve(GOMULE_APP_JAR).toAbsolutePath().normalize();
            } else {
                parentDir = null;
                jarPath = null;
            }

            if (jarPath == null || !Files.exists(jarPath)) {
                System.out.println("Target JAR not found at: " + jarPath);
                try {
                    Class<?> goMuleClass = Class.forName("gomule.GoMule");
                    Method mainMethod = goMuleClass.getMethod("main", String[].class);
                    mainMethod.invoke(null, (Object) new String[0]);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            "Could not find " + GOMULE_APP_JAR + " at " + jarPath,
                            "Launch Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath.toString());
                pb.directory(parentDir.toFile());
                pb.redirectErrorStream(true);
                pb.start();
                System.exit(0);
            }

        } catch (Exception e) {
            String msg = "Failed to start GoMule.\n\nError: " + e;
            JOptionPane.showMessageDialog(null, msg, "GoMule Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
