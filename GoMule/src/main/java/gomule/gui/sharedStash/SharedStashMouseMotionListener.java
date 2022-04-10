package gomule.gui.sharedStash;

import gomule.d2i.D2SharedStash;
import gomule.gui.D2ViewClipboard;
import gomule.item.D2Item;
import gomule.item.D2ItemRenderer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import static gomule.gui.sharedStash.SharedStashPanel.getColForXCoord;
import static gomule.gui.sharedStash.SharedStashPanel.getRowForYCoord;

class SharedStashMouseMotionListener extends MouseMotionAdapter {
    private final SharedStashPanel sharedStashPanel;

    public SharedStashMouseMotionListener(SharedStashPanel sharedStashPanel) {
        this.sharedStashPanel = sharedStashPanel;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int col = getColForXCoord(e.getX());
        int row = getRowForYCoord(e.getY());
        if (col < 0 || row < 0 || col > 9 || row > 9) {
            sharedStashPanel.setCursorNormal();
            return;
        }
        D2SharedStash.D2SharedStashPane stashPane = sharedStashPanel.getSelectedStashPane();
        D2Item item = stashPane.getItemCovering(col, row);
        if (item != null) {
            sharedStashPanel.setCursorPickupItem();
            sharedStashPanel.setToolTipText(D2ItemRenderer.itemDumpHtml(item, false));
        } else {
            D2Item itemOnClipboard = D2ViewClipboard.getItem();
            boolean canDropItem = itemOnClipboard != null && stashPane.canDropItem(col, row, itemOnClipboard);
            if (itemOnClipboard != null && canDropItem) {
                sharedStashPanel.setCursorDropItem();
            } else {
                sharedStashPanel.setCursorNormal();
            }
            sharedStashPanel.setToolTipText(null);
        }
    }
}
