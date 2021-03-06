package gomule.gui.desktop.generic;

import javax.swing.*;
import java.util.Iterator;

/**
 * desktop handler interface (generic for internal frames or tabs)
 *
 * @author mbr
 */
public interface GoMuleDesktop {
    public JComponent getDisplay();

    public GoMuleView getSelectedView();

    public void addView(GoMuleView pView);

    public void closeView(String pFileName);

    public void removeView(GoMuleView pView);

    public void closeViewAll();

    public void showView(GoMuleView pView);

    public Iterator getIteratorView();
//	public Iterator		getIteratorContainer();
}
