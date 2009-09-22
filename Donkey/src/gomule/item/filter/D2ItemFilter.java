package gomule.item.filter;

import gomule.item.*;

/**
 * anz item filter
 * @author Marco
 *
 */
public interface D2ItemFilter
{
	public String	getDisplayString();
	public boolean	isFilterItem(D2FilterableItem pItem);
}
