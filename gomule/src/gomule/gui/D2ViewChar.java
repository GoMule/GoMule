/*******************************************************************************
 * 
 * Copyright 2007 Andy Theuninck & Randall
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
import gomule.item.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import randall.util.*;

/**
 * @author Marco
 *  
 */
public class D2ViewChar extends JInternalFrame implements D2ItemContainer, D2ItemListListener
{
    private D2CharPainterPanel       iCharPainter;
    private D2MercPainterPanel       iMercPainter;
    private D2CharCursorPainterPanel iCharCursorPainter;
    private D2Character         	 iCharacter;

    private JTextArea				 iMessage;
    
    private static final int         BG_WIDTH         = 550;
    private static final int         BG_HEIGHT        = 383;
    private static final int         BG_MERC_WIDTH    = 323;
    private static final int         BG_MERC_HEIGHT   = 187;
    private static final int         BG_CURSOR_WIDTH  = 78;
    private static final int         BG_CURSOR_HEIGHT = 135;

    private static final int         STASH_X          = 327;
    private static final int         STASH_Y          = 9;
    private static final int         INV_X            = 18;
    private static final int         INV_Y            = 255;
    private static final int         HEAD_X           = 135;
    private static final int         HEAD_Y           = 3;
    private static final int         NECK_X           = 205;
    private static final int         NECK_Y           = 30;
    private static final int         BODY_X           = 135;
    private static final int         BODY_Y           = 75;
    private static final int         L_ARM_X          = 250;
    private static final int         L_ARM_Y          = 50;
    private static final int         R_ARM_X          = 20;
    private static final int         R_ARM_Y          = 50;
    private static final int         GLOVES_X         = 18;
    private static final int         GLOVES_Y         = 175;
    private static final int         BELT_X           = 135;
    private static final int         BELT_Y           = 175;
    private static final int         BOOTS_X          = 250;
    private static final int         BOOTS_Y          = 175;
    private static final int         L_RING_X         = 91;
    private static final int         L_RING_Y         = 175;
    private static final int         R_RING_X         = 205;
    private static final int         R_RING_Y         = 175;
    private static final int         BELT_GRID_X      = 426;
    private static final int         BELT_GRID_Y      = 250;
    private static final int         CUBE_X           = 328;
    private static final int         CUBE_Y           = 251;
    private static final int         GRID_SIZE        = 28;
    private static final int         GRID_SPACER      = 1;
    private static final int         CURSOR_X         = 12;
    private static final int         CURSOR_Y         = 14;

    private String                   iFileName;
    private D2FileManager            iFileManager;

    private int                      iWeaponSlot      = 1;

    private JTextField               iGold;
    private JTextField               iGoldBank;
    private JTextField               iGoldMax;
    private JTextField               iGoldBankMax;
    private JRadioButton             iConnectGold;
    private JRadioButton             iConnectGoldBank;
    private JTextField               iTransferFree;
    
    private JButton					 iGoldTransferBtns[];

