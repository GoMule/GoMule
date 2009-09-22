package gomule.item.filter;

import gomule.item.*;

import java.util.*;

public class D2ItemFilterQueryFactoryInteger extends D2ItemFilterQueryFactoryDefault
{

	public Class getDataType()
	{
		return Integer.class;
	}

	public ArrayList getOperator()
	{
		ArrayList lReturn = new ArrayList();
		
//		D2ItemFilterQueryOperator 
		
		return null;
	}

	public D2ItemFilter getFilter( D2ItemFilterQueryOperator pOperator, Object pValue )
	{
		return new D2QueryItemFilterLevel( "Requerement Lvl", pOperator, pValue);
	}
	
	class D2QueryItemFilterLevel extends D2QueryItemFilter
	{
		public D2QueryItemFilterLevel( String pFilterName, D2ItemFilterQueryOperator pOperation, Object pValue )
		{
			super( pFilterName, pOperation, pValue );
		}

		Object getItemValue( D2FilterableItem pItem )
		{
			return new Integer(pItem.getReqLvl());
		}
		
	}

}
