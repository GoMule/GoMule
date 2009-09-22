package gomule.item.filter;

abstract public class D2ItemFilterDefault implements D2ItemFilter
{
	private String	iFilterName;
	
	public D2ItemFilterDefault(String pFilterName)
	{
		iFilterName = pFilterName;
	}
	
	public String getDisplayString()
	{
		return iFilterName;
	}
	
	public String toString()
	{
		return getDisplayString();
	}
}