    public D2ViewChar(D2FileManager pMainFrame, String pFileName)
    {
        super(pFileName, false, true, false, true);
        
        addInternalFrameListener(new InternalFrameAdapter()
        {
            public void internalFrameClosing(InternalFrameEvent e)
            {
                iFileManager.saveAll();
                closeView();
            }
        });
        
        iFileManager = pMainFrame;
        iFileName = pFileName;

        JTabbedPane lTabs = new JTabbedPane();

        JPanel lCharPanel = new JPanel();
        lCharPanel.setLayout(new BorderLayout());
        iCharPainter = new D2CharPainterPanel();
        lCharPanel.add(iCharPainter, BorderLayout.CENTER);
        lTabs.addTab("Character", lCharPanel);
        setContentPane(lTabs);
        iCharPainter.build();

        JPanel lCursorPanel = new JPanel();
        lCursorPanel.setLayout(new BorderLayout());
        iCharCursorPainter = new D2CharCursorPainterPanel();
        lCursorPanel.add(new JLabel("Only for item retrieval, no items can be put here"), BorderLayout.NORTH);
        lCursorPanel.add(iCharCursorPainter, BorderLayout.CENTER);
        lTabs.addTab("Cursor", lCursorPanel);
        iCharCursorPainter.build();

        JPanel lMercPanel = new JPanel();
        lMercPanel.setLayout(new BorderLayout());
        iMercPainter = new D2MercPainterPanel();
        lMercPanel.add(iMercPainter, BorderLayout.CENTER);
        lTabs.addTab("Mercenary", lMercPanel);
        iMercPainter.build();

        ButtonGroup lConnectGroup = new ButtonGroup();

        RandallPanel lBankPanel = new RandallPanel();
        iGold = new JTextField();
        iGold.setEditable(false);
        iGoldMax = new JTextField();
        iGoldMax.setEditable(false);
        iConnectGold = new JRadioButton();
        lConnectGroup.add(iConnectGold);

        iGoldBank = new JTextField();
        iGoldBank.setEditable(false);
        iGoldBankMax = new JTextField();
        iGoldBankMax.setEditable(false);
        iConnectGoldBank = new JRadioButton();
        lConnectGroup.add(iConnectGoldBank);
        iConnectGoldBank.setSelected(true);

        lBankPanel.addToPanel(new JLabel("Gold: "), 0, 0, 1, RandallPanel.NONE);
        lBankPanel.addToPanel(iConnectGold, 1, 0, 1, RandallPanel.NONE);
        lBankPanel.addToPanel(iGold, 2, 0, 1, RandallPanel.HORIZONTAL);
        lBankPanel.addToPanel(iGoldMax, 3, 0, 1, RandallPanel.HORIZONTAL);
        lBankPanel.addToPanel(new JLabel("Gold Stash: "), 0, 1, 1, RandallPanel.NONE);
        lBankPanel.addToPanel(iConnectGoldBank, 1, 1, 1, RandallPanel.NONE);
        lBankPanel.addToPanel(iGoldBank, 2, 1, 1, RandallPanel.HORIZONTAL);
        lBankPanel.addToPanel(iGoldBankMax, 3, 1, 1, RandallPanel.HORIZONTAL);

        RandallPanel lTransferPanel = new RandallPanel(true);
        lTransferPanel.setBorder("Transfer");
        
        iGoldTransferBtns = new JButton[8];
        
        iGoldTransferBtns[0] = new JButton("to char");
        iGoldTransferBtns[0].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferToChar(10000);
            }
        });
        JTextField lField10000 = new JTextField("10.000");
        lField10000.setEditable(false);
        iGoldTransferBtns[1] = new JButton("from char");
        iGoldTransferBtns[1].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferFromChar(10000);
            }
        });
        
        iGoldTransferBtns[2] = new JButton("to char");
        iGoldTransferBtns[2].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferToChar(100000);
            }
        });
        JTextField lField100000 = new JTextField("100.000");
        lField100000.setEditable(false);
        iGoldTransferBtns[3] = new JButton("from char");
        iGoldTransferBtns[3].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferFromChar(100000);
            }
        });
        
        iGoldTransferBtns[4] = new JButton("to char");
        iGoldTransferBtns[4].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferToChar(1000000);
            }
        });
        JTextField lField1000000 = new JTextField("1.000.000");
        lField1000000.setEditable(false);
        iGoldTransferBtns[5] = new JButton("from char");
        iGoldTransferBtns[5].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferFromChar(1000000);
            }
        });
        
        iGoldTransferBtns[6] = new JButton("to char");
        iGoldTransferBtns[6].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferToChar(getTransferFree());
            }
        });
        iTransferFree = new JTextField("10000");
        iGoldTransferBtns[7] = new JButton("from char");
        iGoldTransferBtns[7].addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                transferFromChar(getTransferFree());
            }
        });

        lTransferPanel.addToPanel(iGoldTransferBtns[0], 0, 0, 1, RandallPanel.NONE);
        lTransferPanel.addToPanel(lField10000, 1, 0, 1, RandallPanel.HORIZONTAL);
        lTransferPanel.addToPanel(iGoldTransferBtns[1], 2, 0, 1, RandallPanel.NONE);
        
        lTransferPanel.addToPanel(iGoldTransferBtns[2], 0, 1, 1, RandallPanel.NONE);
        lTransferPanel.addToPanel(lField100000, 1, 1, 1, RandallPanel.HORIZONTAL);
        lTransferPanel.addToPanel(iGoldTransferBtns[3], 2, 1, 1, RandallPanel.NONE);
        
        lTransferPanel.addToPanel(iGoldTransferBtns[4], 0, 2, 1, RandallPanel.NONE);
        lTransferPanel.addToPanel(lField1000000, 1, 2, 1, RandallPanel.HORIZONTAL);
        lTransferPanel.addToPanel(iGoldTransferBtns[5], 2, 2, 1, RandallPanel.NONE);
        
        lTransferPanel.addToPanel(iGoldTransferBtns[6], 0, 3, 1, RandallPanel.NONE);
        lTransferPanel.addToPanel(iTransferFree, 1, 3, 1, RandallPanel.HORIZONTAL);
        lTransferPanel.addToPanel(iGoldTransferBtns[7], 2, 3, 1, RandallPanel.NONE);

        lBankPanel.addToPanel(lTransferPanel, 0, 10, 3, RandallPanel.HORIZONTAL);

        lBankPanel.finishDefaultPanel();
        lTabs.addTab("Bank", lBankPanel);
        
        iMessage = new JTextArea();
        JScrollPane lScroll = new JScrollPane(iMessage);
        RandallPanel lMessagePanel = new RandallPanel();
        JButton lConnect = new JButton("Connect");
        lConnect.addActionListener(new ActionListener()
	    {
            public void actionPerformed(ActionEvent pEvent)
            {
                connect();
            }
	    });
        JButton lDisconnect = new JButton("Disconnect");
        lDisconnect.addActionListener(new ActionListener()
	    {
            public void actionPerformed(ActionEvent pEvent)
            {
                disconnect(null);
            }
	    });
        
	    lMessagePanel.addToPanel(lConnect, 0, 0, 1, RandallPanel.HORIZONTAL);
	    lMessagePanel.addToPanel(lDisconnect, 1, 0, 1, RandallPanel.HORIZONTAL);
	    lMessagePanel.addToPanel(lScroll, 0, 1, 2, RandallPanel.BOTH);
	    lTabs.addTab("Messages", lMessagePanel);
	    iMessage.setText("Nothing done, disconnected");

        pack();
        setVisible(true);
        
        connect();
        itemListChanged();

        //        setModified(true);
    }
    
    public void connect()
    {
        if ( iCharacter != null )
        {
            return;
        }
        try
        {
	        iCharacter = (D2Character) iFileManager.addItemList(iFileName, this);
	        
	        iGold.setText(Integer.toString(iCharacter.getGold()));
	        iGoldMax.setText(Integer.toString(iCharacter.getGoldMax()));
	        iGoldBank.setText(Integer.toString(iCharacter.getGoldBank()));
	        iGoldBankMax.setText(Integer.toString(iCharacter.getGoldBankMax()));
	        
	        for ( int i = 0 ; i < iGoldTransferBtns.length ; i++ )
	        {
	            iGoldTransferBtns[i].setEnabled(true);
	        }
	        
	        itemListChanged();
	        iMessage.setText("Character loaded");
        }
        catch ( Exception pEx )
        {
            disconnect(pEx);
        }
    }
    
    public void disconnect(Exception pEx)
    {
        if ( iCharacter != null )
        {
            iFileManager.removeItemList(iFileName, this);
        }
        
        iCharacter = null;
        
        iMessage.setText("Character disconnected");
        
        iGold.setText("");
        iGoldMax.setText("");
        iGoldBank.setText("");
        iGoldBankMax.setText("");
        
        for ( int i = 0 ; i < iGoldTransferBtns.length ; i++ )
        {
            iGoldTransferBtns[i].setEnabled(false);
        }
        
        itemListChanged();
    }

    public void transferToChar(int pGoldTransfer)
    {
        if (pGoldTransfer > 0)
        {
            try
            {
                // to char
                int lBank = iFileManager.getProject().getBankValue();
                if (pGoldTransfer > lBank)
                {
                    // don't allow more as the bank has
                    pGoldTransfer = lBank;
                }
                int lGoldChar;
                int lGoldMax;
                if (iConnectGold.isSelected())
                {
                    lGoldChar = iCharacter.getGold();
                    lGoldMax = iCharacter.getGoldMax();
                }
                else
                {
                    lGoldChar = iCharacter.getGoldBank();
                    lGoldMax = iCharacter.getGoldBankMax();
                }
                // char limit
                if (lGoldChar + pGoldTransfer > lGoldMax)
                {
                    pGoldTransfer = lGoldMax - lGoldChar;
                }
                int lNewGold = lGoldChar + pGoldTransfer;
                int lNewGoldBank = lBank - pGoldTransfer;
                if (iConnectGold.isSelected())
                {
                    iCharacter.setGold(lNewGold);
                    iGold.setText(Integer.toString(iCharacter.getGold()));
                }
                else
                {
                    iCharacter.setGoldBank(lNewGold);
                    iGoldBank.setText(Integer.toString(iCharacter.getGoldBank()));
                }
                iFileManager.getProject().setBankValue(lNewGoldBank);
            }
            catch (Exception pEx)
            {
                D2FileManager.displayErrorDialog(pEx);
            }
        }

    }

    public void transferFromChar(int pGoldTransfer)
    {
        if (pGoldTransfer > 0)
        {
            try
            {
                int lGoldChar;
                if (iConnectGold.isSelected())
                {
                    lGoldChar = iCharacter.getGold();
                }
                else
                {
                    lGoldChar = iCharacter.getGoldBank();
                }

                if (pGoldTransfer > lGoldChar)
                {
                    // don't allow more as the char has
                    pGoldTransfer = lGoldChar;
                }
                
                // from char
                int lBank = iFileManager.getProject().getBankValue();
                
                int lNewGold = lGoldChar - pGoldTransfer;
                int lNewGoldBank = lBank + pGoldTransfer;
                
                if (iConnectGold.isSelected())
                {
                    iCharacter.setGold(lNewGold);
                    iGold.setText(Integer.toString(iCharacter.getGold()));
                }
                else
                {
                    iCharacter.setGoldBank(lNewGold);
                    iGoldBank.setText(Integer.toString(iCharacter.getGoldBank()));
                }
                iFileManager.getProject().setBankValue(lNewGoldBank);
            }
            catch (Exception pEx)
            {
                D2FileManager.displayErrorDialog(pEx);
            }
        }

    }

    public int getTransferFree()
    {
        try
        {
            return Integer.parseInt(iTransferFree.getText());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public boolean isHC()
    {
        return iCharacter.isHC();
    }

    public boolean isSC()
    {
        return iCharacter.isSC();
    }

    public String getFileName()
    {
        return iFileName;
    }
    
    public boolean isModified()
    {
        return iCharacter.isModified();
    }
    
    public D2ItemList getItemLists()
    {
        return iCharacter;
    }

    public void closeView()
    {
        disconnect(null);
        iFileManager.removeFromOpenWindows(this);
    }

    public void itemListChanged()
    {
        String lTitle;
        if ( iCharacter == null )
        {
            lTitle = "Disconnected";
        }
        else
        {
            lTitle = iCharacter.getCharName();
	        if (iCharacter == null)
	        {
	            lTitle += " (Error Reading File)";
	        }
	        else
	        {
	            lTitle += (( iCharacter.isModified() ) ? "*" : "");
	            if (iCharacter.isSC())
	            {
	                lTitle += " (SC)";
	            }
	            else if (iCharacter.isHC())
	            {
	                lTitle += " (HC)";
	            }
	            lTitle += iCharacter.getTitleString();
	        }
        }
        setTitle(lTitle);
        iCharPainter.build();
        if ( iMercPainter != null )
        {
            iMercPainter.build();
        }
        iCharCursorPainter.build();
        
        
    }

    public void setCursorPickupItem()
    {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setCursorDropItem()
    {
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void setCursorNormal()
    {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    class D2ItemPanel
    {
        private boolean iIsChar;
        private boolean iIsCursor;
        private int     iPanel;
        private int     iRow;
        private int     iCol;

        public D2ItemPanel(MouseEvent pEvent, boolean pIsChar, boolean pIsCursor)
        {
            iIsChar = pIsChar;
            iIsCursor = pIsCursor;
            int x = pEvent.getX();
            int y = pEvent.getY();

            iPanel = getMousePanel(x, y);
            setRowCol(x, y);
        }

        public int getPanel()
        {
            return iPanel;
        }

        public int getRow()
        {
            return iRow;
        }

        public int getColumn()
        {
            return iCol;
        }

        public boolean isItem()
        {
            if (iIsChar)
            {
                return iCharacter.checkCharPanel(iPanel, iRow, iCol, null);
            }
            return iCharacter.checkMercPanel(iPanel, iRow, iCol, null);
        }

        public int getItemIndex()
        {
            if (iIsChar)
            {
                return iCharacter.getCharItemIndex(iPanel, iRow, iCol);
            }
            return iCharacter.getMercItemIndex(iPanel, iRow, iCol);
        }

        public D2Item getItem()
        {
            if (iIsChar)
            {
                return iCharacter.getCharItem(iCharacter.getCharItemIndex(iPanel, iRow, iCol));
            }
            return iCharacter.getMercItem(iCharacter.getMercItemIndex(iPanel, iRow, iCol));
        }

        // calculate which panel (stash, inventory, equipment slot, etc)
        // the coordinates x and y lie in
        // belt_grid = panel 2
        // offset body positions by 10
        // return -1 on failure
        private int getMousePanel(int x, int y)
        {
            if (iIsCursor)
            {
                if (iIsChar && x >= CURSOR_X && x < CURSOR_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= CURSOR_Y && y < CURSOR_Y + 4 * GRID_SIZE + 4 * GRID_SPACER)
                {
                    return D2Character.BODY_CURSOR;
                }
                return -1;
            }
            if (iIsChar && x >= STASH_X && x < STASH_X + 6 * GRID_SIZE + 6 * GRID_SPACER && y >= STASH_Y && y < STASH_Y + 8 * GRID_SIZE + 8 * GRID_SPACER)
            {
                return D2Character.BODY_STASH_CONTENT;
            }
            if (iIsChar && x >= BELT_GRID_X && x < BELT_GRID_X + 4 * GRID_SIZE + 4 * GRID_SPACER && y >= BELT_GRID_Y && y < BELT_GRID_Y + 4 * GRID_SIZE + 4 * GRID_SPACER)
            {
                return D2Character.BODY_BELT_CONTENT;
            }
            if (iIsChar && x >= INV_X && x < INV_X + 10 * GRID_SIZE + 10 * GRID_SPACER && y >= INV_Y && y < INV_Y + 4 * GRID_SIZE + 4 * GRID_SPACER)
            {
                return D2Character.BODY_INV_CONTENT;
            }
            if (iIsChar && x >= CUBE_X && x < CUBE_X + 3 * GRID_SIZE + 3 * GRID_SPACER && y >= CUBE_Y && y < INV_Y + 4 * GRID_SIZE + 4 * GRID_SPACER)
            {
                return D2Character.BODY_CUBE_CONTENT;
            }
            // merc & char
            if (x >= HEAD_X && x < HEAD_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= HEAD_Y && y < HEAD_Y + 2 * GRID_SIZE + 2 * GRID_SPACER)
            {
                return D2Character.BODY_HEAD;
            }
            if (iIsChar && x >= NECK_X && x < NECK_X + 1 * GRID_SIZE + 1 * GRID_SPACER && y >= NECK_Y && y < NECK_Y + 1 * GRID_SIZE + 1 * GRID_SPACER)
            {
                return D2Character.BODY_NECK;
            }
            // merc & char
            if (x >= L_ARM_X && x < L_ARM_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= L_ARM_Y && y < L_ARM_Y + 4 * GRID_SIZE + 4 * GRID_SPACER)
            {
                if (!iIsChar || iWeaponSlot == 1)
                {
                    // merc
                    return D2Character.BODY_LARM;
                }
                else
                {
                    return D2Character.BODY_LARM2;
                }
            }
            // merc & char
            if (x >= R_ARM_X && x < R_ARM_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= R_ARM_Y && y < R_ARM_Y + 4 * GRID_SIZE + 4 * GRID_SPACER)
            {
                if (!iIsChar || iWeaponSlot == 1)
                {
                    // merc
                    return D2Character.BODY_RARM;
                }
                else
                {
                    return D2Character.BODY_RARM2;
                }
            }
            // merc & char
            if (x >= BODY_X && x < BODY_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= BODY_Y && y < BODY_Y + 3 * GRID_SIZE + 3 * GRID_SPACER)
            {
                return D2Character.BODY_TORSO;
            }
            if (iIsChar && x >= GLOVES_X && x < GLOVES_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= GLOVES_Y && y < GLOVES_Y + 2 * GRID_SIZE + 2 * GRID_SPACER)
            {
                return D2Character.BODY_GLOVES;
            }
            if (iIsChar && x >= BELT_X && x < BELT_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= BELT_Y && y < BELT_Y + 1 * GRID_SIZE + 1 * GRID_SPACER)
            {
                return D2Character.BODY_BELT;
            }
            if (iIsChar && x >= BOOTS_X && x < BOOTS_X + 2 * GRID_SIZE + 2 * GRID_SPACER && y >= BOOTS_Y && y < BOOTS_Y + 2 * GRID_SIZE + 2 * GRID_SPACER)
            {
                return D2Character.BODY_BOOTS;
            }
            if (iIsChar && x >= L_RING_X && x < L_RING_X + 1 * GRID_SIZE + 1 * GRID_SPACER && y >= L_RING_Y && y < L_RING_Y + 1 * GRID_SIZE + 1 * GRID_SPACER)
            {
                return D2Character.BODY_LRING;
            }
            if (iIsChar && x >= R_RING_X && x < R_RING_X + 1 * GRID_SIZE + 1 * GRID_SPACER && y >= R_RING_Y && y < R_RING_Y + 1 * GRID_SIZE + 1 * GRID_SPACER)
            {
                return D2Character.BODY_RRING;
            }
            return -1;
        }

        // get row/col
        private void setRowCol(int x, int y)
        {
            //            int row, col;
            //            int temp_item = -1;
            // non-equppied: calculate row and column
            // then fetch the item if that item space
            // has an item on it
            if (iPanel < 10)
            {
                switch (iPanel)
                {
                case 1: // inventory
                    iRow = (x - INV_X) / (GRID_SIZE + GRID_SPACER);
                    iCol = (y - INV_Y) / (GRID_SIZE + GRID_SPACER);
                    //                    if (iChar.check_panel(panel, row, col))
                    //                    {
                    //                        temp_item = iChar.get_item_index(panel, row, col);
                    //                    }
                    break;
                case 2: // belted
                    iRow = (x - BELT_GRID_X) / (GRID_SIZE + GRID_SPACER);
                    iCol = 3 - ((y - BELT_GRID_Y) / (GRID_SIZE + GRID_SPACER));
                    //                    if (iChar.check_panel(panel, row, col))
                    //                    {
                    //                        temp_item = iChar.get_item_index(panel, col, row);
                    //                    }
                    break;
                case 4: // cube
                    iRow = (x - CUBE_X) / (GRID_SIZE + GRID_SPACER);
                    iCol = (y - CUBE_Y) / (GRID_SIZE + GRID_SPACER);
                    //                    if (iChar.check_panel(panel, row, col))
                    //                    {
                    //                        temp_item = iChar.get_item_index(panel, row, col);
                    //                    }
                    break;
                case 5: // stash
                    iRow = (x - STASH_X) / (GRID_SIZE + GRID_SPACER);
                    iCol = (y - STASH_Y) / (GRID_SIZE + GRID_SPACER);
                    //                    if (iChar.check_panel(panel, row, col))
                    //                    {
                    //                        temp_item = iChar.get_item_index(panel, row, col);
                    //                    }
                    break;
                }
            }
            // equipped
            // row and column are irrelevant, so they can be zero
            else
            {
                iRow = 0;
                iCol = 0;
                //                if (iChar.check_panel(panel, 0, 0))
                //                {
                //                    temp_item = iChar.get_item_index(panel, 0, 0);
                //                }
            }
            //            return temp_item;
        }

    }

    class D2CharPainterPanel extends JPanel
    {
        private Image iBackground;

        public D2CharPainterPanel()
        {
            setSize(BG_WIDTH, BG_HEIGHT);
            Dimension lSize = new Dimension(BG_WIDTH, BG_HEIGHT);
            setPreferredSize(lSize);

            addMouseListener(new MouseAdapter()
            {
                public void mouseReleased(MouseEvent pEvent)
                {
                    if ( iCharacter == null )
                    {
                        return;
                    }
                    //                    System.err.println("Mouse Clicked: " + pEvent.getX() + ",
                    // " + pEvent.getY() );
                    if (pEvent.getButton() == MouseEvent.BUTTON1 /*
                                                                  * &&
                                                                  * pEvent.getClickCount() ==
                                                                  * 1
                                                                  */)
                    {
                        int lX = pEvent.getX();
                        int lY = pEvent.getY();
                        if (((lX >= 16 && lX <= 45) || (lX >= 247 && lX <= 276)) && (lY >= 24 && lY <= 44))
                        {
                            setWeaponSlot(1);
                        }
                        else if (((lX >= 51 && lX <= 80) || (lX >= 282 && lX <= 311)) && (lY >= 24 && lY <= 44))
                        {
                            setWeaponSlot(2);
                        }
                        // determine where the mouse click is
                        D2ItemPanel lItemPanel = new D2ItemPanel(pEvent, true, false);
                        if (lItemPanel.getPanel() != -1)
                        {
                            // if there is an item to grab, grab it
                            if (lItemPanel.isItem())
                            {
                                D2Item lTemp = lItemPanel.getItem();
                                iCharacter.unmarkCharGrid(lTemp);
                                iCharacter.removeCharItem(lItemPanel.getItemIndex());
                                D2ViewClipboard.addItem(lTemp);
                                setCursorDropItem();

//                                // redraw
//                                build();
//                                repaint();
                            }
                            else if (D2ViewClipboard.getItem() != null)
                            {
                                //	                    	System.err.println("Drop item");
                                // since there is an item on the mouse, try to
                                // drop it here

                                D2Item lDropItem = D2ViewClipboard.getItem();
                                //		                        int lDropWidth = lDropItem.get_width();
                                //		                        int lDropHeight = lDropItem.get_height();
                                //	                        int r = 0, c = 0;
                                boolean drop = false;
                                // non-equipped items, handle differently
                                // because they require a row and column
                                if (lItemPanel.getPanel() < 10)
                                {
                                    // calculate row and column for the given
                                    // panel
                                    // with mouse coords x and y (split into an
                                    // int for
                                    // convenience)
                                    //	                            int temp = find_grid(panel, x, y);
                                    //	                            r = temp >> 16;
                                    //	                            c = temp & 0xffff;
                                    //                            r -= (D2MouseItem.get_mouse_x() /
                                    // GRID_SIZE);
                                    //                            c -= (D2MouseItem.get_mouse_y() /
                                    // GRID_SIZE);
                                    // if that area of the character is empty,
                                    // then update fields of the item and set
                                    // the 'drop' variable to true
                                    if (iCharacter.checkCharGrid(lItemPanel.getPanel(), lItemPanel.getRow(), lItemPanel.getColumn(), lDropItem))
                                    {
                                        switch (lItemPanel.getPanel())
                                        {
                                        case 2:
                                            lDropItem.set_location((short) 2);
                                            lDropItem.set_body_position((short) 0);
                                            lDropItem.set_col((short) (4 * lItemPanel.getColumn() + lItemPanel.getRow()));
                                            lDropItem.set_row((short) 0);
                                            lDropItem.set_panel((short) 0);
                                            break;
                                        case 1:
                                        case 4:
                                        case 5:
                                            lDropItem.set_location((short) 0);
                                            lDropItem.set_body_position((short) 0);
                                            lDropItem.set_row((short) lItemPanel.getColumn());
                                            lDropItem.set_col((short) lItemPanel.getRow());
                                            lDropItem.set_panel((short) lItemPanel.getPanel());
                                            break;
                                        }
                                        drop = true;
                                    }
                                }
                                // equipped items, a bit simpler
                                // if that equipment slot is empty, update the
                                // item's fields and set drop to true
                                // r and c are set to width and height
                                // for find_corner to deal with variable-size
                                // objects in the hands
                                // (note lack of item-type checking)
                                else
                                {
                                    if (!iCharacter.checkCharPanel(lItemPanel.getPanel(), 0, 0, lDropItem))
                                    {
                                        lDropItem.set_location((short) 1);
                                        lDropItem.set_body_position((short) (lItemPanel.getPanel() - 10));
                                        lDropItem.set_col((short) 0);
                                        lDropItem.set_row((short) 0);
                                        lDropItem.set_panel((short) 0);
                                        drop = true;
                                        //	                                r = lDropWidth;
                                        //	                                c = lDropHeight;
                                    }
                                }
                                // if the space to set the item is empty
                                if (drop)
                                {
                                    iCharacter.markCharGrid(lDropItem);
                                    // move the item to a new charcter, if
                                    // needed
                                    iCharacter.addCharItem(D2ViewClipboard.removeItem());

                                    // redraw
//                                    build();
//                                    repaint();

                                    setCursorPickupItem();
                                    //my_char.show_grid();
                                }
                            }
                        }
                    }
                }

                public void mouseEntered(MouseEvent e)
                {
                    setCursorNormal();
                }

                public void mouseExited(MouseEvent e)
                {
                    setCursorNormal();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter()
            {
                public void mouseMoved(MouseEvent pEvent)
                {
                    if ( iCharacter == null )
                    {
                        return;
                    }
                    //            	    restoreSubcomponentFocus();
                    D2Item lCurrentMouse = null;

                    D2ItemPanel lItemPanel = new D2ItemPanel(pEvent, true, false);
                    if (lItemPanel.getPanel() != -1)
                    {
                        if (lItemPanel.isItem())
                        {
                            lCurrentMouse = lItemPanel.getItem();
                        }

                        if (lItemPanel.isItem())
                        {
                            setCursorPickupItem();
                        }
                        else
                        {
                            if (D2ViewClipboard.getItem() == null)
                            {
                                setCursorNormal();
                            }
                            else
                            {
                                D2Item lDropItem = D2ViewClipboard.getItem();
                                //	                        int lDropWidth = lDropItem.get_width();
                                //	                        int lDropHeight = lDropItem.get_height();

                                boolean drop = false;

                                if (lItemPanel.getPanel() < 10)
                                {
                                    if (iCharacter.checkCharGrid(lItemPanel.getPanel(), lItemPanel.getRow(), lItemPanel.getColumn(), lDropItem))
                                    {
                                        drop = true;
                                    }
                                }
                                else
                                {
                                    if (!iCharacter.checkCharPanel(lItemPanel.getPanel(), 0, 0, lDropItem))
                                    {
                                        drop = true;
                                    }
                                }
                                if (drop)
                                {
                                    setCursorDropItem();
                                }
                                else
                                {
                                    setCursorNormal();
                                }
                            }
                        }
                    }
                    else
                    {
                        setCursorNormal();
                    }
                    if (lCurrentMouse == null)
                    {
                        D2CharPainterPanel.this.setToolTipText(null);
                    }
                    else
                    {
                        D2CharPainterPanel.this.setToolTipText(lCurrentMouse.toStringHtml());
                    }
                }
            });
        }

        public void setWeaponSlot(int pWeaponSlot)
        {
            iWeaponSlot = pWeaponSlot;
            build();
//            repaint();
        }

        public void build()
        {
            Image lEmptyBackground;
            if (iWeaponSlot == 1)
            {
                lEmptyBackground = D2ImageCache.getImage("background.png");
            }
            else
            {
                lEmptyBackground = D2ImageCache.getImage("background2.png");
            }
            
            int lWidth = lEmptyBackground.getWidth(D2CharPainterPanel.this);
            int lHeight = lEmptyBackground.getHeight(D2CharPainterPanel.this);
            
            iBackground = iFileManager.getGraphicsConfiguration().createCompatibleImage(lWidth, lHeight, Transparency.BITMASK);
//            iBackground = new BufferedImage(lWidth, lHeight, BufferedImage.TYPE_3BYTE_BGR);

            Graphics2D lGraphics = (Graphics2D) iBackground.getGraphics();

            lGraphics.drawImage(lEmptyBackground, 0, 0, D2CharPainterPanel.this);

            if ( iCharacter != null )
            {
	            for (int i = 0; i < iCharacter.getCharItemNr(); i++)
	            {
	                D2Item temp_item = iCharacter.getCharItem(i);
	                Image lImage = D2ImageCache.getDC6Image(temp_item);
	                int location = temp_item.get_location();
	                // on one of the grids
	                // these items have varying height and width
	                // and a variable position, indexed from the
	                // top left
	                if (location == 0)
	                {
	                    int panel = temp_item.get_panel();
	                    int x = temp_item.get_col();
	                    int y = temp_item.get_row();
	                    int w = temp_item.get_width();
	                    int h = temp_item.get_height();
	                    switch (panel)
	                    {
	                    // in the inventory
	                    case 1:
	                        //                    	System.err.println("Item loc 0 - 1 - " +
	                        // temp_item.get_name() + " - " + temp_item.get_image()
	                        // );
	                        lGraphics.drawImage(lImage, INV_X + x * GRID_SIZE + x * GRID_SPACER, INV_Y + y * GRID_SIZE + y * GRID_SPACER, D2CharPainterPanel.this);
	                        break;
	                    // in the cube
	                    case 4:
	                        lGraphics.drawImage(lImage, CUBE_X + x * GRID_SIZE + x * GRID_SPACER, CUBE_Y + y * GRID_SIZE + y * GRID_SPACER, D2CharPainterPanel.this);
	                        break;
	                    // in the stash
	                    case 5:
	                        lGraphics.drawImage(lImage, STASH_X + x * GRID_SIZE + x * GRID_SPACER, STASH_Y + y * GRID_SIZE + y * GRID_SPACER, D2CharPainterPanel.this);
	                        break;
	                    }
	                }
	                // on the belt
	                // belt row and col is indexed from the top
	                // left, but this displays them from the
	                // bottom right (so the 0th row items get
	                // placed in the bottom belt row)
	                // these items can all be assumed to be 1x1
	                else if (location == 2)
	                {
	                    int x = temp_item.get_col();
	                    int y = x / 4;
	                    x = x % 4;
	                    lGraphics.drawImage(lImage, BELT_GRID_X + x * GRID_SIZE + x * GRID_SPACER, BELT_GRID_Y + (3 - y) * GRID_SIZE + (3 - y) * GRID_SPACER, D2CharPainterPanel.this);
	                }
	                // on the body
	                else
	                {
	                    int body_position = temp_item.get_body_position();
	                    int w, h, wbias, hbias;
	                    switch (body_position)
	                    {
	                    // head (assume 2x2)
	                    case 1:
	                        lGraphics.drawImage(lImage, HEAD_X, HEAD_Y, D2CharPainterPanel.this);
	                        break;
	                    // neck / amulet (assume 1x1)
	                    case 2:
	                        lGraphics.drawImage(lImage, NECK_X, NECK_Y, D2CharPainterPanel.this);
	                        break;
	                    case 3:
	                        // body (assume 2x3
	                        lGraphics.drawImage(lImage, BODY_X, BODY_Y, D2CharPainterPanel.this);
	                        break;
	                    // right arm (give the whole 2x4)
	                    // biases are to center non-2x4 items
	                    case 4:
	                    case 11:
	                        if ((iWeaponSlot == 1 && body_position == 4) || (iWeaponSlot == 2 && body_position == 11))
	                        {
	                            w = temp_item.get_width();
	                            h = temp_item.get_height();
	                            wbias = 0;
	                            hbias = 0;
	                            if (w == 1)
	                                wbias += GRID_SIZE / 2;
	                            if (h == 3)
	                                hbias += GRID_SIZE / 2;
	                            else if (h == 2)
	                                hbias += GRID_SIZE;
	                            lGraphics.drawImage(lImage, R_ARM_X + wbias, R_ARM_Y + hbias, D2CharPainterPanel.this);
	                        }
	                        break;
	                    // left arm (give the whole 2x4)
	                    case 5:
	                    case 12:
	                        if ((iWeaponSlot == 1 && body_position == 5) || (iWeaponSlot == 2 && body_position == 12))
	                        {
	                            w = temp_item.get_width();
	                            h = temp_item.get_height();
	                            wbias = 0;
	                            hbias = 0;
	                            if (w == 1)
	                                wbias += GRID_SIZE / 2;
	                            if (h == 3)
	                                hbias += GRID_SIZE / 2;
	                            else if (h == 2)
	                                hbias += GRID_SIZE;
	                            lGraphics.drawImage(lImage, L_ARM_X + wbias, L_ARM_Y + hbias, D2CharPainterPanel.this);
	                        }
	                        break;
	                    // left ring (assume 1x1)
	                    case 6:
	                        lGraphics.drawImage(lImage, L_RING_X, L_RING_Y, D2CharPainterPanel.this);
	                        break;
	                    // right ring (assume 1x1)
	                    case 7:
	                        lGraphics.drawImage(lImage, R_RING_X, R_RING_Y, D2CharPainterPanel.this);
	                        break;
	                    // belt (assume 2x1)
	                    case 8:
	                        lGraphics.drawImage(lImage, BELT_X, BELT_Y, D2CharPainterPanel.this);
	                        break;
	                    case 9:
	                        // boots (assume 2x2)
	                        lGraphics.drawImage(lImage, BOOTS_X, BOOTS_Y, D2CharPainterPanel.this);
	                        break;
	                    // gloves (assume 2x2)
	                    case 10:
	                        lGraphics.drawImage(lImage, GLOVES_X, GLOVES_Y, D2CharPainterPanel.this);
	                        break;
	                    }
	                }
	            }
            }
            repaint();
        }

        public void paint(Graphics pGraphics)
        {
            super.paint(pGraphics);
            Graphics2D lGraphics = (Graphics2D) pGraphics;

            lGraphics.drawImage(iBackground, 0, 0, D2CharPainterPanel.this);
        }
    }

    class D2MercPainterPanel extends JPanel
    {
        private Image iBackground;

        public D2MercPainterPanel()
        {
            setSize(BG_MERC_WIDTH, BG_MERC_HEIGHT);
            Dimension lSize = new Dimension(BG_MERC_WIDTH, BG_MERC_HEIGHT);
            setPreferredSize(lSize);

            addMouseListener(new MouseAdapter()
            {
                public void mouseReleased(MouseEvent pEvent)
                {
                    if ( iCharacter == null )
                    {
                        return;
                    }
                    //                  System.err.println("Mouse Clicked: " + pEvent.getX() + ",
                    // " + pEvent.getY() );
                    if (pEvent.getButton() == MouseEvent.BUTTON1 /*
                                                                  * &&
                                                                  * pEvent.getClickCount() ==
                                                                  * 1
                                                                  */)
                    {
                        // determine where the mouse click is
                        D2ItemPanel lItemPanel = new D2ItemPanel(pEvent, false, false);
                        if (lItemPanel.getPanel() != -1)
                        {
                            // if there is an item to grab, grab it
                            if (lItemPanel.isItem())
                            {
                                D2Item lTemp = lItemPanel.getItem();
                                iCharacter.unmarkMercGrid(lTemp);
                                iCharacter.removeMercItem(lItemPanel.getItemIndex());
                                D2ViewClipboard.addItem(lTemp);
                                setCursorDropItem();

                                // redraw
//                                build();
//                                repaint();
                            }
                            else if (D2ViewClipboard.getItem() != null)
                            {
                                //	                    	System.err.println("Drop item");
                                // since there is an item on the mouse, try to
                                // drop it here

                                D2Item lDropItem = D2ViewClipboard.getItem();
                                //		                        int lDropWidth = lDropItem.get_width();
                                //		                        int lDropHeight = lDropItem.get_height();
                                //	                        int r = 0, c = 0;
                                boolean drop = false;
                                // equipped items, a bit simpler
                                // if that equipment slot is empty, update the
                                // item's fields and set drop to true
                                // r and c are set to width and height
                                // for find_corner to deal with variable-size
                                // objects in the hands
                                // (note lack of item-type checking)
                                if (!iCharacter.checkMercPanel(lItemPanel.getPanel(), 0, 0, lDropItem))
                                {
                                    lDropItem.set_location((short) 1);
                                    lDropItem.set_body_position((short) (lItemPanel.getPanel() - 10));
                                    lDropItem.set_col((short) 0);
                                    lDropItem.set_row((short) 0);
                                    lDropItem.set_panel((short) 0);
                                    drop = true;
                                    //	                                r = lDropWidth;
                                    //	                                c = lDropHeight;
                                }
                                // if the space to set the item is empty
                                if (drop)
                                {
                                    iCharacter.markMercGrid(lDropItem);
                                    // move the item to a new charcter, if
                                    // needed
                                    iCharacter.addMercItem(D2ViewClipboard.removeItem());

                                    // redraw
//                                    build();
//                                    repaint();

                                    setCursorPickupItem();
                                    //my_char.show_grid();
                                }
                            }
                        }
                    }
                }

                public void mouseEntered(MouseEvent e)
                {
                    setCursorNormal();
                }

                public void mouseExited(MouseEvent e)
                {
                    setCursorNormal();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter()
            {
                public void mouseMoved(MouseEvent pEvent)
                {
                    if ( iCharacter == null )
                    {
                        return;
                    }
                    //            	    restoreSubcomponentFocus();
                    D2Item lCurrentMouse = null;

                    D2ItemPanel lItemPanel = new D2ItemPanel(pEvent, false, false);
                    if (lItemPanel.getPanel() != -1)
                    {
                        if (lItemPanel.isItem())
                        {
                            lCurrentMouse = lItemPanel.getItem();
                        }

                        if (lItemPanel.isItem())
                        {
                            setCursorPickupItem();
                        }
                        else
                        {
                            if (D2ViewClipboard.getItem() == null)
                            {
                                setCursorNormal();
                            }
                            else
                            {
                                D2Item lDropItem = D2ViewClipboard.getItem();
                                //	                        int lDropWidth = lDropItem.get_width();
                                //	                        int lDropHeight = lDropItem.get_height();

                                boolean drop = false;

                                if (!iCharacter.checkMercPanel(lItemPanel.getPanel(), 0, 0, lDropItem))
                                {
                                    drop = true;
                                }
                                if (drop)
                                {
                                    setCursorDropItem();
                                }
                                else
                                {
                                    setCursorNormal();
                                }
                            }
                        }
                    }
                    else
                    {
                        setCursorNormal();
                    }
                    if (lCurrentMouse == null)
                    {
                        D2MercPainterPanel.this.setToolTipText(null);
                    }
                    else
                    {
                        D2MercPainterPanel.this.setToolTipText(lCurrentMouse.toStringHtml());
                    }
                }
            });
        }

        public void build()
        {
            Image lEmptyBackground = D2ImageCache.getImage("merc.png");

            int lWidth = lEmptyBackground.getWidth(D2MercPainterPanel.this);
            int lHeight = lEmptyBackground.getHeight(D2MercPainterPanel.this);
            
            iBackground = iFileManager.getGraphicsConfiguration().createCompatibleImage(lWidth, lHeight, Transparency.BITMASK);
//            iBackground = new BufferedImage(lEmptyBackground.getWidth(lWidth, lHeight, BufferedImage.TYPE_3BYTE_BGR);

            Graphics2D lGraphics = (Graphics2D) iBackground.getGraphics();

            lGraphics.drawImage(lEmptyBackground, 0, 0, D2MercPainterPanel.this);

            if ( iCharacter != null )
            {
	            for (int i = 0; i < iCharacter.getMercItemNr(); i++)
	            {
	                D2Item temp_item = iCharacter.getMercItem(i);
	                Image lImage = D2ImageCache.getDC6Image(temp_item);
	                int location = temp_item.get_location();
	                // on the body
	                {
	                    int body_position = temp_item.get_body_position();
	                    int w, h, wbias, hbias;
	                    switch (body_position)
	                    {
	                    // head (assume 2x2)
	                    case 1:
	                        lGraphics.drawImage(lImage, HEAD_X, HEAD_Y, D2MercPainterPanel.this);
	                        break;
	                    case 3:
	                        // body (assume 2x3
	                        lGraphics.drawImage(lImage, BODY_X, BODY_Y, D2MercPainterPanel.this);
	                        break;
	                    // right arm (give the whole 2x4)
	                    // biases are to center non-2x4 items
	                    case 4:
	                        if ((iWeaponSlot == 1 && body_position == 4) || (iWeaponSlot == 2 && body_position == 11))
	                        {
	                            w = temp_item.get_width();
	                            h = temp_item.get_height();
	                            wbias = 0;
	                            hbias = 0;
	                            if (w == 1)
	                                wbias += GRID_SIZE / 2;
	                            if (h == 3)
	                                hbias += GRID_SIZE / 2;
	                            else if (h == 2)
	                                hbias += GRID_SIZE;
	                            lGraphics.drawImage(lImage, R_ARM_X + wbias, R_ARM_Y + hbias, D2MercPainterPanel.this);
	                        }
	                        break;
	                    // left arm (give the whole 2x4)
	                    case 5:
	                        if ((iWeaponSlot == 1 && body_position == 5) || (iWeaponSlot == 2 && body_position == 12))
	                        {
	                            w = temp_item.get_width();
	                            h = temp_item.get_height();
	                            wbias = 0;
	                            hbias = 0;
	                            if (w == 1)
	                                wbias += GRID_SIZE / 2;
	                            if (h == 3)
	                                hbias += GRID_SIZE / 2;
	                            else if (h == 2)
	                                hbias += GRID_SIZE;
	                            lGraphics.drawImage(lImage, L_ARM_X + wbias, L_ARM_Y + hbias, D2MercPainterPanel.this);
	                        }
	                        break;
	                    }
	                }
	            }
            }
            repaint();
        }

        public void paint(Graphics pGraphics)
        {
            super.paint(pGraphics);
            Graphics2D lGraphics = (Graphics2D) pGraphics;

            lGraphics.drawImage(iBackground, 0, 0, D2MercPainterPanel.this);
        }
    }

    class D2CharCursorPainterPanel extends JPanel
    {
        private Image iBackground;

        public D2CharCursorPainterPanel()
        {
            setSize(BG_CURSOR_WIDTH, BG_CURSOR_HEIGHT);
            Dimension lSize = new Dimension(BG_CURSOR_WIDTH, BG_CURSOR_HEIGHT);
            setPreferredSize(lSize);

            addMouseListener(new MouseAdapter()
            {
                public void mouseReleased(MouseEvent pEvent)
                {
                    if ( iCharacter == null )
                    {
                        return;
                    }
                    //                    System.err.println("Mouse Clicked: " + pEvent.getX() + ",
                    // " + pEvent.getY() );
                    if (pEvent.getButton() == MouseEvent.BUTTON1 /*
                                                                  * &&
                                                                  * pEvent.getClickCount() ==
                                                                  * 1
                                                                  */)
                    {
                        // determine where the mouse click is
                        D2ItemPanel lItemPanel = new D2ItemPanel(pEvent, true, true);
                        if (lItemPanel.getPanel() != -1)
                        {
                            // if there is an item to grab, grab it
                            if (lItemPanel.isItem())
                            {
                                D2Item lTemp = lItemPanel.getItem();
                                iCharacter.unmarkCharGrid(lTemp);
                                iCharacter.removeCharItem(lItemPanel.getItemIndex());
                                D2ViewClipboard.addItem(lTemp);
                                setCursorDropItem();

                                // redraw
//                                build();
//                                repaint();
                            }
                            else if (D2ViewClipboard.getItem() != null)
                            {
                                // MBR: for now, disable dropping completely,
                                // it's not working
                                //	// System.err.println("Drop item");
                                //		                        // since there is an item on the mouse, try
                                // to drop it here
                                //		                    	
                                //		                        D2Item lDropItem = D2MouseItem.getItem();
                                //// int lDropWidth = lDropItem.get_width();
                                //// int lDropHeight = lDropItem.get_height();
                                //	// int r = 0, c = 0;
                                //		                        boolean drop = false;
                                //		                        // non-equipped items, handle differently
                                //		                        // because they require a row and column
                                //		                        // equipped items, a bit simpler
                                //		                        // if that equipment slot is empty, update
                                // the
                                //		                        // item's fields and set drop to true
                                //		                        // r and c are set to width and height
                                //		                        // for find_corner to deal with variable-size
                                //		                        // objects in the hands
                                //		                        // (note lack of item-type checking)
                                //	                            if
                                // (!iChar.checkCharPanel(lItemPanel.getPanel(),
                                // 0, 0, lDropItem))
                                //	                            {
                                //	                            	lDropItem.set_location((short) 1);
                                //	                            	lDropItem.set_body_position((short)
                                // (lItemPanel.getPanel() - 10));
                                //	                            	lDropItem.set_col((short) 0);
                                //	                            	lDropItem.set_row((short) 0);
                                //	                            	lDropItem.set_panel((short) 0);
                                //	                                drop = true;
                                //// r = lDropWidth;
                                //// c = lDropHeight;
                                //	                            }
                                //		                        // if the space to set the item is empty
                                //		                        if (drop)
                                //		                        {
                                //		                            iChar.markCharGrid(lDropItem);
                                //		                            // move the item to a new charcter, if needed
                                //	                                iChar.addCharItem(D2MouseItem.removeItem());
                                //		
                                //		                            setModified( true );
                                //		
                                //		                            // redraw
                                //		                            build();
                                //		                            repaint();
                                //		                            
                                //		                            setCursorPickupItem();
                                //		                            //my_char.show_grid();
                                //		                        }
                            }
                        }
                    }
                }

                public void mouseEntered(MouseEvent e)
                {
                    setCursorNormal();
                }

                public void mouseExited(MouseEvent e)
                {
                    setCursorNormal();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter()
            {
                public void mouseMoved(MouseEvent pEvent)
                {
                    if ( iCharacter == null )
                    {
                        return;
                    }
                    //            	    restoreSubcomponentFocus();
                    D2Item lCurrentMouse = null;

                    D2ItemPanel lItemPanel = new D2ItemPanel(pEvent, true, true);
                    if (lItemPanel.getPanel() != -1)
                    {
                        if (lItemPanel.isItem())
                        {
                            lCurrentMouse = lItemPanel.getItem();
                        }

                        if (lItemPanel.isItem())
                        {
                            setCursorPickupItem();
                        }
                        else
                        {
                            if (D2ViewClipboard.getItem() == null)
                            {
                                setCursorNormal();
                            }
                            else
                            {
                                setCursorNormal();

                                // MBR: for now, disable dropping completely
                                //		                        D2Item lDropItem = D2MouseItem.getItem();
                                //	// int lDropWidth = lDropItem.get_width();
                                //	// int lDropHeight = lDropItem.get_height();
                                //	
                                //		                        boolean drop = false;
                                //	
                                //	                            if
                                // (!iChar.checkCharPanel(lItemPanel.getPanel(),
                                // 0, 0, lDropItem))
                                //	                            {
                                //	                                drop = true;
                                //	                            }
                                //		                        if (drop)
                                //		                        {
                                //		                        	setCursorDropItem();
                                //		                        }
                                //		                        else
                                //		                        {
                                //		                        	setCursorNormal();
                                //		                        }
                            }
                        }
                    }
                    else
                    {
                        setCursorNormal();
                    }
                    if (lCurrentMouse == null)
                    {
                        D2CharCursorPainterPanel.this.setToolTipText(null);
                    }
                    else
                    {
                        D2CharCursorPainterPanel.this.setToolTipText(lCurrentMouse.toStringHtml());
                    }
                }
            });
        }

        public void build()
        {
            Image lEmptyBackground = D2ImageCache.getImage("cursor.png");
            
            int lWidth = lEmptyBackground.getWidth(D2CharCursorPainterPanel.this);
            int lHeight = lEmptyBackground.getHeight(D2CharCursorPainterPanel.this);
            
            iBackground = iFileManager.getGraphicsConfiguration().createCompatibleImage(lWidth, lHeight, Transparency.BITMASK);
//            iBackground = new BufferedImage(lWidth, lHeight, BufferedImage.TYPE_3BYTE_BGR);

            Graphics2D lGraphics = (Graphics2D) iBackground.getGraphics();

            lGraphics.drawImage(lEmptyBackground, 0, 0, D2CharCursorPainterPanel.this);

            if ( iCharacter != null )
            {
	            for (int i = 0; i < iCharacter.getCharItemNr(); i++)
	            {
	                D2Item temp_item = iCharacter.getCharItem(i);
	                Image lImage = D2ImageCache.getDC6Image(temp_item);
	                int location = temp_item.get_location();
	                if (location == 0)
	                {
	                    // do nothing
	                }
	                else if (location == 2)
	                {
	                    // do nothing
	                }
	                else
	                // on the body
	                {
	                    int body_position = temp_item.get_body_position();
	                    int w, h, wbias, hbias;
	                    if (body_position == 0)
	                    {
	                        lGraphics.drawImage(lImage, CURSOR_X, CURSOR_Y, D2CharCursorPainterPanel.this);
	                    }
	                }
	            }
            }
            repaint();
        }

        public void paint(Graphics pGraphics)
        {
            super.paint(pGraphics);
            Graphics2D lGraphics = (Graphics2D) pGraphics;

            lGraphics.drawImage(iBackground, 0, 0, D2CharCursorPainterPanel.this);
        }
    }

}