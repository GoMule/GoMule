package gomule.updater;

import gomule.updater.util.UpdaterUIUtils.ProgressCallback;

import javax.swing.*;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static gomule.updater.GoMuleUpdater.setUpdatesDisabled;

public class UpdaterUI {

    private JButton cancelButton;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    public enum UpdateResult {
        COMPLETED,
        SKIPPED,
        FAILED,
        CANCELLED
    }

    public UpdateResult runUpdateFlow(Path currentDir, UpdateChecker.UpdateInfo updateInfo) {
        int choice = showUpdatePrompt(updateInfo);
        if (choice == JOptionPane.CLOSED_OPTION || choice == JOptionPane.NO_OPTION || choice == 2) {
            if (choice == 2) {
                setUpdatesDisabled();
                System.out.println("Updates disabled by user");
            }
            return UpdateResult.SKIPPED;
        }
        try {
            return runUpdateWithUI(currentDir, updateInfo);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null || msg.isEmpty()) {
                msg = "Unknown error";
            }
            JOptionPane.showMessageDialog(null,
                    "Update failed: " + msg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return UpdateResult.FAILED;
        }
    }

    private int showUpdatePrompt(UpdateChecker.UpdateInfo updateInfo) {
        Object[] options = {"Yes", "No", "Disable Updates"};
        return JOptionPane.showOptionDialog(null,
                "A new version (" + updateInfo.getVersion() + ") is available.\n\n" +
                        "Release Notes:\n" + updateInfo.getReleaseNotes() + "\n\n" +
                        "Would you like to download and install it now?",
                "Update Available",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private UpdateResult runUpdateWithUI(Path currentDir, UpdateChecker.UpdateInfo updateInfo) throws Exception {
        JFrame parent = createParentFrame();
        JLabel statusLabel = new JLabel("Preparing...");
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        JDialog dialog = createProgressDialog(parent, statusLabel, progressBar);

        SwingWorker<UpdateResult, String> worker = new SwingWorker<UpdateResult, String>() {
            @Override
            protected UpdateResult doInBackground() throws Exception {
                progressBar.setIndeterminate(false);
                ProgressCallback progressCallback = this::publish;
                Path extractDir = UpdateDownloader.downloadAndExtract(updateInfo, cancelled, progressCallback);
                if (cancelled.get()) return UpdateResult.CANCELLED;
                UpdateApplier.applyUpdate(extractDir, currentDir, progressCallback, cancelled);
                if (cancelled.get()) {
                    return UpdateResult.CANCELLED;
                } else {
                    return UpdateResult.COMPLETED;
                }
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                if (!chunks.isEmpty()) {
                    String last = chunks.get(chunks.size() - 1);
                    statusLabel.setText(last);
                    if (last.contains("%")) {
                        String pct = last.replaceAll("\\D+", "");
                        try {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(Integer.parseInt(pct));
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        progressBar.setIndeterminate(true);
                    }
                }
            }

            @Override
            protected void done() {
                dialog.dispose();
            }
        };
        worker.execute();
        dialog.setVisible(true);
        try {
            return worker.get();
        } finally {
            dialog.dispose();
            parent.dispose();
        }
    }

    private JFrame createParentFrame() {
        JFrame parent = new JFrame();
        parent.setAlwaysOnTop(true);
        parent.setVisible(false);
        return parent;
    }

    private JDialog createProgressDialog(JFrame parent, JLabel statusLabel, JProgressBar progressBar) {
        JDialog dialog = new JDialog(parent, "Updating GoMule", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancelled.set(true);
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statusLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        progressBar.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(10));
        cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> {
            cancelled.set(true);
            cancelButton.setEnabled(false);
            statusLabel.setText("Cancelling...");
        });
        panel.add(cancelButton);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }
}
