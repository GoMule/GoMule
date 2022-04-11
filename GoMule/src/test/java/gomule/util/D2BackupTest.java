package gomule.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class D2BackupTest {

    @Test
    void intToStringShouldAddLeadingZeros() {
        String actual = D2Backup.intToString(1, 10);
        assertEquals("0000000001", actual);
        assertEquals(10, actual.length());
    }

    @Test
    void intToStringShouldAddLeadingZerosLongValue() {
        String actual = D2Backup.intToString(1, 100);
        assertTrue(actual.contains("01"));
        assertEquals(100, actual.length());
    }

    @Test
    void intToStringShouldCutLeadingNumbers() {
        String actual = D2Backup.intToString(111111114, 4);
        assertEquals("1114", actual);
    }
}