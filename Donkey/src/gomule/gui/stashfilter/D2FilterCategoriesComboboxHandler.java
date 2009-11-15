package gomule.gui.stashfilter;

import gomule.item.filter.*;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * class in control of single combobox. 
 * When checkbox activated, add filter to D2FilterHandler and visa versa 
 * @author Marco
 *
 */
public class D2FilterCategoriesComboboxHandler
{
	private D2FilterHandler			iFilterHandler;
	
	private JComboBox				iComboBox;
	private DefaultComboBoxModel	iModel;
	
	private ArrayList				iFollowUpList;
	private Object					iFilterType;
	
	private D2ItemFilterFactory		iFilterFactory;
	private int						iLevel;
	
	public D2FilterCategoriesComboboxHandler(D2FilterHandler pFilterHandler, Object pFilterType, D2ItemFilterFactory pFilterFactory, int pLevel)
	{
		iFilterHandler = pFilterHandler;
		iFollowUpList = new ArrayList();
		iFilterType = pFilterType;
		iFilterFactory = pFilterFactory;
		iLevel = pLevel;
		
		iModel = new DefaultComboBoxModel();
		fillModel( null );
		
		iComboBox = new JComboBox( iModel );
		iComboBox.setMaximumRowCount( 25 ); // default only 8, make sure some more are displayed
		iComboBox.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent pE )
			{
				iFilterHandler.clearFilter( iFilterType );
				Object lSelected = iComboBox.getSelectedItem();
				if ( lSelected instanceof D2ItemFilter )
				{
					iFilterHandler.addFilter( iFilterType, (D2ItemFilter) lSelected );
				}
				doFollowUpAction();
			}
		} );
	}
	
	private boolean fillModel(D2ItemFilter pFilter)
	{
		iModel.removeAllElements();
		iModel.addElement( "All" );
		
		ArrayList lFilterList = iFilterFactory.getItemCategories( iLevel, pFilter );
		
		if ( lFilterList == null )
		{
			return false;
		}
		
		for ( int i = 0 ; i < lFilterList.size() ; i++ )
		{
			iModel.addElement( lFilterList.get( i ) );
		}
		
		return true;
	}
	
	public void addFollowUp(D2FilterCategoriesComboboxHandler pFollowUp)
	{
		iFollowUpList.add( pFollowUp );
		doFollowUpAction();
	}
	
	protected void doFollowUpAction()
	{
		if ( !iFollowUpList.isEmpty() )
		{
			Object lSelected = iComboBox.getSelectedItem();
			if ( lSelected instanceof D2ItemFilter )
			{
				for ( int i = 0 ; i < iFollowUpList.size() ; i++ )
				{
					D2FilterCategoriesComboboxHandler lFollowUp = (D2FilterCategoriesComboboxHandler) iFollowUpList.get( i );
					lFollowUp.enable( (D2ItemFilter) lSelected );
				}
			}
			else
			{
				for ( int i = 0 ; i < iFollowUpList.size() ; i++ )
				{
					D2FilterCategoriesComboboxHandler lFollowUp = (D2FilterCategoriesComboboxHandler) iFollowUpList.get( i );
					lFollowUp.disable();
				}
			}
		}
	}
	
	public void enable( D2ItemFilter pFilter )
	{
		if ( fillModel( pFilter ) )
		{
			iComboBox.setEnabled( true );
		}
		else
		{
			disable();
		}
	}
	
	public void disable()
	{
		iComboBox.setEnabled( false );
		iComboBox.setSelectedIndex( 0 );
		doFollowUpAction();
	}
	
	public JComponent getDisplay()
	{
		return iComboBox;
	}
}
