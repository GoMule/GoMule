package gomule.d2i;

import gomule.gui.D2ItemListAdapter;
import gomule.item.D2Item;
import gomule.util.D2Project;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

public class D2SharedStash extends D2ItemListAdapter {
    public D2SharedStash(String pFileName) {
        super(pFileName);
    }

    @Override
    public boolean containsItem(D2Item pItem) {
        return false;
    }

    @Override
    public void removeItem(D2Item pItem) {

    }

    @Override
    public void addItem(D2Item pItem) {

    }

    @Override
    public ArrayList getItemList() {
        return null;
    }

    @Override
    public int getNrItems() {
        return 0;
    }

    @Override
    public String getFilename() {
        return null;
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

    }

    @Override
    protected void saveInternal(D2Project pProject) {

    }
}
