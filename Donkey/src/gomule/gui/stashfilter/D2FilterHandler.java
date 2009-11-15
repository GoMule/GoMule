package gomule.gui.stashfilter;

import gomule.gui.*;
import gomule.item.filter.*;

/**
 * class in controll of all active filters
 * @author Marco
 *
 */
public class D2FilterHandler
{
	private D2ItemFilterComposite	iFilterList;
	private D2ItemFilterComposite	iFilterTypeList;
	private D2ItemFilterComposite	iFilterQualityList;
	private D2ItemFilterComposite	iFilterEtherealList;
	private D2ItemFilterComposite	iFilterCategories1List;
	private D2ItemFilterComposite	iFilterCategories2List;
	private D2ItemFilterComposite	iFilterCategories3List;
	private D2ItemFilterComposite	iFilterCategories4List;
	private D2ItemFilterComposite	iFilterTextSearchList;
	
	private D2ViewStash				iViewStash;
	
	public D2FilterHandler(D2ViewStash pViewStash)
	{
		iViewStash = pViewStash;
		
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

		iFilterTextSearchList = new D2ItemFilterComposite();
		iFilterTextSearchList.setAnd( true );
		
		iFilterList.addFilter( iFilterTypeList );
		iFilterList.addFilter( iFilterQualityList );
		iFilterList.addFilter( iFilterEtherealList );
		iFilterList.addFilter( iFilterCategories1List );
		iFilterList.addFilter( iFilterCategories2List );
		iFilterList.addFilter( iFilterCategories3List );
		iFilterList.addFilter( iFilterCategories4List );
		iFilterList.addFilter( iFilterTextSearchList );
	}
	
	public D2ItemFilter getItemFilter()
	{
		return iFilterList;
	}
	
	private D2ItemFilterComposite getComposite(Object pFilterType)
	{
		if ( pFilterType == D2ViewStashFilterNew.TYPE )
		{
			return iFilterTypeList;
		}
		else if ( pFilterType == D2ViewStashFilterNew.QUALITY )
		{
			return iFilterQualityList;
		}
		else if ( pFilterType == D2ViewStashFilterNew.ETHEREAL )
		{
			return iFilterEtherealList;
		}
		else if ( pFilterType == D2ViewStashFilterNew.CATEGORIES_1 )
		{
			return iFilterCategories1List;
		}
		else if ( pFilterType == D2ViewStashFilterNew.CATEGORIES_2 )
		{
			return iFilterCategories2List;
		}
		else if ( pFilterType == D2ViewStashFilterNew.CATEGORIES_3 )
		{
			return iFilterCategories3List;
		}
		else if ( pFilterType == D2ViewStashFilterNew.CATEGORIES_4 )
		{
			return iFilterCategories4List;
		}
		else if ( pFilterType == D2ViewStashFilterNew.TEXT_FILTER )
		{
			return iFilterTextSearchList;
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