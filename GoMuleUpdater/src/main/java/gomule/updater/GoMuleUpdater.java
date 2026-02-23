package gomule.updater;

import gomule.util.Analytics;
import gomule.util.AppPaths;
import gomule.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static gomule.updater.GoMuleLauncher.launchGoMule;

public class GoMuleUpdater {
    private static final String MANIFEST_URL = "https://gomule.github.io/metadata/version.json";
    public static final String GOMULE_APP_JAR = "app/GoMuleApp.jar";
    private static final String UPDATES_DISABLED = "updates.disabled";

    public static void main(String[] args) {
        String currentVersion = Version.getCurrentVersion();
        Path appJarPath = Paths.get(GOMULE_APP_JAR).toAbsolutePath().normalize();
        if (Files.exists(appJarPath)) {
            String appVersion = Version.getVersionFromJar(appJarPath);
            if (appVersion != null) {
                currentVersion = appVersion;
            }
        }
        Analytics.trackLaunch(currentVersion);

        if (isUpdatesDisabled()) {
            System.out.println("Updates disabled, launching GoMule...");
            launchGoMule();
            return;
        }

        UpdateChecker updateChecker = new UpdateChecker(MANIFEST_URL, currentVersion);
        UpdateChecker.UpdateInfo updateInfo = updateChecker.checkForUpdate();

        if (updateInfo == null) {
            Analytics.trackUpdateCheck(currentVersion, false);
            System.out.println("No update available, launching GoMule...");
            launchGoMule();
            return;
        }

        Analytics.trackUpdateCheck(currentVersion, true);
        Path currentDir = Paths.get(".").toAbsolutePath().normalize();
        UpdaterUI updaterUI = new UpdaterUI();
        Analytics.trackUpdateStart(currentVersion, updateInfo.getVersion());
        UpdaterUI.UpdateResult result = updaterUI.runUpdateFlow(currentDir, updateInfo);
        try {
            if (result == UpdaterUI.UpdateResult.COMPLETED) {
                Analytics.trackUpdateSuccess(currentVersion, updateInfo.getVersion());
            } else if (result == UpdaterUI.UpdateResult.FAILED) {
                Analytics.trackUpdateFailed(currentVersion, updateInfo.getVersion(), "Update failed");
            } else if (result == UpdaterUI.UpdateResult.SKIPPED) {
                Analytics.trackUpdateSkipped(currentVersion, updateInfo.getVersion());
            }
        } catch (Exception e) {
            Analytics.trackUpdateFailed(currentVersion, updateInfo.getVersion(), "Update failed: " + e.getMessage());
        } finally {
            launchGoMule();
        }
    }

    private static final File PROPERTIES_FILE = new File(AppPaths.getBaseDir(), "projects/projects.properties");

    private static boolean isUpdatesDisabled() {
        try {
            if (PROPERTIES_FILE.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
                    props.load(fis);
                    return "true".equalsIgnoreCase(props.getProperty(UPDATES_DISABLED));
                }
            }
        } catch (Exception e) {
            System.err.println("Could not check updates.disabled: " + e.getMessage());
        }
        return false;
    }

    public static void setUpdatesDisabled() {
        try {
            Properties props = new Properties();
            if (PROPERTIES_FILE.exists()) {
                try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
                    props.load(fis);
                }
            }
            props.setProperty(UPDATES_DISABLED, String.valueOf(true));
            PROPERTIES_FILE.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(PROPERTIES_FILE)) {
                props.store(fos, null);
            }
        } catch (Exception e) {
            System.err.println("Could not save updates.disabled: " + e.getMessage());
        }
    }
}
