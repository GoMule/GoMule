package gomule.item.filter;

import gomule.item.*;


abstract public class D2ItemFilterQueryFactoryDefault implements D2ItemFilterQueryFactory
{

	abstract class D2QueryItemFilter extends D2ItemFilterDefault
	{
		private D2ItemFilterQueryOperator	iOperation;
		private Object						iValue;

		public D2QueryItemFilter( String pFilterName, D2ItemFilterQueryOperator pOperation, Object pValue )
		{
			super( pFilterName );
			iOperation = pOperation;
			iValue = pValue;
		}

		public boolean isFilterItem( D2FilterableItem pItem )
		{
			// TODO Auto-generated method stub
			return iOperation.isAllowed( getItemValue(pItem), iValue );
		}
		
		abstract Object getItemValue( D2FilterableItem pItem );
		
	}
}
