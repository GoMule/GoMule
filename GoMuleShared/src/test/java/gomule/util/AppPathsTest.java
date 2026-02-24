package gomule.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppPathsTest {
    @Test
    void getLocationForClass_handlesNullGracefully() {
        Path path = AppPaths.getLocationForClass(null);
        assertNull(path);
    }
    @Test
    void getLocationForClass_handlesNonJarClass() {
        Path path = AppPaths.getLocationForClass(String.class);
        assertNull(path);
    }
    @Test
    void getLocationForClass_returnsPathForJarClass() {
        Path path = AppPaths.getLocationForClass(AppPaths.class);
        assertNotNull(path);
        String pathStr = path.toString();
        assertTrue(pathStr.endsWith(".jar") || pathStr.contains("classes"),
                "Expected jar or classes dir, got: " + pathStr);
    }
}
