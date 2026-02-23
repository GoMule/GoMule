package gomule.updater.util;

import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;

public class ChecksumUtil {

    public static String calculateSha256(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = java.nio.file.Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean verifySha256(Path file, String expectedChecksum) {
        try {
            String calculated = calculateSha256(file);
            String expected = expectedChecksum.toLowerCase().replace("sha256-", "").replace("sha256:", "");
            return calculated.equals(expected);
        } catch (Exception e) {
            return false;
        }
    }
}
