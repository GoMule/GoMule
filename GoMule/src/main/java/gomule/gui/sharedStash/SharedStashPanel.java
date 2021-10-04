package gomule.gui.sharedStash;

import gomule.d2i.D2SharedStash;
import gomule.gui.D2FileManager;
import gomule.gui.D2ImageCache;
import gomule.item.D2Item;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.util.Collections.emptyList;

public class SharedStashPanel extends JPanel {

    public static final int BG_WIDTH = 362;
    public static final int BG_HEIGHT = 470;
    private final D2FileManager fileManager;
    private final D2ViewSharedStash sharedStashView;
    private int selectedStashPaneIndex = 0;
    private Image background;

    public SharedStashPanel(D2FileManager fileManager, D2ViewSharedStash sharedStashView) {
        this.fileManager = fileManager;
        this.sharedStashView = sharedStashView;
        setLayout(new BorderLayout());
        setSize(BG_WIDTH, BG_HEIGHT);
        Dimension lSize = new Dimension(BG_WIDTH, BG_HEIGHT);
        setPreferredSize(lSize);
        addMouseListener(new SharedStashPanelMouseClickHandler(this));
        addMouseMotionListener(new SharedStashMouseMotionListener(this));
        setVisible(true);
        ToolTipManager.sharedInstance().setDismissDelay(40000);
        ToolTipManager.sharedInstance().setInitialDelay(300);
        build();
    }

    public void build() {
        Image lEmptyBackground = D2ImageCache.getImage("stash" + (selectedStashPaneIndex + 1) + ".jpg");
        background = fileManager.getGraphicsConfiguration().createCompatibleImage(BG_WIDTH, BG_HEIGHT, Transparency.BITMASK);
        Graphics2D lGraphics = (Graphics2D) background.getGraphics();
        lGraphics.drawImage(lEmptyBackground, 0, 0, this);
        if (getSharedStash() != null) placeItemsInView();
        repaint();
    }

    private void placeItemsInView() {
        D2SharedStash.D2SharedStashPane pane = getSelectedStashPane();
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

    public static int getXCoordForCol(int col) {
        int diffx = (col / 2);
        return 29 + (col * 28) + ((diffx * 3) + ((col - diffx) * 2));
    }

    public static int getYCoordForRow(int row) {
        int diffy = (row / 2);
        return 75 + (row * 28) + ((diffy * 3) + ((row - diffy) * 2));
    }

    public static int getColForXCoord(int x) {
        if (x < 29) return -1;
        return ((2 * x) - 58) / 61;
    }

    public static int getRowForYCoord(int y) {
        if (y < 75) return -1;
        return ((2 * y) - 150) / 61;
    }

    @Override
    public void paint(Graphics pGraphics) {
        super.paint(pGraphics);
        Graphics2D lGraphics = (Graphics2D) pGraphics;
        lGraphics.drawImage(background, 0, 0, this);
    }

    public void setCursorPickupItem() {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setCursorDropItem() {
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void setCursorNormal() {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public java.util.List<D2Item> removeAllItems() {
        D2SharedStash sharedStash = getSharedStash();
        if (sharedStash == null) return emptyList();
        D2SharedStash.D2SharedStashPane stashPane = getSelectedStashPane();
        sharedStash.replacePane(selectedStashPaneIndex, D2SharedStash.D2SharedStashPane.fromItems(emptyList(), stashPane.getGold()));
        sharedStash.setModified(true);
        return stashPane.getItems();
    }

    public java.util.List<D2Item> tryToAddItems(java.util.List<D2Item> items) {
        D2SharedStash sharedStash = getSharedStash();
        if (sharedStash == null) return emptyList();
        D2SharedStash.D2SharedStashPane stashPane = getSelectedStashPane();
        java.util.List<D2Item> successfullyAddedItems = new ArrayList<>();
        for (D2Item item : items) {
            stashPane = getD2SharedStashPane(stashPane, successfullyAddedItems, item);
        }
        sharedStash.replacePane(selectedStashPaneIndex, stashPane);
        sharedStash.setModified(true);
        return successfullyAddedItems;
    }

    public D2SharedStash.D2SharedStashPane getSelectedStashPane() {
        return getSharedStash().getPane(selectedStashPaneIndex);
    }

    public D2SharedStash getSharedStash() {
        return sharedStashView.getSharedStash();
    }

    public int getSelectedStashPaneIndex() {
        return selectedStashPaneIndex;
    }

    public void setSelectedStashPaneIndex(int selectedStashPaneIndex) {
        this.selectedStashPaneIndex = selectedStashPaneIndex;
    }

    private D2SharedStash.D2SharedStashPane getD2SharedStashPane(D2SharedStash.D2SharedStashPane stashPane, java.util.List<D2Item> successfullyAddedItems, D2Item item) {
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
}