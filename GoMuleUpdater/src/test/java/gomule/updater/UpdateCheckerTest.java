package gomule.updater;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateCheckerTest {

    private final UpdateChecker checker = new UpdateChecker("http://example.com/version.json", "R5.1");

    @Test
    public void isNewerVersion_majorVersionIncrease() {
        assertTrue(checker.isNewerVersion("R6.0", "R5.1"));
        assertTrue(checker.isNewerVersion("R10.0", "R5.1"));
    }

    @Test
    public void isNewerVersion_minorVersionIncrease() {
        assertTrue(checker.isNewerVersion("R5.2", "R5.1"));
        assertTrue(checker.isNewerVersion("R5.10", "R5.1"));
    }

    @Test
    public void isNewerVersion_sameVersion() {
        assertFalse(checker.isNewerVersion("R5.1", "R5.1"));
        assertFalse(checker.isNewerVersion("5.1", "5.1"));
    }

    @Test
    public void isNewerVersion_olderVersion() {
        assertFalse(checker.isNewerVersion("R5.0", "R5.1"));
        assertFalse(checker.isNewerVersion("R4.9", "R5.1"));
    }

    @Test
    public void isNewerVersion_differentFormats() {
        assertTrue(checker.isNewerVersion("R5.2-BETA", "R5.1"));
        assertTrue(checker.isNewerVersion("5.2", "R5.1"));
        assertTrue(checker.isNewerVersion("R5.1", "5.0"));
    }

    @Test
    public void isNewerVersion_singlePartVersion() {
        assertTrue(checker.isNewerVersion("R6", "R5.1"));
        assertFalse(checker.isNewerVersion("R5", "R5.1"));
    }

    @Test
    public void isNewerVersion_threePartVersion() {
        assertTrue(checker.isNewerVersion("R5.1.1", "R5.1.0"));
        assertTrue(checker.isNewerVersion("R5.2.0", "R5.1.9"));
        assertFalse(checker.isNewerVersion("R5.1.0", "R5.1.1"));
    }

    @Test
    public void isNewerVersion_emptyOrInvalid() {
        assertFalse(checker.isNewerVersion("", "R5.1"));
        assertTrue(checker.isNewerVersion("R5.1", ""));
        assertFalse(checker.isNewerVersion("abc", "R5.1"));
    }
}
