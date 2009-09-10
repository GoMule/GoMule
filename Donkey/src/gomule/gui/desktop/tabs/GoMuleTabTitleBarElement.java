package gomule.gui.desktop.tabs;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;

public class GoMuleTabTitleBarElement 
{
	private ComponentListener		iComponentListener;
	private GoMuleTabElement		iTitle;
	private int						iWidth;
	private int						iHeight;

	public GoMuleTabTitleBarElement( GoMuleTabElement pTitle )
	{
		iTitle = pTitle;
		checkSize();
		iComponentListener = new ComponentAdapter()
		{
			public void componentResized( ComponentEvent e )
			{
				checkSize();
			}
		};
		iTitle.getTitleComponent().addComponentListener( iComponentListener );
	}

	public void checkSize()
	{
		Rectangle lBounds = iTitle.getTitleComponent().getBounds();
		if ( lBounds.width != 0 && lBounds.height != 0 )
		{
			iWidth = lBounds.width;
			iHeight = lBounds.height;
		}
		else
		{
			Dimension lPreferred = iTitle.getTitleComponent().getPreferredSize();
			iWidth = lPreferred.width + 6;
			iHeight = lPreferred.height + 6;
		}
		// System.err.println("Size: " + iWidth + ", " + iHeight );
	}

	public GoMuleTabElement getGoMuleTabElement()
	{
		return iTitle;
	}

	public JComponent getComponent()
	{
		return iTitle.getTitleComponent();
	}

	public void setSelectedComponent()
	{
		iTitle.setSelectedComponent();
	}

	public String getTitleText()
	{
		return iTitle.getTitle();
	}

	public int getWidth()
	{
		return iWidth;
	}

	public int getHeight()
	{
		return iHeight;
	}

	public void cleanUp()
	{
		if ( iTitle != null && iComponentListener != null )
		{
			iTitle.getTitleComponent().removeComponentListener( iComponentListener );
		}
		iTitle = null;
		iComponentListener = null;
	}

}
