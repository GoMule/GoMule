package gomule.item.filter.free;

import java.util.*;

public class D2ItemFilterQueryFactoryInteger extends D2ItemFilterQueryFactoryDefault
{

	public D2ItemFilterQueryFactoryInteger( D2ItemFilterQueryFactoryProperty pProperty )
	{
		super( pProperty );
	}

	public Class getDataType()
	{
		return Integer.class;
	}
	
	public Object getDataValue( String pValue )
	{
		try
		{
			return new Integer( pValue );
		}
		catch ( NumberFormatException pEx )
		{
			// not an integer
			return null;
		}
	}

	public ArrayList getOperator()
	{
		ArrayList lReturn = new ArrayList();
		
		lReturn.add( new D2ItemFilterQueryOperatorSmaller() );
		lReturn.add( new D2ItemFilterQueryOperatorGreater() );
		lReturn.add( new D2ItemFilterQueryOperatorEqual() );
		
		return lReturn;
	}
	
//	class D2QueryItemFilterLevel extends D2QueryItemFilter
//	{
//		public D2QueryItemFilterLevel( String pFilterName, D2ItemFilterQueryOperator pOperation, Object pValue )
//		{
//			super( pFilterName, pOperation, pValue );
//		}
//
//		Object getItemValue( D2FilterableItem pItem )
//		{
//			return new Integer(pItem.getReqLvl());
//		}
//		
//	}
//
}
