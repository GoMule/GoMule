package gomule.gui.stashfilter;

import gomule.gui.*;
import gomule.item.*;
import gomule.item.filter.*;

import java.util.*;

import javax.swing.*;

import randall.util.*;

public class D2ViewStashFilterNew implements D2ViewStashFilter
{
	protected static final Object	TYPE = new Object();
	protected static final Object	QUALITY = new Object();
	protected static final Object	ETHEREAL = new Object();
	protected static final Object	CATEGORIES_1 = new Object();
	protected static final Object	CATEGORIES_2 = new Object();
	protected static final Object	CATEGORIES_3 = new Object();
	protected static final Object	CATEGORIES_4 = new Object();

	protected static final Object	TEXT_FILTER = new Object();
	
	private RandallPanel		iDisplay;
	
	private D2FilterHandler		iFilterHandler;
	
	public D2ViewStashFilterNew(D2ViewStash pViewStash)
	{
		// real filter handler
		iFilterHandler = new D2FilterHandler(pViewStash);
		
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
				
				D2FilterSingleCheckBoxHandler lCheckbox = new D2FilterSingleCheckBoxHandler(iFilterHandler, QUALITY, lFilter);
				
				lPanel.addToPanel( lCheckbox.getDisplay(), i, 0, 1, RandallPanel.HORIZONTAL );
			}
			
			// add ethereal to this line as well
			{
				D2ItemFilter lFilter = lFilterFactory.getEthereal();
				
				D2FilterSingleCheckBoxHandler lCheckbox = new D2FilterSingleCheckBoxHandler(iFilterHandler, ETHEREAL, lFilter);
				
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
				
				D2FilterSingleCheckBoxHandler lCheckbox = new D2FilterSingleCheckBoxHandler(iFilterHandler, TYPE, lFilter);
				
				lPanel.addToPanel( lCheckbox.getDisplay(), i, 0, 1, RandallPanel.HORIZONTAL );
			}
			
			iDisplay.addToPanel( lPanel, 0, lY++, 1, RandallPanel.HORIZONTAL );
		}
		
		// categories filter panel
		{
			RandallPanel lPanel = new RandallPanel();
			
			D2FilterCategoriesComboboxHandler lCombobox1 = new D2FilterCategoriesComboboxHandler(iFilterHandler, CATEGORIES_1, lFilterFactory, 1);
			lPanel.addToPanel( lCombobox1.getDisplay(), 0, 0, 1, RandallPanel.HORIZONTAL );
			
			D2FilterCategoriesComboboxHandler lCombobox2 = new D2FilterCategoriesComboboxHandler(iFilterHandler, CATEGORIES_2, lFilterFactory, 2);
			lPanel.addToPanel( lCombobox2.getDisplay(), 1, 0, 1, RandallPanel.HORIZONTAL );
			
			D2FilterCategoriesComboboxHandler lCombobox3 = new D2FilterCategoriesComboboxHandler(iFilterHandler, CATEGORIES_3, lFilterFactory, 3);
			lPanel.addToPanel( lCombobox3.getDisplay(), 2, 0, 1, RandallPanel.HORIZONTAL );
			
			D2FilterCategoriesComboboxHandler lCombobox4 = new D2FilterCategoriesComboboxHandler(iFilterHandler, CATEGORIES_4, lFilterFactory, 4);
			lPanel.addToPanel( lCombobox4.getDisplay(), 3, 0, 1, RandallPanel.HORIZONTAL );
			
			lCombobox1.addFollowUp( lCombobox2 );
			lCombobox2.addFollowUp( lCombobox3 );
			lCombobox2.addFollowUp( lCombobox4 ); // mbr: on purpose to the second combobox
			
			iDisplay.addToPanel( lPanel, 0, lY++, 1, RandallPanel.HORIZONTAL );
		}
		
		{
			RandallPanel lFreeFilter = new RandallPanel();
			int lFreeY = 0;
			
			for ( int i = 0 ; i < 6 ; i++ )
			{
				// free filter
				{
					D2FilterSingleFreeHandler lFree = new D2FilterSingleFreeHandler(iFilterHandler, lFilterFactory.getItemQueryFactory());
					if ( i == 1 || i == 2)
					{
						lFree.setSelectedIndex( i );
					}
					lFree.setSelectedOp( 1 );
					
					lFreeFilter.addToPanel( lFree.getDisplay(), 0, lFreeY, 1, RandallPanel.HORIZONTAL );
				}
				
				// free filter
				{
					D2FilterSingleFreeHandler lFree = new D2FilterSingleFreeHandler(iFilterHandler, lFilterFactory.getItemQueryFactory());
					if ( i == 1 || i == 2)
					{
						lFree.setSelectedIndex( i );
					}
					
					lFreeFilter.addToPanel( lFree.getDisplay(), 1, lFreeY, 1, RandallPanel.HORIZONTAL );
				}
				lFreeY++;
			}
			
			iDisplay.addToPanel( lFreeFilter, 0, lY, 1, RandallPanel.HORIZONTAL );
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
	
}
