package gomule.gui.stashfilter;

import gomule.item.filter.*;

import java.awt.event.*;

import javax.swing.*;

/**
 * class in control of single combobox. 
 * When checkbox activated, add filter to D2FilterHandler and visa versa 
 * @author Marco
 *
 */
public class D2FilterSingleCheckBoxHandler
{
	private D2FilterHandler			iFilterHandler;
	
	private JCheckBox		iCheckBox;
	private Object			iFilterType;
	private D2ItemFilter	iFilter;
	
	public D2FilterSingleCheckBoxHandler(D2FilterHandler pFilterHandler, Object pFilterType, D2ItemFilter pFilter)
	{
		iFilterHandler = pFilterHandler;
		iFilterType = pFilterType;
		iFilter = pFilter;
		
		iCheckBox = new JCheckBox( iFilter.getDisplayString() );
		iCheckBox.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent pE )
			{
				if ( iCheckBox.isSelected() )
				{
					iFilterHandler.addFilter( iFilterType, iFilter );
				}
				else
				{
					iFilterHandler.removeFilter( iFilterType, iFilter );
				}
			}
		} );
	}
	
	public JComponent getDisplay()
	{
		return iCheckBox;
	}
}