package gomule.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ApplicationRunningChecker implements Runnable {

    private final Runtime runtime;
    private final String applicationName;
    private final Runnable action;

    public ApplicationRunningChecker(Runtime runtime, String applicationName, Runnable action) {
        this.runtime = runtime;
        this.applicationName = applicationName;
        this.action = action;
        Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).build()).scheduleWithFixedDelay(this, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        try {
            Process proc = runtime.exec(new String[]{"cmd", "/c", "tasklist"});
            boolean isApplicationRunning = new BufferedReader(new InputStreamReader(proc.getInputStream(), Charsets.UTF_8))
                    .lines()
                    .anyMatch(s -> s.startsWith(applicationName));
            if (isApplicationRunning) action.run();
        } catch (IOException e) {
            System.err.println("ApplicationRunningChecker failed to run");
        }
    }
}