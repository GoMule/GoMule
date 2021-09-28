package gomule.gui;

import gomule.d2i.D2SharedStash;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.io.File;

public class D2ViewSharedStash extends JInternalFrame implements D2ItemContainer, D2ItemListListener {
    private final D2FileManager fileManager;
    private final String sharedStashFilename;
    private static final int BG_WIDTH = 569;
    private static final int BG_HEIGHT = 739;
    private D2SharedStash sharedStash;

    public D2ViewSharedStash(D2FileManager fileManager, String sharedStashFilename) {
        super(sharedStashFilename, false, true, false, true);
        this.fileManager = fileManager;
        this.sharedStashFilename = sharedStashFilename;
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                fileManager.saveAll();
                closeView();
            }
        });
        ToolTipManager.sharedInstance().setDismissDelay(40000);
        ToolTipManager.sharedInstance().setInitialDelay(300);
        JPanel panel = new SharedStashPanel(fileManager);
        setContentPane(panel);
        pack();
        setVisible(true);
        connect();
    }

    public void activateView() {
        toFront();
        requestFocusInWindow();
    }

    @Override
    public String getFileName() {
        return sharedStashFilename;
    }

    @Override
    public boolean isHC() {
        return false;
    }

    @Override
    public boolean isSC() {
        return false;
    }

    @Override
    public void closeView() {
        disconnect(null);
        fileManager.removeFromOpenWindows(this);
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public D2ItemList getItemLists() {
        return null;
    }

    @Override
    public void connect() {
        if (sharedStash != null) {
            return;
        }
        try {
            sharedStash = (D2SharedStash) fileManager.addItemList(sharedStashFilename, this);
            itemListChanged();
        } catch (Exception pEx) {
            disconnect(pEx);
            pEx.printStackTrace();
        }
    }

    @Override
    public void disconnect(Exception pEx) {
        if (sharedStash != null) {
            fileManager.removeItemList(sharedStashFilename, this);
        }
        sharedStash = null;
        itemListChanged();
    }

    @Override
    public void itemListChanged() {
        String lTitle;
        if (sharedStash == null) {
            lTitle = "Disconnected";
        } else {
            lTitle = sharedStashFilename.substring(sharedStashFilename.lastIndexOf(File.separator) + 1);
            lTitle += ((sharedStash.isModified()) ? "*" : "");
            if (sharedStash.isSC()) {
                lTitle += " (SC)";
            } else if (sharedStash.isHC()) {
                lTitle += " (HC)";
            }
        }
        setTitle(lTitle);
    }

    private static class SharedStashPanel extends JPanel {

        private final D2FileManager fileManager;
        private int selectedStash = 0;
        private Image background;

        public SharedStashPanel(D2FileManager fileManager) {
            this.fileManager = fileManager;
            setLayout(new BorderLayout());
            setSize(BG_WIDTH, BG_HEIGHT);
            Dimension lSize = new Dimension(BG_WIDTH, BG_HEIGHT);
            setPreferredSize(lSize);
            setVisible(true);
            build();
        }

        public void build() {
            Image lEmptyBackground = D2ImageCache.getImage("stash" + (selectedStash + 1) + ".jpg");
            background = fileManager.getGraphicsConfiguration().createCompatibleImage(BG_WIDTH, BG_HEIGHT, Transparency.BITMASK);
            Graphics2D lGraphics = (Graphics2D) background.getGraphics();
            lGraphics.drawImage(lEmptyBackground, 0, 0, this);
            repaint();
        }

        @Override
        public void paint(Graphics pGraphics) {
            super.paint(pGraphics);
            Graphics2D lGraphics = (Graphics2D) pGraphics;
            lGraphics.drawImage(background, 0, 0, this);
        }
    }
}
