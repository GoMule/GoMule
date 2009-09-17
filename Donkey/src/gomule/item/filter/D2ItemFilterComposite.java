package gomule.item.filter;

import gomule.item.*;

import java.util.*;

/**
 * multiple filters combined with and/or
 * @author Marco
 *
 */
public class D2ItemFilterComposite implements D2ItemFilter
{
	private ArrayList	iFilterList;
	private boolean		iAnd;
	
	public D2ItemFilterComposite()
	{
		iFilterList = new ArrayList();
		iAnd = true;
	}
	
	public void addFilter(D2ItemFilter pFilter)
	{
		iFilterList.add( pFilter );
	}
	
	public void removeFilter(D2ItemFilter pFilter)
	{
		iFilterList.remove( pFilter );
	}
	
	public String getDisplayString()
	{
		if ( iFilterList.isEmpty() )
		{
			return "No Filter";
		}
		StringBuilder lBuilder = new StringBuilder();
		lBuilder.append( ((D2ItemFilter) iFilterList.get( 0 )).getDisplayString() );
		for ( int i = 1 ; i < iFilterList.size() ; i++ )
		{
			if ( isAnd() )
			{
				lBuilder.append( " and " );
			}
			else
			{
				lBuilder.append( " or " );
			}
			lBuilder.append( ((D2ItemFilter) iFilterList.get( 0 )).getDisplayString() );
		}
		return null;
	}

	public boolean isFilterItem( D2Item pItem )
	{
		if ( iFilterList.isEmpty() )
		{
			// if no filter items, it is always ok
			return true;
		}
		
		if ( isAnd() )
		{
			// and
			for ( int i = 0 ; i < iFilterList.size() ; i++ )
			{
				if ( !((D2ItemFilter) iFilterList.get( i )).isFilterItem( pItem ) )
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			// or
			for ( int i = 0 ; i < iFilterList.size() ; i++ )
			{
				if ( ((D2ItemFilter) iFilterList.get( i )).isFilterItem( pItem ) )
				{
					return true;
				}
			}
			return false;
		}
	}

	public boolean isAnd()
	{
		return iAnd;
	}

	public void setAnd( boolean pAnd )
	{
		iAnd = pAnd;
	}

}
