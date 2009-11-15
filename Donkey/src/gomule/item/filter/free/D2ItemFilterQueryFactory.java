package gomule.item.filter.free;

import gomule.item.filter.*;

import java.util.*;

/**
 * item filter factory based upon operator and value
 * @author Marco
 *
 */
public interface D2ItemFilterQueryFactory
{
	/**
	 * name of the factory / filter
	 * @return
	 */
	public String getName();
	
	/**
	 * list of operators ( D2ItemFilterQueryOperator )
	 * @return
	 */
	public ArrayList getOperator();
	
	/**
	 * data entry type
	 * @return
	 */
	public Class getDataType();
	
	/**
	 * convert "String" input from textfield to real value. Invalid values convert to null
	 * @param pValue
	 * @return
	 */
	public Object getDataValue(String pValue);
	
	/**
	 * data filter
	 * @param pOperator
	 * @param pValue
	 * @return
	 */
	public D2ItemFilter getFilter(D2ItemFilterQueryOperator pOperator, Object pValue);
}
