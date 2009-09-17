package gomule.gui.stashfilter;

import java.util.*;

import javax.swing.*;

public interface D2ViewStashFilter
{
	public JComponent getDisplay();
	
	public ArrayList filterItems(ArrayList pList);
}
