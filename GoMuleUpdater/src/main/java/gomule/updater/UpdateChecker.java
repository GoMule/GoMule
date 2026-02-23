package gomule.updater;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Checks for available updates by fetching version manifest from remote server
 */
public class UpdateChecker {
    private static final int TIMEOUT_MS = 5000;

    private final String manifestUrl;
    private final String currentVersion;

    public UpdateChecker(String manifestUrl, String currentVersion) {
        this.manifestUrl = manifestUrl;
        this.currentVersion = currentVersion;
    }

    /**
     * Check for available updates with timeout
     *
     * @return UpdateInfo if update available, null otherwise
     */
    public UpdateInfo checkForUpdate() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<UpdateInfo> future = executor.submit(this::fetchUpdateInfo);

        try {
            return future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.err.println("Update check timed out");
            future.cancel(true);
            return null;
        } catch (Exception e) {
            System.err.println("Update check failed: " + e.getMessage());
            return null;
        } finally {
            executor.shutdownNow();
        }
    }

    private UpdateInfo fetchUpdateInfo() throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(manifestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "GoMule/" + currentVersion);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Update check returned status: " + responseCode);
                return null;
            }

            JsonObject root = Json.parse(new InputStreamReader(conn.getInputStream())).asObject();
            String latestVersion = root.get("latestVersion").asString();
            if (isNewerVersion(latestVersion, currentVersion)) {
                return new UpdateInfo(
                        latestVersion,
                        root.get("downloadUrl").asString(),
                        root.get("checksum").asString(),
                        root.getString("releaseNotes", "")
                );
            }
            return null; // No update needed
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Compare version strings (e.g., "R5.1" vs "R5.2")
     *
     * @return true if remoteVersion is newer than currentVersion
     */
    boolean isNewerVersion(String remoteVersion, String currentVersion) {
        // Simple version comparison - handles format like "R5.1", "R5.2-BETA", etc.
        String remote = remoteVersion.replaceAll("[^0-9.]", "");
        String current = currentVersion.replaceAll("[^0-9.]", "");

        String[] remoteParts = remote.split("\\.");
        String[] currentParts = current.split("\\.");

        int maxLength = Math.max(remoteParts.length, currentParts.length);

        for (int i = 0; i < maxLength; i++) {
            int remotePart = i < remoteParts.length ? parseVersionPart(remoteParts[i]) : 0;
            int currentPart = i < currentParts.length ? parseVersionPart(currentParts[i]) : 0;

            if (remotePart > currentPart) {
                return true;
            } else if (remotePart < currentPart) {
                return false;
            }
        }

        return false; // Versions are equal
    }

    private int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Holds information about an available update
     */
    public static class UpdateInfo {
        private final String version;
        private final String downloadUrl;
        private final String checksum;
        private final String releaseNotes;

        public UpdateInfo(String version, String downloadUrl, String checksum, String releaseNotes) {
            this.version = version;
            this.downloadUrl = downloadUrl;
            this.checksum = checksum;
            this.releaseNotes = releaseNotes;
        }

        public String getVersion() {
            return version;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getChecksum() {
            return checksum;
        }

        public String getReleaseNotes() {
            return releaseNotes;
        }
    }
}
