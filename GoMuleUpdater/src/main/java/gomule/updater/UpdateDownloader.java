package gomule.updater;

import gomule.updater.util.ChecksumUtil;
import gomule.updater.util.UpdaterUIUtils.ProgressCallback;
import gomule.updater.util.ZipUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static gomule.updater.util.UpdaterUIUtils.emitProgress;
import static gomule.updater.util.UpdaterUIUtils.isCancelled;

public class UpdateDownloader {

    @FunctionalInterface
    public interface ConnectionFactory {
        HttpURLConnection create(String url) throws IOException;
    }

    public static Path downloadAndExtract(UpdateChecker.UpdateInfo updateInfo, AtomicBoolean cancelled, ProgressCallback callback) throws IOException {
        return downloadAndExtract(updateInfo, cancelled, callback, url -> (HttpURLConnection) new URL(url).openConnection());
    }

    public static Path downloadAndExtract(UpdateChecker.UpdateInfo updateInfo, AtomicBoolean cancelled, ProgressCallback callback, ConnectionFactory connectionFactory) throws IOException {
        Path tempDir = Files.createTempDirectory("gomule-update-");
        Path zipFile = tempDir.resolve("update.zip");
        emitProgress(callback, "Downloading...");
        downloadFile(updateInfo.getDownloadUrl(), zipFile, cancelled, callback, connectionFactory);
        if (isCancelled(cancelled)) {
            return null;
        }
        emitProgress(callback, "Verifying download...");
        if (!ChecksumUtil.verifySha256(zipFile, updateInfo.getChecksum())) {
            throw new IOException("Checksum verification failed");
        }
        emitProgress(callback, "Extracting...");
        Path extractDir = tempDir.resolve("extracted");
        ZipUtil.extractZip(zipFile, extractDir);
        return extractDir;
    }

    static void downloadFile(String urlString, Path destFile, AtomicBoolean cancelled, ProgressCallback callback, ConnectionFactory connectionFactory) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = connectionFactory.create(urlString);
            int fileSize = conn.getContentLength();
            try (InputStream in = conn.getInputStream();
                 java.io.FileOutputStream out = new java.io.FileOutputStream(destFile.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                int totalRead = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    if (isCancelled(cancelled)) {
                        return;
                    }
                    out.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    if (callback != null && fileSize > 0) {
                        int percent = (int) ((totalRead * 100.0) / fileSize);
                        callback.onProgress("Downloading: " + percent + "%");
                    }
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
