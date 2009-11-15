package gomule.item.filter.free;

import gomule.item.*;
import gomule.item.filter.*;

/**
 * abstract filter factory class.
 * defines how name is and how it's value is retrieved
 * 
 * @author Marco
 *
 */
abstract public class D2ItemFilterQueryFactoryDefault implements D2ItemFilterQueryFactory
{
	protected final D2ItemFilterQueryFactoryProperty	iProperty;
	
	public D2ItemFilterQueryFactoryDefault(D2ItemFilterQueryFactoryProperty pProperty)
	{
		iProperty = pProperty;
	}
	
	public String getName()
	{
		return iProperty.getName();
	}
	
	public String toString()
	{
		return getName();
	}
	
	public D2ItemFilter getFilter( D2ItemFilterQueryOperator pOperator, Object pFilterValue )
	{
		return new D2QueryItemFilter(pOperator, pFilterValue);
	}

	class D2QueryItemFilter extends D2ItemFilterDefault
	{
		private final D2ItemFilterQueryOperator	iOperation;
		private final Object						iFilterValue;

		public D2QueryItemFilter( D2ItemFilterQueryOperator pOperation, Object pFilterValue )
		{
			super( iProperty.getName() );
			iOperation = pOperation;
			iFilterValue = pFilterValue;
		}

		public boolean isFilterItem( D2FilterableItem pItem )
		{
			return iOperation.isAllowed( getItemValue(pItem), iFilterValue );
		}
		
		public Object getItemValue( D2FilterableItem pItem )
		{
			return iProperty.getValue(pItem);
		}
		
	}
}
