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
			
			D2FilterCategoriesComboboxHandler lCombobox1 = new D2FilterCategoriesComboboxHandler(CATEGORIES_1, lFilterFactory, 1);
			lPanel.addToPanel( lCombobox1.getDisplay(), 0, 0, 1, RandallPanel.HORIZONTAL );
			
			D2FilterCategoriesComboboxHandler lCombobox2 = new D2FilterCategoriesComboboxHandler(CATEGORIES_2, lFilterFactory, 2);
			lPanel.addToPanel( lCombobox2.getDisplay(), 1, 0, 1, RandallPanel.HORIZONTAL );
			
			D2FilterCategoriesComboboxHandler lCombobox3 = new D2FilterCategoriesComboboxHandler(CATEGORIES_3, lFilterFactory, 3);
			lPanel.addToPanel( lCombobox3.getDisplay(), 2, 0, 1, RandallPanel.HORIZONTAL );
			
			D2FilterCategoriesComboboxHandler lCombobox4 = new D2FilterCategoriesComboboxHandler(CATEGORIES_4, lFilterFactory, 4);
			lPanel.addToPanel( lCombobox4.getDisplay(), 3, 0, 1, RandallPanel.HORIZONTAL );
			
			lCombobox1.addFollowUp( lCombobox2 );
			lCombobox2.addFollowUp( lCombobox3 );
			lCombobox2.addFollowUp( lCombobox4 ); // mbr: on purpose to the second combobox
			
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
	private static final Object	CATEGORIES_1 = new Object();
	private static final Object	CATEGORIES_2 = new Object();
	private static final Object	CATEGORIES_3 = new Object();
	private static final Object	CATEGORIES_4 = new Object();
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
		private D2ItemFilterComposite	iFilterCategories1List;
		private D2ItemFilterComposite	iFilterCategories2List;
		private D2ItemFilterComposite	iFilterCategories3List;
		private D2ItemFilterComposite	iFilterCategories4List;
		
		public D2FilterHandler()
		{
			iFilterList = new D2ItemFilterComposite();
			
			iFilterTypeList = new D2ItemFilterComposite();
			iFilterTypeList.setAnd( false );
			
			iFilterQualityList = new D2ItemFilterComposite();
			iFilterQualityList.setAnd( false );
			
			iFilterEtherealList = new D2ItemFilterComposite();
			iFilterEtherealList.setAnd( false );
			
			iFilterCategories1List = new D2ItemFilterComposite();
			iFilterCategories2List = new D2ItemFilterComposite();
			iFilterCategories3List = new D2ItemFilterComposite();
			iFilterCategories4List = new D2ItemFilterComposite();
			
			iFilterList.addFilter( iFilterTypeList );
			iFilterList.addFilter( iFilterQualityList );
			iFilterList.addFilter( iFilterEtherealList );
			iFilterList.addFilter( iFilterCategories1List );
			iFilterList.addFilter( iFilterCategories2List );
			iFilterList.addFilter( iFilterCategories3List );
			iFilterList.addFilter( iFilterCategories4List );
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
			else if ( pFilterType == CATEGORIES_1 )
			{
				return iFilterCategories1List;
			}
			else if ( pFilterType == CATEGORIES_2 )
			{
				return iFilterCategories2List;
			}
			else if ( pFilterType == CATEGORIES_3 )
			{
				return iFilterCategories3List;
			}
			else if ( pFilterType == CATEGORIES_4 )
			{
				return iFilterCategories4List;
			}
			return null;	
		}
		
		public void clearFilter(Object pFilterType)
		{
			D2ItemFilterComposite lComposite = getComposite(pFilterType);
			lComposite.clearFilter( );
			iViewStash.itemListChanged();
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
	class D2FilterCategoriesComboboxHandler
	{
		private JComboBox				iComboBox;
		private DefaultComboBoxModel	iModel;
		
		private ArrayList				iFollowUpList;
		private Object					iFilterType;
		
		private D2ItemFilterFactory		iFilterFactory;
		private int						iLevel;
		
		public D2FilterCategoriesComboboxHandler(Object pFilterType, D2ItemFilterFactory pFilterFactory, int pLevel)
		{
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
	
}
