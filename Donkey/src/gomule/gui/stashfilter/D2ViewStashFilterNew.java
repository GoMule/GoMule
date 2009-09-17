package gomule.gui.stashfilter;

import gomule.gui.*;
import gomule.item.*;
import gomule.item.filter.*;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import randall.util.*;

public class D2ViewStashFilterNew implements D2ViewStashFilter
{
	private D2ViewStash			iViewStash;
	private RandallPanel		iDisplay;
	
	private D2FilterHandler		iFilterHandler;
	
	public D2ViewStashFilterNew(D2ViewStash pViewStash)
	{
		iViewStash = pViewStash;
		
		// real filter handler
		iFilterHandler = new D2FilterHandler();
		
		// filter gui for stash
		iDisplay = new RandallPanel();
		
		// filter (1.10) factory
		D2ItemFilterFactory lFilterFactory = new D2ItemFilterFactory110();

		// for all to add filter panels
		int lY = 0;
		
		// quality filter panel
		{
			RandallPanel lPanel = new RandallPanel();
			
			// types as checkboxes
			ArrayList lQualities = lFilterFactory.getItemQualities();
			
			for ( int i = 0 ; i < lQualities.size() ; i++ )
			{
				D2ItemFilter lFilter = (D2ItemFilter) lQualities.get( i );
				
				D2FilterSingleCheckBoxHandler lCheckbox = new D2FilterSingleCheckBoxHandler(QUALITY, lFilter);
				
				lPanel.addToPanel( lCheckbox.getDisplay(), i, 0, 1, RandallPanel.HORIZONTAL );
			}
			
			// add ethereal to this line as well
			{
				D2ItemFilter lFilter = lFilterFactory.getEthereal();
				
				D2FilterSingleCheckBoxHandler lCheckbox = new D2FilterSingleCheckBoxHandler(ETHEREAL, lFilter);
				
				lPanel.addToPanel( lCheckbox.getDisplay(), lQualities.size(), 0, 1, RandallPanel.HORIZONTAL );
			}
			
			iDisplay.addToPanel( lPanel, 0, lY++, 1, RandallPanel.HORIZONTAL );
		}
		
		// type filter panel
		{
			RandallPanel lPanel = new RandallPanel();
			
			// types as checkboxes
			ArrayList lTypes = lFilterFactory.getItemTypes();
			
			for ( int i = 0 ; i < lTypes.size() ; i++ )
			{
				D2ItemFilter lFilter = (D2ItemFilter) lTypes.get( i );
				
				D2FilterSingleCheckBoxHandler lCheckbox = new D2FilterSingleCheckBoxHandler(TYPE, lFilter);
				
				lPanel.addToPanel( lCheckbox.getDisplay(), i, 0, 1, RandallPanel.HORIZONTAL );
			}
			
			iDisplay.addToPanel( lPanel, 0, lY++, 1, RandallPanel.HORIZONTAL );
		}
		
		// categories filter panel
		{
			RandallPanel lPanel = new RandallPanel();
			
//			asdf
			
			iDisplay.addToPanel( lPanel, 0, lY++, 1, RandallPanel.HORIZONTAL );
		}
	}

	public ArrayList filterItems( ArrayList pList )
	{
		ArrayList lReturn = new ArrayList();
		
		for ( int i = 0 ; i < pList.size() ; i++ )
		{
			D2Item lItem = (D2Item) pList.get( i );
			if ( iFilterHandler.getItemFilter().isFilterItem( lItem ) )
			{
				lReturn.add( lItem );
			}
		}
		
		return lReturn;
	}

	public JComponent getDisplay()
	{
		return iDisplay;
	}
	
