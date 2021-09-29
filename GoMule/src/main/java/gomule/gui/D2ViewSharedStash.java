package gomule.gui;

import gomule.d2i.D2SharedStash;
import gomule.item.D2Item;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class D2ViewSharedStash extends JInternalFrame implements D2ItemContainer, D2ItemListListener {
    private final D2FileManager fileManager;
    private final String sharedStashFilename;
    private static final int BG_WIDTH = 362;
    private static final int BG_HEIGHT = 470;
    private final SharedStashPanel sharedStashPanel;
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
        sharedStashPanel = new SharedStashPanel(fileManager, this);
        setContentPane(sharedStashPanel);
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
        sharedStashPanel.build();
    }

    private static class SharedStashPanel extends JPanel {

        private final D2FileManager fileManager;
        private D2ViewSharedStash sharedStashView;
        private int selectedStashPane = 0;
        private Image background;

        public SharedStashPanel(D2FileManager fileManager, D2ViewSharedStash sharedStashView) {
            this.fileManager = fileManager;
            this.sharedStashView = sharedStashView;
            setLayout(new BorderLayout());
            setSize(BG_WIDTH, BG_HEIGHT);
            Dimension lSize = new Dimension(BG_WIDTH, BG_HEIGHT);
            setPreferredSize(lSize);
            addMouseListener(mouseListener);
            addMouseMotionListener(mouseMotionListener);
            setVisible(true);
            build();
        }

        public void build() {
            Image lEmptyBackground = D2ImageCache.getImage("stash" + (selectedStashPane + 1) + ".jpg");
            background = fileManager.getGraphicsConfiguration().createCompatibleImage(BG_WIDTH, BG_HEIGHT, Transparency.BITMASK);
            Graphics2D lGraphics = (Graphics2D) background.getGraphics();
            lGraphics.drawImage(lEmptyBackground, 0, 0, this);
            if (sharedStashView.sharedStash != null) placeItemsInView(sharedStashView.sharedStash);
            repaint();
        }

        private void placeItemsInView(D2SharedStash sharedStash) {
            D2SharedStash.D2SharedStashPane pane = sharedStash.getPane(selectedStashPane);
            pane.getItems().forEach(item -> {
                if (item.get_location() != 0 && item.get_body_position() != 0 && item.get_panel() != 5) return;
                Image image = D2ImageCache.getDC6Image(item);
                int col = item.get_col();
                int row = item.get_row();
                int x = getXCoordForCol(col);
                int y = getYCoordForRow(row);
                background.getGraphics().drawImage(image, x, y, this);
            });
        }

        private int getXCoordForCol(int col) {
            int diffx = (col / 2);
            return 29 + (col * 28) + ((diffx * 3) + ((col - diffx) * 2));
        }

        private int getYCoordForRow(int row) {
            int diffy = (row / 2);
            return 75 + (row * 28) + ((diffy * 3) + ((row - diffy) * 2));
        }

        private int getColForXCoord(int x) {
            if (x < 29) return -1;
            return ((2 * x) - 58) / 61;
        }

        private int getRowForYCoord(int y) {
            if (y < 75) return -1;
            return ((2 * y) - 150) / 61;
        }

        @Override
        public void paint(Graphics pGraphics) {
            super.paint(pGraphics);
            Graphics2D lGraphics = (Graphics2D) pGraphics;
            lGraphics.drawImage(background, 0, 0, this);
        }

        private final MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = getColForXCoord(e.getX());
                int row = getRowForYCoord(e.getY());
                if (col < 0 || row < 0 || col > 9 || row > 9) {
                    setCursorNormal();
                    return;
                }
                D2SharedStash.D2SharedStashPane stashPane = sharedStashView.sharedStash.getPane(selectedStashPane);
                D2Item item = stashPane.getItemCovering(col, row);
                if (item != null) {
                    setCursorPickupItem();
                } else {
                    D2Item itemOnClipboard = D2ViewClipboard.getItem();
                    boolean canDropItem = itemOnClipboard != null && stashPane.canDropItem(col, row, itemOnClipboard);
                    System.out.println(canDropItem);
                    if (itemOnClipboard != null && canDropItem) {
                        setCursorDropItem();
                    } else {
                        setCursorNormal();
                    }
                }
            }
        };

        public void setCursorPickupItem() {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void setCursorDropItem() {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }

        public void setCursorNormal() {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        private final MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (sharedStashView.sharedStash == null) return;
                Integer possibleStashTabClick = getPossibleStashTabClick(e.getX(), e.getY());
                setStashTab(possibleStashTabClick);
            }

            private void setStashTab(Integer possibleStashTabClick) {
                if (possibleStashTabClick == null) return;
                if (selectedStashPane == possibleStashTabClick) return;
                selectedStashPane = possibleStashTabClick;
                build();
            }

            private Integer getPossibleStashTabClick(int x, int y) {
                if (x >= 26 && x <= 258 && y >= 50 && y <= 72) {
                    if (x <= 103) return 0;
                    if (x <= 181) return 1;
                    return 2;
                }
                return null;
            }
        };
    }
}
