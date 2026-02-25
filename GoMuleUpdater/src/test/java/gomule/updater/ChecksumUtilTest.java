package gomule.updater;

import gomule.updater.util.ChecksumUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ChecksumUtilTest {

    @Test
    public void calculateSha256_emptyFile(@TempDir Path tempDir) throws Exception {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.write(emptyFile, "".getBytes());

        String checksum = ChecksumUtil.calculateSha256(emptyFile);
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", checksum);
    }

    @Test
    public void calculateSha256_knownContent(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "Hello, World!".getBytes());

        String checksum = ChecksumUtil.calculateSha256(file);
        assertEquals("dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f", checksum);
    }

    @Test
    public void verifySha256_correctChecksum(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "Hello, World!".getBytes());

        String knownChecksum = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f";
        assertTrue(ChecksumUtil.verifySha256(file, knownChecksum));
    }

    @Test
    public void verifySha256_incorrectChecksum(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "Hello, World!".getBytes());

        assertFalse(ChecksumUtil.verifySha256(file, "wrongchecksum"));
    }

    @Test
    public void verifySha256_withPrefix(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "Hello, World!".getBytes());

        String checksumWithPrefix = "sha256_dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f";
        assertTrue(ChecksumUtil.verifySha256(file, checksumWithPrefix));
    }

    @Test
    public void verifySha256_caseInsensitive(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "Hello, World!".getBytes());

        String uppercaseChecksum = "DFFD6021BB2BD5B0AF676290809EC3A53191DD81C7F70A4B28688A362182986F";
        assertTrue(ChecksumUtil.verifySha256(file, uppercaseChecksum));
    }

    @Test
    public void verifySha256_nonexistentFile() {
        assertFalse(ChecksumUtil.verifySha256(Paths.get("/nonexistent/file.txt"), "abc123"));
    }
}
