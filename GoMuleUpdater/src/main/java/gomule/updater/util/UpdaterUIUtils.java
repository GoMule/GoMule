package gomule.updater.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class UpdaterUIUtils {
    private UpdaterUIUtils() {
    }

    public static boolean isCancelled(AtomicBoolean cancelled) {
        return cancelled != null && cancelled.get();
    }

    public static void emitProgress(ProgressCallback callback, String status) {
        if (callback != null) callback.onProgress(status);
    }

    public interface ProgressCallback {
        void onProgress(String status);
    }
}
