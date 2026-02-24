package gomule.updater;

import gomule.updater.util.UpdaterUIUtils.ProgressCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateApplierTest {

    @Test
    public void applyUpdate_fullFlow_copiesFiles(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);

        Path fileInUpdate = updateDir.resolve("file.txt");
        Files.write(fileInUpdate, "Hello World".getBytes());

        UpdateApplier.applyUpdate(updateDir, currentDir, null, null);

        assertTrue(Files.exists(currentDir.resolve("file.txt")));
        assertEquals("Hello World", new String(Files.readAllBytes(currentDir.resolve("file.txt"))));
    }

    @Test
    public void applyUpdate_fullFlow_createsSubdirectories(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);

        Path fileInUpdate = updateDir.resolve("subdir/file.txt");
        Files.createDirectories(fileInUpdate.getParent());
        Files.write(fileInUpdate, "nested".getBytes());

        UpdateApplier.applyUpdate(updateDir, currentDir, null, null);

        assertTrue(Files.exists(currentDir.resolve("subdir/file.txt")));
    }

    @Test
    public void applyUpdate_fullFlow_backupsWhitelistedDirectories(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);
        // Files in whitelisted directories that SHOULD be backed up
        Files.createDirectory(currentDir.resolve("app"));
        Files.write(currentDir.resolve("app/config.txt"), "app config".getBytes());
        Files.createDirectory(currentDir.resolve("d2111"));
        Files.write(currentDir.resolve("d2111/data.txt"), "d2111 data".getBytes());
        Files.createDirectory(currentDir.resolve("resources"));
        Files.write(currentDir.resolve("resources/res.txt"), "resources".getBytes());

        // Non-whitelisted files that should NOT be backed up
        Files.write(currentDir.resolve("random.txt"), "random".getBytes());
        Files.createDirectory(currentDir.resolve("randomdir"));
        Files.write(currentDir.resolve("randomdir/file.txt"), "randomdir".getBytes());

        Files.createDirectory(updateDir.resolve("app"));
        Files.write(updateDir.resolve("app/newfile.txt"), "new app".getBytes());
        UpdateApplier.applyUpdate(updateDir, currentDir, null, null);
        // Verify backup contains whitelisted files
        assertTrue(Files.exists(currentDir.resolve("app/newfile.txt")));
        assertEquals("new app", new String(Files.readAllBytes(currentDir.resolve("app/newfile.txt"))));

        // Non-whitelisted should not be gone (not backed up, not restored)
        assertTrue(Files.exists(currentDir.resolve("random.txt")));
        assertTrue(Files.exists(currentDir.resolve("randomdir")));
    }

    @Test
    public void applyUpdate_cancelRestoresWhitelistedDirectories(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);
        // Create files in whitelisted directories in currentDir (should be backed up)
        Files.createDirectory(currentDir.resolve("app"));
        Files.write(currentDir.resolve("app/config.txt"), "app config".getBytes());
        Files.createDirectory(currentDir.resolve("d2111"));
        Files.write(currentDir.resolve("d2111/data.txt"), "d2111 data".getBytes());
        // Create files in updateDir
        Files.createDirectories(updateDir.resolve("app"));
        Files.write(updateDir.resolve("app/newfile.txt"), "new app".getBytes());
        // Cancel after backup
        AtomicBoolean cancelled = new AtomicBoolean(false);
        ProgressCallback callback = status -> {
            if (status.contains("Backed up:") && !cancelled.get()) {
                cancelled.set(true);
            }
        };
        UpdateApplier.applyUpdate(updateDir, currentDir, callback, cancelled);
        // Verify whitelisted files are restored from backup
        assertTrue(Files.exists(currentDir.resolve("app/config.txt")));
        assertEquals("app config", new String(Files.readAllBytes(currentDir.resolve("app/config.txt"))));
        assertTrue(Files.exists(currentDir.resolve("d2111/data.txt")));
        assertEquals("d2111 data", new String(Files.readAllBytes(currentDir.resolve("d2111/data.txt"))));
        // New files from update should NOT exist (update was cancelled)
        assertFalse(Files.exists(currentDir.resolve("app/newfile.txt")));
    }

    @Test
    public void applyUpdate_fullFlow_preservesUpdaterAndProjects(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);

        Files.write(currentDir.resolve("GoMule.jar"), "updater".getBytes());
        Files.createDirectory(currentDir.resolve("projects"));
        Files.write(currentDir.resolve("projects/settings.txt"), "user data".getBytes());
        Files.write(currentDir.resolve("oldfile.txt"), "should be kept".getBytes());
        Files.createDirectory(currentDir.resolve("olddir"));
        Files.write(currentDir.resolve("olddir/file.txt"), "should be kept".getBytes());

        Files.write(updateDir.resolve("newfile.txt"), "new content".getBytes());

        UpdateApplier.applyUpdate(updateDir, currentDir, null, null);

        assertTrue(Files.exists(currentDir.resolve("GoMule.jar")));
        assertEquals("updater", new String(Files.readAllBytes(currentDir.resolve("GoMule.jar"))));
        assertTrue(Files.exists(currentDir.resolve("projects")));
        assertTrue(Files.exists(currentDir.resolve("projects/settings.txt")));
        assertEquals("user data", new String(Files.readAllBytes(currentDir.resolve("projects/settings.txt"))));
        assertTrue(Files.exists(currentDir.resolve("newfile.txt")));
        assertTrue(Files.exists(currentDir.resolve("oldfile.txt")));
        assertTrue(Files.exists(currentDir.resolve("olddir")));
    }

    @Test
    public void applyUpdate_fullFlow_stripsRootFolder(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(updateDir.resolve("GoMule"));
        Files.createDirectory(currentDir);

        Files.write(updateDir.resolve("GoMule/GoMuleApp.jar"), "app".getBytes());
        Files.write(updateDir.resolve("GoMule/config.txt"), "config".getBytes());

        UpdateApplier.applyUpdate(updateDir, currentDir, null, null);

        assertTrue(Files.exists(currentDir.resolve("GoMuleApp.jar")));
        assertEquals("app", new String(Files.readAllBytes(currentDir.resolve("GoMuleApp.jar"))));
        assertTrue(Files.exists(currentDir.resolve("config.txt")));
    }

    @Test
    public void applyUpdate_cancelDuringClean_restoresBackup(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);

        Files.write(currentDir.resolve("GoMule.jar"), "updater".getBytes());
        Files.createDirectory(currentDir.resolve("projects"));
        Files.write(currentDir.resolve("projects/settings.txt"), "user data".getBytes());
        Files.write(currentDir.resolve("oldfile.txt"), "original content".getBytes());

        Files.write(updateDir.resolve("newfile.txt"), "new content".getBytes());
        Files.createDirectory(currentDir.resolve("app"));
        Files.write(currentDir.resolve("app/settings.txt"), "app data".getBytes());

        AtomicBoolean cancelled = new AtomicBoolean(false);
        ProgressCallback callback = status -> {
            if (status.contains("Backed up:")) {
                cancelled.set(true);
            }
        };

        UpdateApplier.applyUpdate(updateDir, currentDir, callback, cancelled);
        assertTrue(Files.exists(currentDir.resolve("oldfile.txt")));
        assertEquals("original content", new String(Files.readAllBytes(currentDir.resolve("oldfile.txt"))));
        assertFalse(Files.exists(currentDir.resolve("newfile.txt")));
        assertTrue(Files.exists(currentDir.resolve("projects")));
        assertTrue(Files.exists(currentDir.resolve("app/settings.txt")));
        assertEquals("app data", new String(Files.readAllBytes(currentDir.resolve("app/settings.txt"))));
    }

    @Test
    public void applyUpdate_cancelDuringApply_restoresBackup(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);

        Files.write(currentDir.resolve("GoMule.jar"), "updater".getBytes());
        Files.createDirectory(currentDir.resolve("projects"));
        Files.write(currentDir.resolve("projects/settings.txt"), "user data".getBytes());
        Files.write(currentDir.resolve("oldfile.txt"), "original content".getBytes());

        Files.write(updateDir.resolve("newfile1.txt"), "new content 1".getBytes());
        Files.write(updateDir.resolve("newfile2.txt"), "new content 2".getBytes());

        AtomicBoolean cancelled = new AtomicBoolean(false);
        ProgressCallback callback = status -> {
            if (status.contains("Installing update") && !cancelled.get()) {
                cancelled.set(true);
            }
        };

        UpdateApplier.applyUpdate(updateDir, currentDir, callback, cancelled);
        assertTrue(Files.exists(currentDir.resolve("oldfile.txt")));
        assertEquals("original content", new String(Files.readAllBytes(currentDir.resolve("oldfile.txt"))));
        assertFalse(Files.exists(currentDir.resolve("newfile1.txt")));
        assertFalse(Files.exists(currentDir.resolve("newfile2.txt")));
        assertTrue(Files.exists(currentDir.resolve("projects")));
    }

    @Test
    public void applyUpdate_cancelAfterBackup_completesCleanAndApply(@TempDir Path tempDir) throws Exception {
        Path updateDir = tempDir.resolve("update");
        Path currentDir = tempDir.resolve("current");
        Files.createDirectory(updateDir);
        Files.createDirectory(currentDir);

        Files.write(currentDir.resolve("GoMule.jar"), "updater".getBytes());
        Files.createDirectory(currentDir.resolve("projects"));
        Files.write(currentDir.resolve("projects/settings.txt"), "user data".getBytes());
        Files.write(currentDir.resolve("file1.txt"), "original 1".getBytes());
        Files.write(currentDir.resolve("file2.txt"), "original 2".getBytes());

        Files.write(updateDir.resolve("newfile.txt"), "new content".getBytes());

        AtomicBoolean cancelled = new AtomicBoolean(false);
        ProgressCallback callback = status -> {
            if (status.contains("Backed up:") && !cancelled.get()) {
                cancelled.set(true);
            }
        };

        UpdateApplier.applyUpdate(updateDir, currentDir, callback, cancelled);
        assertTrue(Files.exists(currentDir.resolve("file1.txt")));
        assertEquals("original 1", new String(Files.readAllBytes(currentDir.resolve("file1.txt"))));
        assertTrue(Files.exists(currentDir.resolve("newfile.txt")));
    }
}
