package gomule.gui.desktop.tabs;

import gomule.gui.D2ImageCache;
import gomule.gui.desktop.generic.GoMuleDesktop;
import gomule.gui.desktop.generic.GoMuleDesktopListener;
import gomule.gui.desktop.generic.GoMuleView;
import gomule.gui.desktop.generic.GoMuleViewChar;
import gomule.gui.desktop.generic.GoMuleViewDisplayHandler;
import gomule.gui.desktop.generic.GoMuleViewStash;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;

public class GoMuleDesktopTabs  implements GoMuleDesktop
{
	private GoMuleTabDisplay	iDesktop;
	private ArrayList			iOpenedViews;
	
	public GoMuleDesktopTabs()
	{
		iDesktop = new GoMuleTabDisplay()
		{
			public void requestTabClose(JComponent pTab) 
			{
				ArrayList lClone = new ArrayList( iOpenedViews );
				for ( int i = 0 ; i < lClone.size() ; i++ )
				{
					GoMuleView lView = (GoMuleView) lClone.get(i);
					if ( pTab == lView.getDisplay() )
					{
						((GoMuleViewTabDisplayHandler) lView.getDisplayHandler()).fireViewClosing(lView);
					}
				}
			}
			
			public void tabActivated(JComponent pTab) 
			{
				ArrayList lClone = new ArrayList( iOpenedViews );
				for ( int i = 0 ; i < lClone.size() ; i++ )
				{
					GoMuleView lView = (GoMuleView) lClone.get(i);
					if ( pTab == lView.getDisplay() )
					{
						((GoMuleViewTabDisplayHandler) lView.getDisplayHandler()).fireViewActivated(lView);
					}
				}
			}
		};
		iOpenedViews = new ArrayList();
	}

	public JComponent getDisplay() 
	{
		return iDesktop;
	}

	public void addView(GoMuleView pView) 
	{
		GoMuleViewDisplayHandler lDisplayHandler = new GoMuleViewTabDisplayHandler(pView);
		iOpenedViews.add(pView);
		String lTitle = null;
		if ( pView instanceof GoMuleViewChar )
		{
			lTitle = ((GoMuleViewChar) pView).getViewChar().getViewTitle();
		}
		else if ( pView instanceof GoMuleViewStash )
		{
			lTitle = ((GoMuleViewStash) pView).getViewStash().getViewTitle();
		}
		else
		{
			lTitle = pView.getItemContainer().getFileName();
		}
		
		iDesktop.addTab(lTitle, pView.getDisplay());
//		iDesktop.setSelectedComponent( pView.getDisplay() );
	}
	
	class GoMuleViewTabDisplayHandler implements GoMuleViewDisplayHandler
	{
		private ArrayList	iListenerList;
		private GoMuleView	iView;
		
		public GoMuleViewTabDisplayHandler(GoMuleView pView)
		{
			iView = pView;
			iView.setDisplayHandler(this);
			iListenerList = new ArrayList();
		}

		public void addDesktopListener(GoMuleDesktopListener pListener) 
		{
			iListenerList.add( pListener );
			pListener.viewActivated( iView );
		}
		
		public void fireViewClosing(GoMuleView pView)
		{
			for ( int i = 0 ; i < iListenerList.size() ; i++ )
			{
				GoMuleDesktopListener lListener = (GoMuleDesktopListener) iListenerList.get(i);
				lListener.viewClosing(pView);
			}
		}
		
		public void fireViewActivated(GoMuleView pView)
		{
			for ( int i = 0 ; i < iListenerList.size() ; i++ )
			{
				GoMuleDesktopListener lListener = (GoMuleDesktopListener) iListenerList.get(i);
				lListener.viewActivated(pView);
			}
		}
		
		protected Window getParentWindow()
		{
			Container lContainer = GoMuleDesktopTabs.this.getDisplay();
			
			while ( lContainer != null )
			{
				if ( lContainer instanceof Window )
				{
					return (Window) lContainer;
				}
				lContainer = lContainer.getParent();
			}
			return null;
		}

		public void setCursor(Cursor pCursor) 
		{
			Window lParent = getParentWindow();
			if ( lParent != null )
			{
				lParent.setCursor( pCursor );
			}
		}

		public void setTitle(String pNewTitle) 
		{
			ArrayList lClone = new ArrayList( iOpenedViews );
			for ( int i = 0 ; i < lClone.size() ; i++ )
			{
				GoMuleView lView = (GoMuleView) lClone.get(i);
				if ( iView == lView )
				{
					iDesktop.setTabTitle(lView.getDisplay(), pNewTitle);
				}
			}
		}
		
		public void setEdited(boolean pEdited) 
		{
			ArrayList lClone = new ArrayList( iOpenedViews );
			for ( int i = 0 ; i < lClone.size() ; i++ )
			{
				GoMuleView lView = (GoMuleView) lClone.get(i);
				if ( iView == lView )
				{
					if ( pEdited )
					{
						iDesktop.setTabIcon(lView.getDisplay(), D2ImageCache.getIcon("TabEdit.gif") );
					}
					else
					{
						iDesktop.setTabIcon(lView.getDisplay(), D2ImageCache.getIcon("TabEmpty.gif") );
					}
				}
			}
		}
		
	}
	
//	protected int getIndex(GoMuleView pView)
//	{
//		for ( int lComp = 0 ; lComp < iDesktop.getComponentCount() ; lComp++ )
//		{
//			if ( iDesktop.getComponent(lComp) == pView.getDisplay() )
//			{
//				return lComp;
//			}
//		}
//		return -1;
//	}
	
	public void showView(GoMuleView pView) 
	{
		iDesktop.setSelectedComponent( pView.getDisplay() );
	}

	public void closeView(String pFileName) 
	{
		ArrayList lClone = new ArrayList( iOpenedViews );
		for ( int i = 0 ; i < lClone.size() ; i++ )
		{
			GoMuleView lView = (GoMuleView) lClone.get(i);
			if ( lView.getItemContainer().getFileName().equals(pFileName))
			{
				((GoMuleViewTabDisplayHandler) lView.getDisplayHandler()).fireViewClosing(lView);
//				lView.getItemContainer().closeView();
//				iDesktop.closeTab( lView.getDisplay() );
			}
		}
		// not yet implemented
//		iDesktop.closeTab( pView.getdi)
	}

	public void closeViewAll() 
	{
		ArrayList lClone = new ArrayList( iOpenedViews );
		for ( int i = 0 ; i < lClone.size() ; i++ )
		{
			GoMuleView lView = (GoMuleView) lClone.get(i);
			((GoMuleViewTabDisplayHandler) lView.getDisplayHandler()).fireViewClosing(lView);
//			lView.getItemContainer().closeView();
		}
//		iDesktop.closeAllTabs();
	}

	public Iterator getIteratorView() {
		ArrayList lList = new ArrayList( iOpenedViews );
		
		return lList.iterator();
	}

	public GoMuleView getSelectedView() {
//		iDesktop.get
		return null;
	}

	public void removeView(GoMuleView pView) {
		iOpenedViews.remove( pView );
		iDesktop.closeTab(pView.getDisplay());
	}

}
