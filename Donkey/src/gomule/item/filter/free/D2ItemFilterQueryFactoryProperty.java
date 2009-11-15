package gomule.item.filter.free;

import gomule.item.*;

abstract public class D2ItemFilterQueryFactoryProperty
{
	private String	iName;
	
	public D2ItemFilterQueryFactoryProperty(String pName)
	{
		iName = pName;
	}
	
	public String getName()
	{
		return iName;
	}
	
	public String toString()
	{
		return getName();
	}
	
	abstract public Object getValue(D2FilterableItem pItem);
}
