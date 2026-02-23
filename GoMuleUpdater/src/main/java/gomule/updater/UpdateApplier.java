package gomule.updater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static gomule.updater.util.UpdaterUIUtils.*;

public class UpdateApplier {

    private static final String UPDATER_JAR = "GoMule.jar";
    private static final String PROJECTS_DIR = "projects";
    private static final String ZIP_ROOT_PREFIX = "GoMule";
    private static final String BACKUP_DIR = ".gomule_update_backup";

    public static void applyUpdate(Path updateDir, Path currentDir, ProgressCallback callback, AtomicBoolean cancelled) throws IOException {
        try {
            emitProgress(callback, "Creating backup...");
            backupCurrentDirectory(currentDir, callback);
            if (isCancelled(cancelled)) return;
            emitProgress(callback, "Cleaning old files...");
            cleanCurrentDirectory(currentDir, cancelled);
            if (isCancelled(cancelled)) return;
            emitProgress(callback, "Installing update...");
            applyFiles(updateDir, currentDir, callback, cancelled);
        } catch (Exception e) {
            try {
                restoreFromBackup(currentDir, callback);
            } catch (Exception rollbackEx) {
                throw new IOException("Update failed: " + e.getMessage() + ". Rollback also failed: " + rollbackEx.getMessage());
            }
            throw new IOException("Update failed: " + e.getMessage());
        } finally {
            if (isCancelled(cancelled)) {
                restoreFromBackup(currentDir, callback);
            }
            deleteBackup(currentDir);
        }
    }

    private static Predicate<Path> skipUpdater() {
        return path -> !path.getFileName().toString().equals(UPDATER_JAR);
    }

    private static void backupCurrentDirectory(Path currentDir, ProgressCallback callback) throws IOException {
        Path backupDir = currentDir.resolve(BACKUP_DIR);
        if (Files.exists(backupDir)) deleteDirectory(backupDir);
        Files.createDirectory(backupDir);
        copyDirectoryContents(currentDir, backupDir, null, callback, "Backed up: ", skipUpdater(), null);
    }

    private static void restoreFromBackup(Path currentDir, ProgressCallback callback) throws IOException {
        cleanCurrentDirectory(currentDir, null);
        emitProgress(callback, "Restoring backup...");
        Path backupDir = currentDir.resolve(BACKUP_DIR);
        System.out.println("Restoring from backup");
        if (!Files.exists(backupDir)) throw new IOException("No backup found to restore");
        copyDirectoryContents(backupDir, currentDir, null, callback, "Restored: ", skipUpdater(), null);
    }

    private static void deleteBackup(Path currentDir) {
        Path backupDir = currentDir.resolve(BACKUP_DIR);
        try {
            if (Files.exists(backupDir)) {
                deleteDirectory(backupDir);
                System.out.println("Deleted backup");
            }
        } catch (IOException e) {
            System.err.println("Failed to delete backup: " + e.getMessage());
        }
    }

    private static void cleanCurrentDirectory(Path currentDir, AtomicBoolean cancelled) throws IOException {
        try (Stream<Path> stream = Files.list(currentDir)) {
            stream.forEach(path -> {
                if (isCancelled(cancelled)) return;
                String fileName = path.getFileName().toString();
                if (fileName.equals(UPDATER_JAR) ||
                        fileName.equals(PROJECTS_DIR) ||
                        fileName.equals(BACKUP_DIR)) {
                    return;
                }
                try {
                    if (Files.isDirectory(path)) {
                        deleteDirectory(path);
                    } else {
                        Files.delete(path);
                    }
                    System.out.println("Deleted: " + fileName);
                } catch (IOException e) {
                    System.err.println("Failed to delete: " + path + " - " + e.getMessage());
                }
            });
        }
    }

    private static void applyFiles(Path updateDir, Path currentDir, ProgressCallback callback, AtomicBoolean cancelled) throws IOException {
        Path rootPrefix = updateDir.resolve(ZIP_ROOT_PREFIX);
        boolean hasPrefix = Files.exists(rootPrefix);

        copyDirectoryContents(updateDir, currentDir, hasPrefix ? rootPrefix : null, callback, "Updated: ", skipUpdater(), cancelled);
    }

    private static void copyDirectoryContents(Path sourceDir, Path destDir, Path prefixToStrip, ProgressCallback callback, String logPrefix, Predicate<Path> filter, AtomicBoolean cancelled) throws IOException {
        List<String> failedFiles = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(sourceDir)) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(filter)
                    .forEach(file -> {
                        if (isCancelled(cancelled)) return;
                        try {
                            Path relativePath = prefixToStrip != null
                                    ? prefixToStrip.relativize(file)
                                    : sourceDir.relativize(file);
                            Path targetPath = destDir.resolve(relativePath);

                            Files.createDirectories(targetPath.getParent());
                            Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);

                            String message = logPrefix + relativePath;
                            System.out.println(message);
                            if (callback != null) {
                                callback.onProgress(message);
                            }
                        } catch (IOException e) {
                            System.err.println("Failed to copy: " + file + " - " + e.getMessage());
                            failedFiles.add(file.toString());
                        }
                    });
        }

        if (!failedFiles.isEmpty()) {
            throw new IOException("Failed to copy: " + failedFiles);
        }
    }

    private static void deleteDirectory(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }
}
