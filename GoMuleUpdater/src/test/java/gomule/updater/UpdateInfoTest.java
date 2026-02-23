package gomule.updater;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UpdateInfoTest {

    @Test
    public void constructorAndGetters() {
        UpdateChecker.UpdateInfo info = new UpdateChecker.UpdateInfo(
                "R5.2",
                "https://example.com/download.zip",
                "abc123",
                "Bug fixes"
        );

        assertEquals("R5.2", info.getVersion());
        assertEquals("https://example.com/download.zip", info.getDownloadUrl());
        assertEquals("abc123", info.getChecksum());
        assertEquals("Bug fixes", info.getReleaseNotes());
    }

    @Test
    public void releaseNotesCanBeEmpty() {
        UpdateChecker.UpdateInfo info = new UpdateChecker.UpdateInfo(
                "R5.2",
                "https://example.com/download.zip",
                "abc123",
                ""
        );
        assertEquals("", info.getReleaseNotes());
    }

    @Test
    public void releaseNotesCanBeNull() {
        UpdateChecker.UpdateInfo info = new UpdateChecker.UpdateInfo(
                "R5.2",
                "https://example.com/download.zip",
                "abc123",
                null
        );
        assertNull(info.getReleaseNotes());
    }
}
