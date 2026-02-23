package gomule.updater;

import gomule.updater.util.UpdaterUIUtils.ProgressCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UpdateDownloaderTest {

    @Test
    public void downloadAndExtract_cancelDuringDownload_returnsNull() throws Exception {
        UpdateChecker.UpdateInfo updateInfo = mock(UpdateChecker.UpdateInfo.class);
        when(updateInfo.getDownloadUrl()).thenReturn("http://example.com/update.zip");
        when(updateInfo.getChecksum()).thenReturn("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

        AtomicBoolean cancelled = new AtomicBoolean(false);

        ProgressCallback progressCallback = status -> {
        };

        UpdateDownloader.ConnectionFactory connectionFactory = url -> {
            cancelled.set(true);
            return createMockConnection();
        };

        Path result = UpdateDownloader.downloadAndExtract(updateInfo, cancelled, progressCallback, connectionFactory);

        assertNull(result);
    }

    @Test
    public void downloadFile_cancelDuringDownload_doesNotThrow(@TempDir Path tempDir) throws Exception {
        String content = "test content";

        AtomicBoolean cancelled = new AtomicBoolean(false);
        ProgressCallback callback = status -> {
        };

        UpdateDownloader.ConnectionFactory connectionFactory = url -> {
            cancelled.set(true);
            return createMockConnection(content, content.length());
        };

        Path destFile = tempDir.resolve("output.txt");

        UpdateDownloader.downloadFile("http://example.com/file.zip", destFile, cancelled, callback, connectionFactory);

        assertTrue(Files.exists(destFile));
        assertEquals(0, Files.size(destFile));
    }

    @Test
    public void downloadFile_progressCallbackCalled(@TempDir Path tempDir) throws Exception {
        String content = "test content";

        ProgressCallback callback = mock(ProgressCallback.class);
        UpdateDownloader.ConnectionFactory connectionFactory = url -> createMockConnection(content, content.length());

        Path destFile = tempDir.resolve("output.txt");

        UpdateDownloader.downloadFile("http://example.com/file.zip", destFile, null, callback, connectionFactory);

        verify(callback).onProgress(eq("Downloading: 100%"));
        assertTrue(Files.exists(destFile));
    }

    @Test
    public void downloadFile_noCancel_flagSetAfterDownload_completesNormally(@TempDir Path tempDir) throws Exception {
        String content = "test content";

        AtomicBoolean cancelled = new AtomicBoolean(false);
        ProgressCallback callback = status -> {
        };
        UpdateDownloader.ConnectionFactory connectionFactory = url -> createMockConnection(content, content.length());

        Path destFile = tempDir.resolve("output.txt");

        UpdateDownloader.downloadFile("http://example.com/file.zip", destFile, cancelled, callback, connectionFactory);

        assertFalse(cancelled.get());
        assertTrue(Files.exists(destFile));
    }

    private HttpURLConnection createMockConnection() throws IOException {
        return createMockConnection("test", "test".length());
    }

    private HttpURLConnection createMockConnection(String content, int totalSize) throws IOException {
        HttpURLConnection mock = mock(HttpURLConnection.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());

        when(mock.getContentLength()).thenReturn(totalSize);
        when(mock.getInputStream()).thenReturn(inputStream);

        return mock;
    }
}