	private static final Object	TYPE = new Object();
	private static final Object	QUALITY = new Object();
	private static final Object	ETHEREAL = new Object();
	/**
	 * class in controll of all active filters
	 * @author Marco
	 *
	 */
	class D2FilterHandler
	{
		private D2ItemFilterComposite	iFilterList;
		private D2ItemFilterComposite	iFilterTypeList;
		private D2ItemFilterComposite	iFilterQualityList;
		private D2ItemFilterComposite	iFilterEtherealList;
		
		public D2FilterHandler()
		{
			iFilterList = new D2ItemFilterComposite();
			
			iFilterTypeList = new D2ItemFilterComposite();
			iFilterTypeList.setAnd( false );
			
			iFilterQualityList = new D2ItemFilterComposite();
			iFilterQualityList.setAnd( false );
			
			iFilterEtherealList = new D2ItemFilterComposite();
			iFilterEtherealList.setAnd( false );
			
			iFilterList.addFilter( iFilterTypeList );
			iFilterList.addFilter( iFilterQualityList );
			iFilterList.addFilter( iFilterEtherealList );
		}
		
		public D2ItemFilter getItemFilter()
		{
			return iFilterList;
		}
		
		private D2ItemFilterComposite getComposite(Object pFilterType)
		{
			if ( pFilterType == TYPE )
			{
				return iFilterTypeList;
			}
			else if ( pFilterType == QUALITY )
			{
				return iFilterQualityList;
			}
			else if ( pFilterType == ETHEREAL )
			{
				return iFilterEtherealList;
			}
			return null;	
		}
		
		public void addFilter(Object pFilterType, D2ItemFilter pFilter)
		{
			D2ItemFilterComposite lComposite = getComposite(pFilterType);
			
			if ( lComposite != null )
			{
				lComposite.addFilter( pFilter );
			}
			else
			{
				System.err.println("Unknown filter type for adding");
				return;
			}
			iViewStash.itemListChanged();
		}
		
		public void removeFilter(Object pFilterType, D2ItemFilter pFilter)
		{
			D2ItemFilterComposite lComposite = getComposite(pFilterType);
			
			if ( lComposite != null )
			{
				lComposite.removeFilter( pFilter );
			}
			else
			{
				System.err.println("Unknown filter type for removing");
				return;
			}
			iViewStash.itemListChanged();
		}
	}
	
	/**
	 * class in control of single combobox. 
	 * When checkbox activated, add filter to D2FilterHandler and visa versa 
	 * @author Marco
	 *
	 */
	class D2FilterSingleCheckBoxHandler
	{
		private JCheckBox		iCheckBox;
		private Object			iFilterType;
		private D2ItemFilter	iFilter;
		
		public D2FilterSingleCheckBoxHandler(Object pFilterType, D2ItemFilter pFilter)
		{
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
	
	/**
	 * class in control of single combobox. 
	 * When checkbox activated, add filter to D2FilterHandler and visa versa 
	 * @author Marco
	 *
	 */
	class D2FilterComboboxHandler
	{
		private JComboBox		iComboBox;
		private Object			iFilterType;
		
		public D2FilterComboboxHandler(Object pFilterType, ArrayList pFilterList)
		{
			iFilterType = pFilterType;
			
			DefaultComboBoxModel lModel = new DefaultComboBoxModel();
			lModel.addElement( "test" );
			
			iComboBox = new JComboBox( lModel );
//			iFilter = pFilter;
			
//			iCheckBox = new JCheckBox( iFilter.getDisplayString() );
//			iCheckBox.addActionListener( new ActionListener()
//			{
//				public void actionPerformed( ActionEvent pE )
//				{
//					if ( iCheckBox.isSelected() )
//					{
//						iFilterHandler.addFilter( iFilterType, iFilter );
//					}
//					else
//					{
//						iFilterHandler.removeFilter( iFilterType, iFilter );
//					}
//				}
//			} );
		}
		
		public JComponent getDisplay()
		{
			return iComboBox;
		}
	}
	
	class D2FilterComboboxItem
	{
		private D2ItemFilter	iFilter;
		
	}
	
}
