package gomule.updater;

import gomule.updater.util.ZipUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ZipUtilTest {

    @Test
    public void extractZip_simpleZip(@TempDir Path tempDir) throws Exception {
        Path zipFile = tempDir.resolve("test.zip");
        Path extractDir = tempDir.resolve("extracted");

        createTestZip(zipFile, "file1.txt", "Hello", "subdir/file2.txt", "World");

        ZipUtil.extractZip(zipFile, extractDir);

        assertTrue(Files.exists(extractDir.resolve("file1.txt")));
        assertTrue(Files.exists(extractDir.resolve("subdir/file2.txt")));
        assertEquals("Hello", new String(Files.readAllBytes(extractDir.resolve("file1.txt"))));
        assertEquals("World", new String(Files.readAllBytes(extractDir.resolve("subdir/file2.txt"))));
    }

    @Test
    public void extractZip_emptyZip(@TempDir Path tempDir) throws Exception {
        Path zipFile = tempDir.resolve("empty.zip");
        Path extractDir = tempDir.resolve("extracted");

        createEmptyZip(zipFile);
        ZipUtil.extractZip(zipFile, extractDir);

        assertTrue(Files.exists(extractDir));
    }

    @Test
    public void extractZip_preventsZipSlip(@TempDir Path tempDir) throws Exception {
        Path zipFile = tempDir.resolve("malicious.zip");
        Path extractDir = tempDir.resolve("extracted");

        createMaliciousZip(zipFile);

        assertThrows(IOException.class, () -> ZipUtil.extractZip(zipFile, extractDir));
    }

    private void createTestZip(Path zipFile, String... entries) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            for (int i = 0; i < entries.length; i += 2) {
                ZipEntry entry = new ZipEntry(entries[i]);
                zos.putNextEntry(entry);
                zos.write(entries[i + 1].getBytes());
                zos.closeEntry();
            }
        }
    }

    private void createEmptyZip(Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
        }
    }

    private void createMaliciousZip(Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            ZipEntry entry = new ZipEntry("../outside.txt");
            zos.putNextEntry(entry);
            zos.write("malicious".getBytes());
            zos.closeEntry();
        }
    }
}
