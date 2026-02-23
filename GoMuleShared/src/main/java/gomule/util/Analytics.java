package gomule.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Analytics {
    private static final String BASE_URL = "https://silospen.com/gomuleevent/handler";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "analytics");
        t.setDaemon(true);
        return t;
    });

    public static void trackLaunch(String version) {
        track("launch", "version", version);
    }

    public static void trackUpdateCheck(String version, boolean hasUpdate) {
        track("update_check", "version", version, "has_update", String.valueOf(hasUpdate));
    }

    public static void trackUpdateStart(String from, String to) {
        track("update_start", "version", from, "new_version", to);
    }

    public static void trackUpdateSuccess(String from, String to) {
        track("update_success", "version", from, "new_version", to);
    }

    public static void trackUpdateFailed(String from, String to, String error) {
        track("update_failed", "version", from, "new_version", to, "error", error);
    }

    public static void trackUpdateSkipped(String from, String to) {
        track("update_skipped", "version", from, "new_version", to);
    }

    private static void track(String event, String... params) {
        executor.execute(() -> {
            try {
                String os = System.getProperty("os.name", "unknown");
                if (os.toLowerCase().startsWith("windows")) {
                    os = "windows";
                } else if (os.toLowerCase().startsWith("mac")) {
                    os = "mac";
                } else {
                    os = "linux";
                }
                String java = System.getProperty("java.version", "unknown");

                StringBuilder url = new StringBuilder(BASE_URL);
                url.append("?event=").append(urlEncode(event));
                url.append("&os=").append(urlEncode(os));
                url.append("&java=").append(urlEncode(java));

                for (int i = 0; i < params.length; i += 2) {
                    url.append("&").append(urlEncode(params[i]))
                            .append("=").append(urlEncode(params[i + 1]));
                }

                URL u = new URL(url.toString());
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.getResponseCode();
                conn.disconnect();
            } catch (Exception e) {
                System.err.println("Analytics tracking failed: " + e.getMessage());
            }
        });
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }
}
