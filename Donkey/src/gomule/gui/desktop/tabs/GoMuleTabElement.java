package gomule.gui.desktop.tabs;

import gomule.gui.D2ImageCache;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;

import randall.util.RandallPanel;

public class GoMuleTabElement 
{
	private static Color sActivatedBackgroundColor;
	private static Color sDeactivatedBackgroundColor;
	private static Color sActivatedBackgroundColor1;
	private static Color sActivatedBackgroundColor2;
	private static Color sForegroundColor;
	private static Color sForegroundColorSelected;
	private static Color sForegroundColorMouseOver;
	private static Border sActivatedBorder;
	private static Border sDeactivatedBorder;
	
	static {
		setColors();
	}

	private String iTitle;
	private TitleComponent iTitleComponent;
	private JLabel iTitleLabel;
	private JLabel iTitleCloseButton;
	private MouseListener	iTitleSelectListener;
	private MouseListener	iTitleCloseListener;
	private int	iTabTitleIndex;

	protected GoMuleTabDisplay iDisplaytab;
	protected JComponent iTabComponent;
	
	public GoMuleTabElement(GoMuleTabDisplay pDisplaytab, String pTitle, JComponent pTab, int pTabIndex)
	{
		iDisplaytab = pDisplaytab;
		iTitle = pTitle;
		iTabComponent = pTab;
		iTabTitleIndex = pTabIndex;
		
		pTab.setName(pTitle);
		
		iTitleComponent = new TitleComponent(); 
		iTitleLabel = new JLabel(iTitle);
		Icon lIcon;
		lIcon = D2ImageCache.getIcon("TabEmpty.gif");

		iTitleLabel.setIcon(lIcon);
		iTitleLabel.setForeground(sForegroundColorSelected);
		iTitleCloseButton = new JLabel(D2ImageCache.getIcon("TabClose.gif"));
		iTitleComponent.addToPanel(iTitleLabel,0,0,1,RandallPanel.NONE);
		iTitleComponent.addToPanel(iTitleCloseButton,1,0,1,RandallPanel.NONE);

		iTitleSelectListener = new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e)
		    {
		    	if ( e.getClickCount() == 1 )
		    	{
			    	if ( e.getButton() == MouseEvent.BUTTON1 )
			    	{
			    		setSelectedComponent();
			    	}
		    	}
		    }
			public void mouseEntered(MouseEvent e) 
		    {
		    	if ( !iTabComponent.isVisible() )
		    	{
		    		iTitleLabel.setForeground(sForegroundColorMouseOver);
		    	}
		    }
			public void mouseExited(MouseEvent e) 
		    {
		    	if ( !iTabComponent.isVisible() )
		    	{
		    		iTitleLabel.setForeground(sForegroundColor);
		    	}
		    }
		};
		iTitleCloseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
		    {
		    	if ( e.getClickCount() == 1 )
		    	{
			    	if ( e.getButton() == MouseEvent.BUTTON1 )
			    	{
			    		buttonCloseTabClick();
			    	}
		    	}
		    }
			public void mouseEntered(MouseEvent e) 
		    {
				iTitleCloseButton.setIcon(D2ImageCache.getIcon("TabCloseSelected.gif"));
		    }
			public void mouseExited(MouseEvent e) 
		    {
				iTitleCloseButton.setIcon(D2ImageCache.getIcon("TabClose.gif"));
		    }
		};
		
		iTitleComponent.addMouseListener(iTitleSelectListener);
		iTitleCloseButton.addMouseListener(iTitleCloseListener);
	}
	
	public void setTabTitle(String pNewTitle)
	{
		iTitleLabel.setText(pNewTitle);
	}
	
    protected static void setColors()
    {
    	sActivatedBackgroundColor = Color.green;
    	sDeactivatedBackgroundColor = Color.lightGray;

    	sActivatedBackgroundColor1 = sActivatedBackgroundColor.brighter().brighter();

    	sActivatedBackgroundColor2 = sActivatedBackgroundColor;

    	sForegroundColor = Color.black;
    	sForegroundColorSelected = Color.black;

    	sForegroundColorMouseOver = Color.red;
    	
    	sActivatedBorder = new GoMuleTabBorder(GoMuleTabBorder.BORDER_ACTIVE);
    	sDeactivatedBorder = new GoMuleTabBorder(GoMuleTabBorder.BORDER_DEACTIVE);
    }
    
	public void setSelectedComponent()
	{
    	if ( !iTabComponent.isVisible() )
    	{
    		iDisplaytab.setSelectedComponent(iTabComponent);
    	}
	}
	
	public void activate()
	{
		iTitleComponent.activate();
		iTitleLabel.setForeground(sForegroundColorSelected);
	}
	
	public void deactivate()
	{
		iTitleLabel.setForeground(sForegroundColor);
		iTitleComponent.deactivate();
	}
	
	public void close() throws Exception
	{
		// TODO ??
	}
	
	public String getTitle()
	{
		return iTitle;
	}
	
	public JComponent getTitleComponent()
	{
		return iTitleComponent;
	}
	public JComponent getTabComponent()
	{
		return iTabComponent;
	}
	public int getTabIndex()
	{
		return iTabTitleIndex;
	}
	
	class TitleComponent extends RandallPanel
	{
		public TitleComponent()
		{
	        setBackground( sDeactivatedBackgroundColor );
	        setLayout( new GridBagLayout() );
	        setSubPanel();
		}
		
		public void activate()
		{
			iTitleComponent.setBorder(sActivatedBorder);
			setOpaque(false);
		}
		
		public void deactivate()
		{
			iTitleComponent.setBorder(sDeactivatedBorder);
			setOpaque(true);
		}

		public void paint(Graphics pGraphics)
		{
			if ( !isOpaque() )
			{
				Graphics2D g = (Graphics2D) pGraphics;
				
				Rectangle lRectangle = TitleComponent.this.getBounds();
				
				g.setPaint(new GradientPaint(0,0,sActivatedBackgroundColor1,0,lRectangle.height,sActivatedBackgroundColor2));
				g.fillRect(0,0,lRectangle.width,lRectangle.height);
			}
			
			super.paint(pGraphics);
		}
	}
	
	protected void buttonCloseTabClick()
	{
		iDisplaytab.requestTabClose(iTabComponent);
	}
	
}
