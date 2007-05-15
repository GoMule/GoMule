/*
 * Created on 5-mrt-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gomule.util;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
 * @author Marco
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class D2CellStringRenderer extends DefaultTableCellRenderer
{
    public D2CellStringRenderer()
    {
    }

    public Component getTableCellRendererComponent(JTable pTable, Object pValue, boolean pIsSelected, boolean pHasFocus, int pRow, int pColumn)
    {
        Object lValue;
        Color lForeground = null;
        
        if ( pValue instanceof D2CellValue )
        {
            lValue = ((D2CellValue) pValue).getValue();
            lForeground = ((D2CellValue) pValue).getForeground();
        }
        else
        {
            lValue = pValue;
        }

        Component lRenderer = super.getTableCellRendererComponent(pTable, lValue, pIsSelected, pHasFocus, pRow, pColumn);
        
        if ( lForeground != null )
        {
            lRenderer.setForeground(lForeground);
        }
        else
        {
            lRenderer.setForeground(Color.black);
        }

        return lRenderer;
    }

}