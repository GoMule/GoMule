package gomule.item.filter;

import java.util.*;

/**
 * general item filter factory
 * @author Marco
 *
 */
public interface D2ItemFilterFactory
{
	public ArrayList	getItemQualities();
	public D2ItemFilter getEthereal();
	
	public ArrayList	getItemTypes();
	
	public ArrayList	getItemCategories(int pLevel, D2ItemFilter pFilter);
	
	public ArrayList	getItemQueryFactory();
}
