package gomule.item.filter.free;

public class D2ItemFilterQueryOperatorEqual implements D2ItemFilterQueryOperator
{

	public boolean isAllowed( Object pItemValue, Object pOperationValue )
	{
		if ( pItemValue == null && pOperationValue == null )
		{
			return true;
		}
		if ( pItemValue == null || pOperationValue == null )
		{
			return false;
		}
		if ( pItemValue.getClass() != pOperationValue.getClass() )
		{
			return false;
		}
		if ( pItemValue instanceof Comparable )
		{
			return ((Comparable) pItemValue).compareTo( pOperationValue ) == 0;
		}
		
		return false;
	}
	
	public String toString()
	{
		return "equals";
	}
	
}