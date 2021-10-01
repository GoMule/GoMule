package gomule.gui;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationRunningCheckerTest {

    @Test
    public void running() throws IOException {
        AtomicBoolean called = new AtomicBoolean(false);
        new ApplicationRunningChecker(mockRuntime(), "my-app", () -> called.set(true)).run();
        assertTrue(called.get());
    }

    @Test
    public void notRunning() throws IOException {
        AtomicBoolean called = new AtomicBoolean(false);
        new ApplicationRunningChecker(mockRuntime(), "not-my-app", () -> called.set(true)).run();
        assertFalse(called.get());
    }

    private Runtime mockRuntime() throws IOException {
        Runtime mockRuntime = Mockito.mock(Runtime.class);
        Process mockProcess = Mockito.mock(Process.class);
        Mockito.when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream("some-header\nmy-app.exe    is    running".getBytes(StandardCharsets.UTF_8)));
        Mockito.when(mockRuntime.exec(Mockito.any(String[].class))).thenReturn(mockProcess);
        return mockRuntime;
    }

}