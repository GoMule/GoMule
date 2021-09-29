package gomule.d2i;

import gomule.gui.D2ItemListAdapter;
import gomule.item.D2Item;
import gomule.util.D2Project;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class D2SharedStash extends D2ItemListAdapter {
    private final List<D2SharedStashPane> panes;

    public D2SharedStash(String pFileName, List<D2SharedStashPane> panes) {
        super(pFileName);
        this.panes = panes;
    }

    public D2SharedStashPane getPane(int index) {
        return panes.get(index);
    }

    @Override
    public boolean containsItem(D2Item pItem) {
        return panes.stream().anyMatch(it -> it.items.contains(pItem));
    }

    @Override
    public void removeItem(D2Item pItem) {
        //Handled by panes
    }

    @Override
    public void addItem(D2Item pItem) {
        //Handled by panes
    }

    @Override
    public List<D2Item> getItemList() {
        return panes.stream().flatMap(it -> it.getItems().stream()).collect(Collectors.toList());
    }

    @Override
    public int getNrItems() {
        return panes.stream().map(it -> it.items.size()).reduce(0, Integer::sum);
    }

    @Override
    public String getFilename() {
        return iFileName;
    }

    @Override
    public boolean isSC() {
        return iFileName.toLowerCase(Locale.forLanguageTag("UTF-8")).contains("softcore");
    }

    @Override
    public boolean isHC() {
        return !isSC();
    }

    @Override
    public void fullDump(PrintWriter pWriter) {
        pWriter.println(iFileName);
        pWriter.println();
        List<D2Item> items = getItemList();
        for (D2Item item : items) {
            item.toWriter(pWriter);
        }
        pWriter.println("Finished: " + iFileName);
        pWriter.println();
    }

    @Override
    protected void saveInternal(D2Project pProject) {

    }

    public void replacePane(int paneIndex, D2SharedStashPane newPane) {
        panes.set(paneIndex, newPane);
    }

    public static class D2SharedStashPane {
        private final List<D2Item> items;
        private final D2Item[][] paneGrid;

        D2SharedStashPane(List<D2Item> items, D2Item[][] paneGrid) {
            this.items = items;
            this.paneGrid = paneGrid;
        }

        public static D2SharedStashPane fromItems(List<D2Item> items) {
            return new D2SharedStashPane(items, constructPaneGrid(items));
        }

        private static D2Item[][] constructPaneGrid(List<D2Item> items) {
            D2Item[][] grid = new D2Item[10][10];
            for (D2Item item : items) {
                for (int i = item.get_col(); i < (int) item.get_col() + (int) item.get_width(); i++) {
                    for (int j = item.get_row(); j < (int) item.get_row() + (int) item.get_height(); j++) {
                        if (grid[i][j] != null) throw new RuntimeException("Failed to create shared stash pane");
                        grid[i][j] = item;
                    }
                }
            }
            return grid;
        }

        public List<D2Item> getItems() {
            return items;
        }

        public D2Item getItemCovering(int col, int row) {
            return paneGrid[col][row];
        }

        public boolean canDropItem(int col, int row, D2Item item) {
            if (col > paneGrid.length - 1 || col < 0 || row > paneGrid[0].length - 1 || row < 0) return false;
            for (int i = col; i < col + item.get_width(); i++) {
                for (int j = row; j < row + item.get_height(); j++) {
                    if (i > paneGrid.length - 1 || j > paneGrid[0].length - 1) return false;
                    if (paneGrid[i][j] != null) return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return "D2SharedStashPane{" +
                    "items=" + items +
                    ", paneGrid=" + Arrays.toString(paneGrid) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            D2SharedStashPane that = (D2SharedStashPane) o;
            return Objects.equals(items, that.items) && Arrays.deepEquals(paneGrid, that.paneGrid);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(items);
            result = 31 * result + Arrays.deepHashCode(paneGrid);
            return result;
        }

        public D2SharedStashPane addItem(int col, int row, D2Item item) {
            item.set_col((short) col);
            item.set_row((short) row);
            item.set_location((short) 0);
            item.set_body_position((short) 0);
            item.set_panel((short) 5);
            item.setCharLvl(75);
            List<D2Item> items = new ArrayList<>(this.items);
            items.add(item);
            return D2SharedStashPane.fromItems(items);
        }

        public D2SharedStashPane removeItem(D2Item item) {
            List<D2Item> items = new ArrayList<>(this.items);
            items.remove(item);
            return D2SharedStashPane.fromItems(items);
        }
    }
}
