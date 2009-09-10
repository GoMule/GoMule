package gomule.gui.desktop.tabs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.border.EmptyBorder;

public class GoMuleTabBorder extends EmptyBorder
{
	public static final int		BORDER_ACTIVE = 0;
	public static final int		BORDER_DEACTIVE = 1;
	public static final int		BORDER_DOWN = 2;
	
	private static final int sTop[]    = new int[] { 2, 4, 5 };
	private static final int sLeft[]   = new int[] { 1, 1, 0 };
	private static final int sBottom[] = new int[] { 4, 2, 1 };
	private static final int sRight[]  = new int[] { 3, 3, 0 };
	
	private int iStatus;

	public GoMuleTabBorder(int pStatus)
	{
		super(sTop[pStatus], sLeft[pStatus], sBottom[pStatus], sRight[pStatus]);
		iStatus = pStatus;
	}
	
    public void paintBorder(Component pComponent, Graphics pGraphics, int x, int y, int width, int height)
    {
    	Graphics2D g = (Graphics2D) pGraphics;
        Color lOldColor = g.getColor();

        switch(iStatus)
        {
        case BORDER_ACTIVE:
//        	g.setColor( Color.black );
//        	g.drawPolyline(new int[] {x, x, x+1}, new int[] {y+1, y, y }, 3);
//        	g.drawPolyline(new int[] {width-1, width-1, width-2}, new int[] {y+1, y, y }, 3);

        	g.setColor(Color.white);
        	g.drawPolyline(new int[] {x, x ,x+2, width-3}, new int[] {height-2, y+2, y, y }, 4);
        	
        	g.setColor(new Color(99,97,99));
        	g.drawPolyline(new int[] {width-2, width-1, width-1}, new int[] {y+1, y+2, height-2 }, 3);
        	
        	g.setColor(new Color(132,130,132));
        	g.drawLine(width-2,y+2,width-2,height-2);

        	break;
        case BORDER_DEACTIVE:
//        	g.setColor( Color.black );
//        	g.fillRect(x,y,width,2);
//        	g.drawPolyline(new int[] {x, x, x+1}, new int[] {y+3, y+2, y+2 }, 3);
//        	g.drawPolyline(new int[] {width-1, width-1, width-2}, new int[] {y+3, y+2, y+2 }, 3);

        	g.setColor(Color.white);
        	g.drawPolyline(new int[] {width-1, x, x ,x+2, width-3}, new int[] {height-1, height-1, y+4, y+2, y+2 }, 5);
        	
        	g.setColor(new Color(99,97,99));
        	g.drawPolyline(new int[] {width-2, width-1, width-1}, new int[] {y+3, y+4, height-2 }, 3);
        	
        	g.setColor(new Color(132,130,132));
        	g.drawLine(width-2,y+4,width-2,height-2);

        	break;
        case BORDER_DOWN:
            g.setColor(Color.white);
        	g.drawLine(x,height-1,width-1,height-1);
        	break;
        }
    	
    	g.setColor(lOldColor);
    }
}
