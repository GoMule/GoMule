package gomule.gui.desktop.tabs;

import gomule.gui.D2ImageCache;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import randall.util.RandallPanel;

public class GoMuleTabTitleBar extends RandallPanel
{
	private ArrayList			iTitleBar = new ArrayList();
	private ArrayList			iTitleBarAccessList = new ArrayList();
	private JLabel				iTabs;

	public GoMuleTabTitleBar()
	{
		init();
	}
	
	private void init()
	{
		setLayout(new GridBagLayout());
		setSubPanel();
		setMargin(0);
		
		JLabel lDummy1 = new JLabel();
		Dimension lSize = new Dimension(50,25);
		lDummy1.setBorder(new GoMuleTabBorder(GoMuleTabBorder.BORDER_DOWN));
		addToPanel(lDummy1, 0, 0, 1, NONE);
		JLabel lDummy2 = new JLabel();
		lDummy2.setBorder(new GoMuleTabBorder(GoMuleTabBorder.BORDER_DOWN));
		
		addToPanel(lDummy2, GoMuleTabDisplay.TAB_LIMIT+5, 0, 1, HORIZONTAL);
		iTabs = new JLabel("");
		iTabs.setIcon(D2ImageCache.getIcon("TabMenuDown.gif"));
		iTabs.setVisible(false);
		
		iTabs.setBorder(new GoMuleTabBorder(GoMuleTabBorder.BORDER_DOWN));

		setMinimumSize(lSize);
		setPreferredSize(lSize);
		setSize(lSize);
		
		iTabs.setBorder(new GoMuleTabBorder(GoMuleTabBorder.BORDER_DOWN));
		iTabs.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) 
			{
				JPopupMenu lPopupMenu = new JPopupMenu();
				for ( int i = 0 ; i < iTitleBar.size() ; i++ )
				{
					GoMuleTabTitleBarElement lElement = (GoMuleTabTitleBarElement) iTitleBar.get(i);
					if ( !lElement.getComponent().isVisible() )
					{
						JMenuItem lMenuItem = new JMenuItem( lElement.getTitleText() );
						lMenuItem.addActionListener( new GoMuleMenuShowElement(lElement) );
						lPopupMenu.add( lMenuItem );
					}
				}
				lPopupMenu.show(iTabs,0,0);
			}
			public void mouseEntered(MouseEvent e) 
		    {
		    	iTabs.setForeground( Color.red );
		    	iTabs.setIcon( D2ImageCache.getIcon("TabMenuDownSelected.gif") );
		    }
			
			public void mouseExited(MouseEvent e)
		    {
		    	iTabs.setForeground(Color.black);
		    	iTabs.setIcon( D2ImageCache.getIcon("TabMenuDown.gif") );
		    }
	
		});
		addToPanel(iTabs, GoMuleTabDisplay.TAB_LIMIT+6, 0, 1, NONE);
//		add(lDummy);
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e) 
			{
				checkTitles();
			}
		});
	}
	
	public void activateTitle(JComponent pTitle)
	{
		GoMuleTabTitleBarElement lElement = findTitleBarElement(pTitle);
		if ( lElement != null )
		{
			iTitleBarAccessList.remove(lElement);
			iTitleBarAccessList.add(0, lElement);
			if ( !lElement.getComponent().isVisible() )
			{
				checkTitles();
			}
		}
	}
	
	public GoMuleTabElement getFirstTitle()
	{
		if ( iTitleBarAccessList != null && iTitleBarAccessList.size() > 0 )
		{
			return ((GoMuleTabTitleBarElement) iTitleBarAccessList.get(0)).getGoMuleTabElement();
		}
		return null;
	}
	
	public synchronized void addTitle(GoMuleTabElement pTitle)
	{
		GoMuleTabTitleBarElement lTitleElement = new GoMuleTabTitleBarElement(pTitle);
		iTitleBar.add(lTitleElement);
		iTitleBarAccessList.add(0, lTitleElement);
		addToPanel(lTitleElement.getComponent(), iTitleBar.size(), 0, 1, RandallPanel.NONE);
		checkTitles();
	}
	
	public synchronized void removeTitle(JComponent pTitle)
	{
		GoMuleTabTitleBarElement lElement = findTitleBarElement(pTitle);
		if ( lElement != null )
		{
			iTitleBar.remove(lElement);
			iTitleBarAccessList.remove(lElement);
		}
//		System.err.println("removeTitle(): " + pTitle.getBounds() + " - " + pTitle.getPreferredSize() );
		remove(pTitle);
		for ( int i = 0 ; i < iTitleBar.size() ; i++ )
		{
			GoMuleTabTitleBarElement lReorganize = (GoMuleTabTitleBarElement) iTitleBar.get(i);
			remove(lReorganize.getComponent());
			addToPanel(lReorganize.getComponent(), i+1, 0, 1, RandallPanel.NONE);
		}
		if ( pTitle.isVisible() )
		{
			checkTitles();
			pTitle.setVisible(false);
		}
		validate();
	}
	
	private GoMuleTabTitleBarElement findTitleBarElement(JComponent pTitle)
	{
		for ( int i = 0 ; i < iTitleBar.size() ; i++ )
		{
			GoMuleTabTitleBarElement lElement = (GoMuleTabTitleBarElement) iTitleBar.get(i);
			if ( lElement.getComponent() == pTitle )
			{
				return lElement;
			}
		}
		return null;
	}
	
	public void checkTitles()
	{
		Rectangle lBounds = getBounds();
		if ( lBounds.width == 0 )
		{
			return;
		}
//		System.err.println("checkTitles(): " + lBounds);
		boolean lFits = true;
		int lWidth = 0;
		for ( int i = 0 ; i < iTitleBarAccessList.size() ; i++ )
		{
			GoMuleTabTitleBarElement lElement = (GoMuleTabTitleBarElement) iTitleBarAccessList.get(i);
			if ( lWidth + lElement.getWidth() < lBounds.width )
			{
				lWidth += lElement.getWidth();
			}
			else
			{
				lFits = false;
			}
		}
		
		lWidth = 0;
		if ( lFits )
		{
			iTabs.setVisible(false);
		}
		else
		{
			lWidth += 50;
		}
		int lNotVisibleCount = 0;
		for ( int i = 0 ; i < iTitleBarAccessList.size() ; i++ )
		{
			GoMuleTabTitleBarElement lElement = (GoMuleTabTitleBarElement) iTitleBarAccessList.get(i);
			if ( lWidth + lElement.getWidth() < lBounds.width )
			{
				lWidth += lElement.getWidth();
				lElement.getComponent().setVisible(true);
			}
			else
			{
				lElement.getComponent().setVisible(false);
				lNotVisibleCount++;
			}
		}
		if ( !lFits )
		{
			iTabs.setText( Integer.toString(lNotVisibleCount) + "   " );
			iTabs.setVisible(true);
		}
	}
	
	class GoMuleMenuShowElement implements ActionListener
	{
		private GoMuleTabTitleBarElement iElement;
		
		public GoMuleMenuShowElement(GoMuleTabTitleBarElement pElement)
		{
			iElement = pElement;
		}
		public void actionPerformed(ActionEvent pEvent)
		{
			iElement.setSelectedComponent();
		}
	}

}
