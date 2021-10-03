package gomule.gui.sharedStash;

import gomule.d2i.D2SharedStash;
import gomule.d2i.D2SharedStash.D2SharedStashPane;
import gomule.gui.*;
import gomule.item.D2Item;
import randall.util.RandallPanel;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;

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
        return sharedStash.isHC();
    }

    @Override
    public boolean isSC() {
        return sharedStash.isSC();
    }

    @Override
    public void closeView() {
        disconnect(null);
        fileManager.removeFromOpenWindows(this);
    }

    @Override
    public boolean isModified() {
        return sharedStash.isModified();
    }

    @Override
    public D2ItemList getItemLists() {
        return sharedStash;
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

    public String getSharedStashName() {
        return sharedStashFilename.substring(sharedStashFilename.lastIndexOf(File.separator) + 1);
    }

    public SharedStashPanel getSharedStashPanel() {
        return sharedStashPanel;
    }

    public static class SharedStashPanel extends JPanel {

        private final D2FileManager fileManager;
        private final D2ViewSharedStash sharedStashView;
        private int selectedStashPane = 0;
        private Image background;

        public SharedStashPanel(D2FileManager fileManager, D2ViewSharedStash sharedStashView) {
            this.fileManager = fileManager;
            this.sharedStashView = sharedStashView;
            setLayout(new BorderLayout());
            setSize(BG_WIDTH, BG_HEIGHT);
            Dimension lSize = new Dimension(BG_WIDTH, BG_HEIGHT);
            setPreferredSize(lSize);
            addMouseListener(new MouseClickHandler(this));
            addMouseMotionListener(mouseMotionListener);
            setVisible(true);
            ToolTipManager.sharedInstance().setDismissDelay(40000);
            ToolTipManager.sharedInstance().setInitialDelay(300);
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
            D2SharedStashPane pane = sharedStash.getPane(selectedStashPane);
            pane.getItems().forEach(item -> {
                if (item.get_location() != 0 && item.get_body_position() != 0 && item.get_panel() != 5) return;
                Image image = D2ImageCache.getDC6Image(item);
                int col = item.get_col();
                int row = item.get_row();
                int x = getXCoordForCol(col);
                int y = getYCoordForRow(row);
                background.getGraphics().drawImage(image, x, y, this);
            });
            background.getGraphics().drawString(Long.toString(pane.getGold()), 155, 417);
        }

        private static int getXCoordForCol(int col) {
            int diffx = (col / 2);
            return 29 + (col * 28) + ((diffx * 3) + ((col - diffx) * 2));
        }

        private static int getYCoordForRow(int row) {
            int diffy = (row / 2);
            return 75 + (row * 28) + ((diffy * 3) + ((row - diffy) * 2));
        }

        private static int getColForXCoord(int x) {
            if (x < 29) return -1;
            return ((2 * x) - 58) / 61;
        }

        private static int getRowForYCoord(int y) {
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
                D2SharedStashPane stashPane = sharedStashView.sharedStash.getPane(selectedStashPane);
                D2Item item = stashPane.getItemCovering(col, row);
                if (item != null) {
                    setCursorPickupItem();
                    setToolTipText(item.itemDumpHtml(false));
                } else {
                    D2Item itemOnClipboard = D2ViewClipboard.getItem();
                    boolean canDropItem = itemOnClipboard != null && stashPane.canDropItem(col, row, itemOnClipboard);
                    if (itemOnClipboard != null && canDropItem) {
                        setCursorDropItem();
                    } else {
                        setCursorNormal();
                    }
                    setToolTipText(null);
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

        private static class MouseClickHandler extends MouseAdapter {

            private final D2ViewSharedStash sharedStashView;
            private final SharedStashPanel sharedStashPanel;

            public MouseClickHandler(SharedStashPanel sharedStashPanel) {
                this.sharedStashView = sharedStashPanel.sharedStashView;
                this.sharedStashPanel = sharedStashPanel;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) handleLeftClick(e);
            }

            private void handleLeftClick(MouseEvent e) {
                if (sharedStashView.sharedStash == null) return;
                Integer possibleStashTabClick = getPossibleStashTabClick(e.getX(), e.getY());
                setStashTab(possibleStashTabClick);
                if (isClickOnGoldButton(e.getX(), e.getY())) showGoldDialog();

                int col = getColForXCoord(e.getX());
                int row = getRowForYCoord(e.getY());
                if (col < 0 || row < 0 || col > 9 || row > 9) return;
                D2SharedStashPane stashPane = getSelectedStashPane();
                D2Item item = stashPane.getItemCovering(col, row);
                if (item != null) {
                    moveItemToClipboard(stashPane, item);
                } else if (D2ViewClipboard.getItem() != null) {
                    tryMoveItemFromClipboard(stashPane, col, row);
                }
            }

            private D2SharedStashPane getSelectedStashPane() {
                return sharedStashView.sharedStash.getPane(getSelectedStashPaneIndex());
            }

            private int getSelectedStashPaneIndex() {
                return sharedStashView.sharedStashPanel.selectedStashPane;
            }

            private boolean isClickOnGoldButton(int x, int y) {
                return x >= 120 && x <= 240 && y >= 394 && y <= 431;
            }

            private void showGoldDialog() {
                JOptionPane.showConfirmDialog(sharedStashPanel, new GoldTransferPanel(sharedStashView.sharedStash, sharedStashPanel), "Transfer Gold",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
            }

            private void tryMoveItemFromClipboard(D2SharedStashPane stashPane, int col, int row) {
                D2Item item = D2ViewClipboard.getItem();
                if (stashPane.canDropItem(col, row, item)) {
                    D2SharedStashPane d2SharedStashPane = stashPane.addItem(col, row, D2ViewClipboard.removeItem());
                    sharedStashView.sharedStash.replacePane(getSelectedStashPaneIndex(), d2SharedStashPane);
                    sharedStashView.sharedStash.setModified(true);
                    sharedStashPanel.setCursorPickupItem();
                }
            }

            private void moveItemToClipboard(D2SharedStashPane stashPane, D2Item item) {
                D2SharedStashPane d2SharedStashPane = stashPane.removeItem(item);
                sharedStashView.sharedStash.replacePane(getSelectedStashPaneIndex(), d2SharedStashPane);
                D2ViewClipboard.addItem(item);
                sharedStashView.sharedStash.setModified(true);
                sharedStashPanel.setCursorDropItem();
            }

            private void setStashTab(Integer possibleStashTabClick) {
                if (possibleStashTabClick == null) return;
                if (getSelectedStashPaneIndex() == possibleStashTabClick) return;
                sharedStashPanel.selectedStashPane = possibleStashTabClick;
                sharedStashPanel.build();
            }

            private Integer getPossibleStashTabClick(int x, int y) {
                if (x >= 26 && x <= 258 && y >= 50 && y <= 72) {
                    if (x <= 103) return 0;
                    if (x <= 181) return 1;
                    return 2;
                }
                return null;
            }
        }

        public List<D2Item> removeAllItems() {
            if (sharedStashView.sharedStash == null) return emptyList();
            D2SharedStashPane stashPane = sharedStashView.sharedStash.getPane(selectedStashPane);
            sharedStashView.sharedStash.replacePane(selectedStashPane, D2SharedStashPane.fromItems(emptyList(), stashPane.getGold()));
            sharedStashView.sharedStash.setModified(true);
            return stashPane.getItems();
        }

        public List<D2Item> tryToAddItems(List<D2Item> items) {
            if (sharedStashView.sharedStash == null) return emptyList();
            D2SharedStashPane stashPane = sharedStashView.sharedStash.getPane(selectedStashPane);
            List<D2Item> successfullyAddedItems = new ArrayList<>();
            for (D2Item item : items) {
                stashPane = getD2SharedStashPane(stashPane, successfullyAddedItems, item);
            }
            sharedStashView.sharedStash.replacePane(selectedStashPane, stashPane);
            sharedStashView.sharedStash.setModified(true);
            return successfullyAddedItems;
        }

        private D2SharedStashPane getD2SharedStashPane(D2SharedStashPane stashPane, List<D2Item> successfullyAddedItems, D2Item item) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (stashPane.canDropItem(j, i, item)) {
                        stashPane = stashPane.addItem(j, i, item);
                        successfullyAddedItems.add(item);
                        return stashPane;
                    }
                }
            }
            return stashPane;
        }

        private static class GoldTransferPanel extends JPanel {
            private final D2SharedStash sharedStash;
            private final SharedStashPanel sharedStashPanel;

            public GoldTransferPanel(D2SharedStash sharedStash, SharedStashPanel sharedStashPanel) {
                super(new GridLayout(0, 1));
                this.sharedStash = sharedStash;
                this.sharedStashPanel = sharedStashPanel;
                setSize(300, 100);
                setPreferredSize(new Dimension(300, 100));
                JTextField transferGoldAmount = new JTextField("10000");
                JButton transferGoldOut = new JButton("From Stash");
                transferGoldOut.addActionListener(pEvent -> transferGoldOut(getGoldAmount(transferGoldAmount)));
                JButton transferGoldIn = new JButton("To Stash");
                transferGoldIn.addActionListener(pEvent -> transferGoldIn(getGoldAmount(transferGoldAmount)));

                RandallPanel transferPanel = new RandallPanel(true);
                transferPanel.setBorder("Transfer");
                transferPanel.addToPanel(transferGoldIn, 0, 0, 1, RandallPanel.NONE);
                transferPanel.addToPanel(transferGoldAmount, 1, 0, 1, RandallPanel.HORIZONTAL);
                transferPanel.addToPanel(transferGoldOut, 2, 0, 1, RandallPanel.NONE);
                add(transferPanel);
            }

            private void transferGoldOut(int goldAmount) {
                transferGold(
                        goldAmount,
                        Integer.MAX_VALUE,
                        this::getSelectedStashGoldValue,
                        this::getBankGoldValue,
                        this::updateSelectedStashGoldValue,
                        this::updateBankGoldValue
                );
            }

            private void transferGoldIn(int goldAmount) {
                transferGold(
                        goldAmount,
                        2_500_000,
                        this::getBankGoldValue,
                        this::getSelectedStashGoldValue,
                        this::updateBankGoldValue,
                        this::updateSelectedStashGoldValue
                );
            }

            private void updateBankGoldValue(int gold) {
                D2FileManager.getInstance().getProject().setBankValue(gold);
            }

            private void updateSelectedStashGoldValue(int gold) {
                sharedStash.replacePane(sharedStashPanel.selectedStashPane, D2SharedStashPane.fromItems(sharedStash.getPane(sharedStashPanel.selectedStashPane).getItems(), gold));
                sharedStash.setModified(true);
            }

            private int getBankGoldValue() {
                return D2FileManager.getInstance().getProject().getBankValue();
            }

            private int getSelectedStashGoldValue() {
                return sharedStash.getPane(sharedStashPanel.selectedStashPane).getGold();
            }

            private void transferGold(int goldAmount, int maxGold, Supplier<Integer> sourceGold, Supplier<Integer> destinationGold, Consumer<Integer> updateSource, Consumer<Integer> updateDestination) {
                if (goldAmount > sourceGold.get()) goldAmount = sourceGold.get();
                if (goldAmount > maxGold) goldAmount = maxGold;

                int newSourceGoldValue = sourceGold.get() - goldAmount;
                int newDestinationGoldValue = destinationGold.get() + goldAmount;
                updateSource.accept(newSourceGoldValue);
                updateDestination.accept(newDestinationGoldValue);
            }

            public int getGoldAmount(JTextField transferGoldAmount) {
                try {
                    return Integer.parseInt(transferGoldAmount.getText());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
    }
}
