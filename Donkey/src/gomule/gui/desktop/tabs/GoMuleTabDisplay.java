package gomule.gui.desktop.tabs;

import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JComponent;

import randall.util.RandallPanel;

public class GoMuleTabDisplay extends RandallPanel
{
	public static final int						TAB_LIMIT		= 300;
	private boolean								iTabComponentsFree[];
	private GoMuleTabTitleBar					iTitleBar;
	private RandallPanel						iComponents;
	private JComponent							iDesktopPane;
	private ArrayList							iTabs			= new ArrayList();
	private boolean								iIgnoreTabClose	= false;

	public GoMuleTabDisplay()
	{
		iTabComponentsFree = new boolean[TAB_LIMIT];
		for ( int i = 0; i < TAB_LIMIT; i++ )
		{
			iTabComponentsFree[i] = true;
		}
		init();
	}

	private void init()
	{
		setMargin( 0 );
		setSubPanel();
		setLayout( new GridBagLayout() );
		iTitleBar = new GoMuleTabTitleBar();
		iComponents = new RandallPanel();
		iComponents.setMargin( 0 );
		addToPanel( iTitleBar, 10, 10, 1, HORIZONTAL );
		addToPanel( iComponents, 10, 20, 1, BOTH );
		iDesktopPane = new RandallPanel();
		iComponents.addToPanel( iDesktopPane, 0, 0, 1, BOTH );
//		iTitleBar.setVisible( false );
	}

	/*
	 * add the given tab
	 */
	public synchronized void addTab( String pTitle, JComponent pTab )
	{
		int lFirstFreeIndex = -1;
		for ( int i = 1; lFirstFreeIndex == -1 && i < iTabComponentsFree.length; i++ )
		{
			if ( iTabComponentsFree[i] )
			{
				lFirstFreeIndex = i;
				iTabComponentsFree[i] = false;
			}
		}
		// System.err.println("addTab(): Index " + lFirstFreeIndex);
		GoMuleTabElement lNewTab = new GoMuleTabElement( this, pTitle, pTab, lFirstFreeIndex );
		iTabs.add( lNewTab );
		addTitle( lNewTab );
		addTabComponent( lNewTab );
		setSelectedComponent(lNewTab);
		validate();
		repaint();
	}
	
	public void setTabTitle(JComponent pTab, String pNewTitle )
	{
		for ( int i = 0 ; i < iTabs.size() ; i++ )
		{
			GoMuleTabElement lTab = (GoMuleTabElement) iTabs.get(i);
			if ( lTab.getTabComponent() == pTab )
			{
				lTab.setTabTitle(pNewTitle);
			}
		}
	}

	private synchronized void addTitle( GoMuleTabElement pTab )
	{
		iTitleBar.addTitle( pTab );
	}

	private void addTabComponent( GoMuleTabElement pTab )
	{
		pTab.getTabComponent().setVisible( false );
		iComponents.addToPanel( pTab.getTabComponent(), pTab.getTabIndex(), 0, 1, BOTH );
	}

	/*
	 * activate the given tab
	 */
	public void setSelectedComponent( JComponent pTab )
	{
		GoMuleTabElement lFound = null;
		for ( int i = 0; i < iTabs.size(); i++ )
		{
			GoMuleTabElement lTab = (GoMuleTabElement) iTabs.get( i );
			if ( lTab.getTabComponent() == pTab )
			{
				lFound = lTab;
			}
		}
		if ( lFound != null )
		{
			setSelectedComponent( lFound );
		}
	}

	private void setSelectedComponent( GoMuleTabElement pTab )
	{
		if ( iDesktopPane.isVisible() )
		{
			iDesktopPane.setVisible( false );
//			iTitleBar.setVisible( true );
		}
		if ( pTab.getTabComponent().isVisible() )
		{
			return;
		}
		for ( int i = 0; i < iTabs.size(); i++ )
		{
			GoMuleTabElement lTab = (GoMuleTabElement) iTabs.get( i );
			if ( lTab != pTab )
			{
				if ( lTab.getTabComponent().isVisible() )
				{
					lTab.getTabComponent().setVisible( false );
					lTab.deactivate();
				}
			}
		}
		for ( int i = 0; i < iTabs.size(); i++ )
		{
			GoMuleTabElement lTab = (GoMuleTabElement) iTabs.get( i );
			if ( lTab == pTab )
			{
				iTitleBar.activateTitle( lTab.getTitleComponent() );
				lTab.getTabComponent().setVisible( true );
				lTab.activate();
				tabActivated( lTab.getTabComponent() );
			}
		}
	}
	
	public void tabActivated( JComponent pTab )
	{
		// nothing
	}

	/**
	 * close all tabs
	 */
	public void closeAllTabs()
	{
		ArrayList lTabElementList = new ArrayList( iTabs );
		
		ArrayList lTabs = new ArrayList();
		for ( int i = 0; i < lTabElementList.size(); i++ )
		{
			GoMuleTabElement lTabElement = (GoMuleTabElement) lTabElementList.get( i );
			closeTab( lTabElement.getTabComponent() );
		}
	}

	private int findIndex( JComponent pTab )
	{
		for ( int i = 0; i < iTabs.size(); i++ )
		{
			GoMuleTabElement lTab = (GoMuleTabElement) iTabs.get( i );
			if ( lTab.getTabComponent() == pTab )
			{
				return i;
			}
		}

		return -1;
	}

	public void requestTabClose( JComponent pTab )
	{
		closeTab( pTab );
	}

	/*
	 * close the given tab
	 */
	public synchronized void closeTab( JComponent pTab )
	{
		if ( iIgnoreTabClose )
		{
			return;
		}

		try
		{
			iIgnoreTabClose = true;

			int lFoundIndex = findIndex( pTab );
			if ( lFoundIndex != -1 )
			{
				boolean lWasVisible = pTab.isVisible();
				GoMuleTabElement lTab = (GoMuleTabElement) iTabs.get( lFoundIndex );

				// Then allow close
				lTab.close();

				// and then remove from tab
				iTabs.remove( lFoundIndex );
				iTitleBar.removeTitle( lTab.getTitleComponent() );
				iComponents.remove( lTab.getTabComponent() );
				lTab.getTabComponent().setVisible( false );
				iTabComponentsFree[lTab.getTabIndex()] = true;
				if ( iTabs.size() == 0 )
				{
					iDesktopPane.setVisible( true );
//					iTitleBar.setVisible( false );
				}
				else if ( lWasVisible )
				{
					GoMuleTabElement lNewElement = iTitleBar.getFirstTitle();
					if ( lNewElement != null )
					{
						setSelectedComponent( lNewElement );
					}
				}
			}
		}
		catch ( Exception pEx )
		{
			pEx.printStackTrace();
		}
		catch ( Throwable pEx2 )
		{
			pEx2.printStackTrace();
		}
		finally
		{
			iIgnoreTabClose = false;
		}
		validate();
		repaint();
	}

}
