/*******************************************************************************
 * 
 * Copyright 2007 Randall
 * 
 * This file is part of gomule.
 * 
 * gomule is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * gomule is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * gomlue; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 *  
 ******************************************************************************/
package gomule.gui;

import gomule.d2s.*;
import gomule.d2x.*;
import gomule.gui.desktop.generic.*;
import gomule.gui.stashfilter.*;
import gomule.item.*;
import gomule.util.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import randall.util.*;

/**
 * @author Marco
 */
public class D2ViewStash /* extends JInternalFrame */ implements D2ItemContainer, D2ItemListListener, GoMuleViewStash
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5346518067556935604L;
	private D2FileManager iFileManager;
    private D2ItemList    iStash;
    private String        iFileName;
    private String        iStashName;

//    private D2StashFilter iStashFilter;
    private D2ItemModel   iItemModel;
    private JTable        iTable;

    private JPanel        iContentPane;
	private RandallPanel		iTopPanel;

    private JEditorPane     iItemText;

    private JButton 	  iPickup;
    private JButton       iDropOne;
    private JButton       iDropAll;
	private JButton				iFilterOld;
	private JButton				iFilterNew;
    
	private JButton iDelete;
	private JButton iDeleteDups;
	
	private JComponent				iContent;
	private GoMuleViewDisplayHandler	iDisplayHandler;
	private String						iTitle;
	private boolean						iEdited;
	
	private D2ViewStashFilter			iFilter;

    public D2ViewStash(D2FileManager pMainFrame, String pFileName)
    {
        iFileManager = pMainFrame;
        iFileName = pFileName;
        iStashName = getStashName( iFileName );

        iContentPane = new JPanel();
        iContentPane.setLayout(new BorderLayout());

        iItemModel = new D2ItemModel();
        iTable = new JTable(iItemModel);
        iTable.addKeyListener(new KeyAdapter()
        {
            public void keyReleased(KeyEvent e) 
            {
                if ( e.getKeyCode() == KeyEvent.VK_Z )
                {
                    int lNew = iTable.getSelectedRow() + 1;
                    if ( lNew < iTable.getRowCount() )
                    {
                        iTable.setRowSelectionInterval(lNew, lNew);
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_A )
                {
                    int lNew = iTable.getSelectedRow() -1;
                    if ( lNew >= 0 )
                    {
                        iTable.setRowSelectionInterval(lNew, lNew);
                    }
                }
            }
        });
        
//        Font lFont = iTable.getTableHeader().getFont();
//            iTable.getTableHeader().setFont( new Font(lFont.getName(), lFont.getStyle(), lFont.getSize()-2) );
        iTable.getTableHeader().addMouseListener( new MouseAdapter() 
        {
            public void mouseReleased(MouseEvent e) 
            {
                if ( e.getSource() instanceof JTableHeader )
                {
                    JTableHeader lHeader = (JTableHeader) e.getSource();
                    int lHeaderCol = lHeader.columnAtPoint(new Point(e.getX(), e.getY()));
                    
                    lHeaderCol = lHeader.getColumnModel().getColumn(lHeaderCol).getModelIndex();
                    
                    if ( lHeaderCol != -1 )
                    {
                        iItemModel.sortCol(lHeaderCol);
                    }
                }
            }
        });
        
        iTable.setDefaultRenderer(String.class, new D2CellStringRenderer() );
        iTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            iTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        iTable.getColumnModel().getColumn(1).setPreferredWidth(11);
        iTable.getColumnModel().getColumn(2).setPreferredWidth(11);
        iTable.getColumnModel().getColumn(3).setPreferredWidth(15);
        JScrollPane lPane = new JScrollPane(iTable);
        lPane.setPreferredSize(new Dimension(257, 100));
        
        JSplitPane stashConts = new JSplitPane();
        stashConts.setLeftComponent(lPane);
        
        RandallPanel lButtonPanel = getButtonPanel();
        
		iFilter = new D2ViewStashFilterOld( this );

		iTopPanel = new RandallPanel();
        iTopPanel.addToPanel(lButtonPanel, 0, 0, 1, RandallPanel.HORIZONTAL);
		iTopPanel.addToPanel( iFilter.getDisplay(), 0, 1, 1, RandallPanel.HORIZONTAL );

        iContentPane.add(iTopPanel, BorderLayout.NORTH);

        JPanel lItemPanel = new JPanel();
        iItemText = new JEditorPane();
        iItemText.setContentType("text/html");
        iItemText.setBackground(Color.black);
        JScrollPane lItemScroll = new JScrollPane(iItemText);
        lItemPanel.setLayout(new BorderLayout());
        lItemPanel.add(lItemScroll, BorderLayout.CENTER);
        lItemPanel.setPreferredSize(new Dimension(250, 100));

        stashConts.setRightComponent(lItemPanel);
        stashConts.setDividerSize(3);
        stashConts.setDividerLocation(257);
        iContentPane.add(stashConts);
        iContent = iContentPane;
        
        connect();

        if (iTable != null)
        {
        	
        	iTable.addMouseListener(new MouseListener(){

				public void mouseClicked(MouseEvent arg0) {
					if(arg0.getButton() == MouseEvent.BUTTON1 && arg0.getClickCount() == 2){
						pickupSelected();
					}
				}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
        	});
        	
            iTable.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener()
                    {
                        public void valueChanged(ListSelectionEvent e)
                        {
                            if (iTable.getSelectedRowCount() == 1)
                            {
                            	
                            	String dispStr = iItemModel.getItem(iTable.getSelectedRow()).itemDumpHtml(true).replaceAll("<[/]*html>", "");
                                if(!isStash()){
                                	iItemText.setText("<html><font size=3 face=Dialog><font color = white>Item From: "+(((D2ItemListAll) iStash).getFilename(iItemModel.getItem(iTable.getSelectedRow())))+"</font><br><br>"+dispStr+"</font></html>");
                                }else{
                                	iItemText.setText("<html><font size=3 face=Dialog>"+dispStr+"</font></html>");
                                    
                                }
                                iItemText.setCaretPosition(0);
                            }
                            else
                            {
                                iItemText.setText("");
                            }
                        }
                    });
        }
        if ( iTable.getRowCount() > 0 )
        {
            iTable.setRowSelectionInterval(0,0);
        }
    }
    
    public static String getStashName(String pFileName)
    {
        ArrayList lList = RandallUtil.split(pFileName, File.separator, true);
        return (String) lList.get(lList.size()-1);
    }
    
    public boolean isStash()
    {
        return !"all".equalsIgnoreCase( iFileName );
    }
    
    public void connect()
    {
        try
        {
            iStash = iFileManager.addItemList(iFileName, this);
            itemListChanged();
            
            if ( isStash() )
            {
	            iPickup.setEnabled(true);
	            iDropOne.setEnabled(true);
	            iDropAll.setEnabled(true);
	            iDropOne.setVisible(true);
	            iDropAll.setVisible(true);
            }
            else
            {
	            iPickup.setEnabled(true);
	            iDropOne.setEnabled(false);
	            iDropAll.setEnabled(false);
	            iDropOne.setVisible(false);
	            iDropAll.setVisible(false);
            }
            iTable.setModel( iItemModel );
        }
        catch( Exception pEx ){
            disconnect(pEx);
        }
        
    }
    
    public void disconnect(Exception pEx)
    {
        if ( iStash != null )
        {
            if ( isStash() )
            {
                iFileManager.removeItemList(iFileName, this);
            }
            else
            {
                ArrayList lList = ((D2ItemListAll) iStash).getAllContainers();
                for ( int i = 0 ; i < lList.size() ; i++ )
                {
                    D2ItemList lItemList = (D2ItemList) lList.get(i);
                    iFileManager.removeItemList(lItemList.getFilename(), this);
                }
            }
        }
        
        iStash = null;
        iPickup.setEnabled(false);
        iDropOne.setEnabled(false);
        iDropAll.setEnabled(false);
        itemListChanged();
    }

    public boolean isHC()
    {
        return iStash.isHC();
    }

    public boolean isSC()
    {
        return iStash.isSC();
    }
    
    private RandallPanel getButtonPanel()
    {
        RandallPanel lButtonPanel = new RandallPanel(true);

        iPickup = new JButton("Pickup");
        iDelete = new JButton("Delete");
        iDeleteDups = new JButton("Delete Dupes");
        iPickup.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent){
            	pickupSelected();
            }
        });
        lButtonPanel.addToPanel(iPickup, 0, 0, 1, RandallPanel.HORIZONTAL);

        iDropOne = new JButton("Drop");
        iDropOne.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                ((D2Stash) iStash).addItem(D2ViewClipboard.removeItem());
            }
        });
        lButtonPanel.addToPanel(iDropOne, 1, 0, 1, RandallPanel.HORIZONTAL);

        iDropAll = new JButton("Drop All");
        iDropAll.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                try
                {
                	iStash.ignoreItemListEvents();
	                ArrayList lItemList = D2ViewClipboard.removeAllItems();
	                while (lItemList.size() > 0)
	                {
	                    ((D2Stash) iStash).addItem((D2Item) lItemList.remove(0));
	                }
                }
                finally
                {
                	 iStash.listenItemListEvents();
                }
                itemListChanged();
            }
        });
        
        iDelete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                Vector lItemList = new Vector();

                int lRows[] = iTable.getSelectedRows();

                if (lRows.length > 0)
                {
                    for (int i = 0; i < lRows.length; i++)
                    {
                        lItemList.add(iItemModel.getItem(lRows[i]));
                    }
                    try
                    {
                    	iStash.ignoreItemListEvents();
                        for (int i = 0; i < lItemList.size(); i++)
                        {				
                       int check = JOptionPane.showConfirmDialog(null, "Delete " + ((D2Item) lItemList.get(i)).getName() + "?");
                        
						if(check == 0){
                            iStash.removeItem((D2Item) lItemList.get(i));
						}
						}
                        
                    }
                    finally
                    {
                    	 iStash.listenItemListEvents();
                    }
                    itemListChanged();
                }
            }
        });
        lButtonPanel.addToPanel(iDelete, 3, 0, 1, RandallPanel.HORIZONTAL);
        

        iDeleteDups.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
              int check = JOptionPane.showConfirmDialog(null, "WARNING: WILL DELETE ALL DUAL FPS. CONTINUE?");
              
				if(check != 0){
                  return;
				}
            	
                HashMap lItemList = new HashMap();

                    for (int i = 0; i < iTable.getRowCount(); i++)
                    {
                    	if(iItemModel.getItem(i).getFingerprint() != null && !iItemModel.getItem(i).getFingerprint().equals("")){
                        lItemList.put(iItemModel.getItem(i).getFingerprint(),iItemModel.getItem(i));
                    	}
                    }
                    
                    
                    try
                    {
                    	iStash.ignoreItemListEvents();
                        
                        for (int i = 0; i < iTable.getRowCount(); i++)
                        {
                        	if(iItemModel.getItem(i).getFingerprint() != null && !iItemModel.getItem(i).getFingerprint().equals("")){
                            iStash.removeItem(iItemModel.getItem(i));
                        	}
                        }
                        
                        Iterator it = lItemList.keySet().iterator();
                     while(it.hasNext()){
                        	((D2Stash) iStash).addItem(((D2Item)lItemList.get(it.next())));
                     }
                        
                    }
                    finally
                    {
                    	 iStash.listenItemListEvents();
                    }
                    itemListChanged();
                
            }
        });
        if(isStash())
        {
        	lButtonPanel.addToPanel(iDeleteDups, 4, 0, 1, RandallPanel.HORIZONTAL);
        }
		iFilterOld = new JButton( "Old Filter" );
		iFilterOld.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent pE )
			{
				iFilterOld.setEnabled( false );
				iFilterNew.setEnabled( true );

				activateNewFilter( new D2ViewStashFilterOld( D2ViewStash.this ) );
			}
		} );
		iFilterOld.setEnabled( false );
		lButtonPanel.addToPanel( iFilterOld, 5, 0, 1, RandallPanel.HORIZONTAL );

		iFilterNew = new JButton( "New Filter" );
		iFilterNew.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent pE )
			{
				iFilterNew.setEnabled( false );
				iFilterOld.setEnabled( true );

				activateNewFilter( new D2ViewStashFilterNew( D2ViewStash.this ) );
			}
		} );
		// iFilterNew.setEnabled( false );
		lButtonPanel.addToPanel( iFilterNew, 6, 0, 1, RandallPanel.HORIZONTAL );

        return lButtonPanel;
    }

	private void activateNewFilter(D2ViewStashFilter pNewFilter)
	{
		if ( iFilter != null )
		{
			iTopPanel.remove( iFilter.getDisplay() );
		}
		
		iFilter = pNewFilter;
		
		if ( iFilter != null )
		{
			iTopPanel.addToPanel( iFilter.getDisplay(), 0, 1, 1, RandallPanel.HORIZONTAL );
		}
		
		iTopPanel.revalidate();
		iTopPanel.repaint();
		
		itemListChanged();
	}

    protected void pickupSelected() {
            Vector lItemList = new Vector();

            int lRows[] = iTable.getSelectedRows();

            if (lRows.length > 0)
            {
                for (int i = 0; i < lRows.length; i++)
                {
                    lItemList.add(iItemModel.getItem(lRows[i]));
                }
                try
                {
                	iStash.ignoreItemListEvents();
                    for (int i = 0; i < lItemList.size(); i++)
                    {
                        iStash.removeItem((D2Item) lItemList.get(i));
                        D2ViewClipboard.addItem((D2Item) lItemList.get(i));
                    }
                }
                finally
                {
                    iStash.listenItemListEvents();
                }
                itemListChanged();
            }
	}
    
    public String getViewTitle()
    {
        String lTitle = iStashName;
        if (iStash == null || iItemModel == null)
        {
            lTitle += " (Disconnected)";
        }
        else
        {
            lTitle += " (" + iItemModel.getRowCount() + "/";
            lTitle += iStash.getNrItems() + ")" + ((iStash.isModified()) ? "*" : "");
            if (iStash.isSC() && iStash.isHC())
            {
                lTitle += " (Unknown)";
            }
            else if (iStash.isSC())
            {
                lTitle += " (SC)";
            }
            else if (iStash.isHC())
            {
                lTitle += " (HC)";
            }
        }
    	
        return lTitle;
    }

    public void itemListChanged()
    {

        iItemModel.refreshData();
        String lTitle = iStashName;
        iEdited = false;
        if (iStash == null || iItemModel == null)
        {
            lTitle += " (Disconnected)";
        }
        else
        {
            lTitle += " (" + iItemModel.getRowCount() + "/";
            iEdited = iStash.isModified();
            lTitle += iStash.getNrItems() + ")" + ( iEdited ? "*" : "" );
            if (iStash.isSC() && iStash.isHC())
            {
                lTitle += " (Unknown)";
            }
            else if (iStash.isSC())
            {
                lTitle += " (SC)";
            }
            else if (iStash.isHC())
            {
                lTitle += " (HC)";
            }
        }
        iTitle = lTitle;
        if ( iDisplayHandler != null )
        {
        	iDisplayHandler.setTitle( iTitle );
        	iDisplayHandler.setEdited( iEdited );
        }
//        setTitle(lTitle);
    }

	public void setDisplayHandler(GoMuleViewDisplayHandler pDisplayHandler)
	{
		iDisplayHandler = pDisplayHandler;
		if ( iTitle != null )
		{
			iDisplayHandler.setTitle( iTitle );
        	iDisplayHandler.setEdited( iEdited );
		}
	}
	
	public GoMuleViewDisplayHandler getDisplayHandler()
	{
		return iDisplayHandler;
	}

	public JComponent getDisplay() 
	{
		return iContent;
	}
	
	public D2ViewStash getViewStash() 
	{
		return this;
	}
	
	public D2ItemContainer getItemContainer() 
	{
		return this;
	}

    class D2ItemModel implements TableModel
    {
        private ArrayList iItems;
        private ArrayList iTableModelListeners = new ArrayList();
        private ArrayList iSortList = new ArrayList();

        private final Object HEADER[] = new Object[] {new Object(), new Object(), new Object(), new Object(), new Object()};
//		private String filterString = "";
//		private int filterVal = -1337;
//		private boolean filterOn = false;
//		private boolean filterMin = true;
        
        public D2ItemModel()
        {
//            iStash = pStash;
            iSortList.add(HEADER[0]);
            refreshData();
        }
        
        public void sortCol(int pHeaderCol)
        {
            iSortList.remove(HEADER[pHeaderCol]);
            iSortList.add(0, HEADER[pHeaderCol]);
            sort();
        }
        
        public int getInteger(JTextField pTextfield)
        {
            String lText = pTextfield.getText();
            if ( lText != null )
            {
                if ( !lText.trim().equals("") )
                {
                    try
                    {
                        pTextfield.setForeground(Color.black);
                        return Integer.parseInt(lText);
                    }
                    catch ( NumberFormatException pEx )
                    {
                        pTextfield.setForeground(Color.red);
                        // do Nothing
                    }
                }
            }
            return -1;
        }

        public void refreshData()
        {
			iItems = new ArrayList();
			if ( iStash != null )
			{
				ArrayList lOldList = iStash.getItemList();

				if ( iFilter != null )
				{
					iItems.addAll( iFilter.filterItems( lOldList ) );
				}
				else
				{
					iItems.addAll( lOldList );
				}
			}
            sort();
        }
        
        public void sort()
        {
            Collections.sort(iItems, new Comparator()
            {
                public int compare(Object pObj1, Object pObj2)
                {
                    D2Item lItem1 = (D2Item) pObj1;
                    D2Item lItem2 = (D2Item) pObj2;
                    
                    for ( int i = 0 ; i < iSortList.size() ; i++ )
                    {
                        Object lSort = iSortList.get(i);
                        
                        if ( lSort ==  HEADER[0] )
                        {
                            return lItem1.getName().compareTo(lItem2.getName());
                        }
                        else if ( lSort ==  HEADER[1] )
                        {
                            return lItem1.getReqLvl() - lItem2.getReqLvl();
                        } 
                        else if ( lSort ==  HEADER[2] )
                        {
                            return lItem1.getReqStr() - lItem2.getReqStr();
                        } 
                        else if ( lSort ==  HEADER[3] )
                        {
                            return lItem1.getReqDex() - lItem2.getReqDex();
                        } 
                        else if ( lSort ==  HEADER[4] )
                        {
                            String lFileName1 = ((D2ItemListAll) iStash).getFilename(lItem1);
                            String lFileName2 = ((D2ItemListAll) iStash).getFilename(lItem2);
                            return lFileName1.compareTo(lFileName2);
                        } 
                    }
                    
                    return 0;
                }
            });
            fireTableChanged();
        }

        public int getRowCount()
        {
            return iItems.size();
        }

        public D2Item getItem(int pRow)
        {
            return (D2Item) iItems.get(pRow);
        }

        public int getColumnCount()
        {
                return 4;
        }

        public String getColumnName(int pCol)
        {
            switch (pCol)
            {
            case 0:
                return "Name";
            case 1:
                return "lvl";
            case 2:
                return "str";
            case 3:
                return "dex";
//            case 4:
//                return "Type";
            default:
                return "";
            }
        }

        public Class getColumnClass(int pCol)
        {
            return String.class;
        }

        public boolean isCellEditable(int pRow, int pCol)
        {
            return false;
        }

        public Object getValueAt(int pRow, int pCol)
        {
            D2Item lItem = (D2Item) iItems.get(pRow);
            switch (pCol)
            {            
            case 0:
                return new D2CellValue( lItem.getItemName(), lItem, iFileManager.getProject());
            case 1:
                return new D2CellValue( getStringValue(lItem.getReqLvl()), lItem, iFileManager.getProject());
            case 2:
                return new D2CellValue( getStringValue(lItem.getReqStr()), lItem, iFileManager.getProject());
            case 3:
                return new D2CellValue( getStringValue(lItem.getReqDex()), lItem, iFileManager.getProject());
            case 4:
                String lFileName = ((D2ItemListAll) iStash).getFilename(lItem);
                String lType = ( lFileName.toLowerCase().endsWith(".d2s") ) ? "C":"S";
                return new D2CellValue( lType, lFileName, lItem, iFileManager.getProject() );
            default:
                return "";
            }
        }
        
        private String getStringValue(int pValue)
        {
            if ( pValue == -1 )
            {
                return "";
            }
            return Integer.toString( pValue );
        }

        public void setValueAt(Object pValue, int pRow, int pCol)
        {
            // Do nothing
        }

        public void addTableModelListener(TableModelListener pListener)
        {
            iTableModelListeners.add(pListener);
        }

        public void removeTableModelListener(TableModelListener pListener)
        {
            iTableModelListeners.remove(pListener);
        }

        public void fireTableChanged()
        {
            fireTableChanged(new TableModelEvent(this));
        }

        public void fireTableChanged(TableModelEvent pEvent)
        {
            for (int i = 0; i < iTableModelListeners.size(); i++)
            {
                ((TableModelListener) iTableModelListeners.get(i))
                        .tableChanged(pEvent);
            }
        }

    }

    public String getFileName()
    {
        return iFileName;
    }

    public boolean isModified()
    {
        return iStash.isModified();
    }

    public D2ItemList getItemLists()
    {
        return iStash;
    }
    
    public void closeView()
    {
        disconnect(null);
        
        iFileManager.removeFromOpenWindows(this);
    }

    public void resetCharacter(D2Character pChar)
    {
        throw new RuntimeException("Internal error: wrong calling");
    }

    public void resetStash(D2Stash pStash)
    {
    	iStash.listenItemListEvents();
        iStash = pStash;
        itemListChanged();
    }
    
    public D2ItemList getStash(){
    	return iStash;
    }
    
    public String getStashName(){
    	return iStashName;
    }

    
}