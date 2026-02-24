package gomule.util;

import org.junit.jupiter.api.Test;

import static gomule.util.Version.isNewerVersion;
import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    public void isNewerVersion_majorVersionIncrease() {
        assertTrue(isNewerVersion("R6.0", "R5.1"));
        assertTrue(isNewerVersion("R10.0", "R5.1"));
    }

    @Test
    public void isNewerVersion_minorVersionIncrease() {
        assertTrue(isNewerVersion("R5.2", "R5.1"));
        assertTrue(isNewerVersion("R5.10", "R5.1"));
    }

    @Test
    public void isNewerVersion_sameVersion() {
        assertFalse(isNewerVersion("R5.1", "R5.1"));
        assertFalse(isNewerVersion("5.1", "5.1"));
    }

    @Test
    public void isNewerVersion_olderVersion() {
        assertFalse(isNewerVersion("R5.0", "R5.1"));
        assertFalse(isNewerVersion("R4.9", "R5.1"));
    }

    @Test
    public void isNewerVersion_differentFormats() {
        assertTrue(isNewerVersion("R5.2-BETA", "R5.1"));
        assertTrue(isNewerVersion("5.2", "R5.1"));
        assertTrue(isNewerVersion("R5.1", "5.0"));
    }

    @Test
    public void isNewerVersion_singlePartVersion() {
        assertTrue(isNewerVersion("R6", "R5.1"));
        assertFalse(isNewerVersion("R5", "R5.1"));
    }

    @Test
    public void isNewerVersion_threePartVersion() {
        assertTrue(isNewerVersion("R5.1.1", "R5.1.0"));
        assertTrue(isNewerVersion("R5.2.0", "R5.1.9"));
        assertFalse(isNewerVersion("R5.1.0", "R5.1.1"));
    }

    @Test
    public void isNewerVersion_emptyOrInvalid() {
        assertFalse(isNewerVersion("", "R5.1"));
        assertTrue(isNewerVersion("R5.1", ""));
        assertFalse(isNewerVersion("abc", "R5.1"));
    }
}