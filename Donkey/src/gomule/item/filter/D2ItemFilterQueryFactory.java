package gomule.item.filter;

import java.util.*;

/**
 * item filter factory based upon operator and value
 * @author Marco
 *
 */
public interface D2ItemFilterQueryFactory
{
	/**
	 * list of operators (
	 * @return
	 */
	public ArrayList getOperator();
	
	/**
	 * data entry type
	 * @return
	 */
	public Class getDataType();
	
	/**
	 * data filter
	 * @param pOperator
	 * @param pValue
	 * @return
	 */
	public D2ItemFilter getFilter(D2ItemFilterQueryOperator pOperator, Object pValue);
}
