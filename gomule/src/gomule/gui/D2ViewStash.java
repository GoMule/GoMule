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

import gomule.d2x.*;
import gomule.item.*;
import gomule.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import randall.util.*;

/**
 * @author Marco
 */
public class D2ViewStash extends JInternalFrame implements D2ItemContainer, D2ItemListListener
{
    private D2FileManager iFileManager;
    private D2ItemList    iStash;
    private String        iFileName;

    private D2StashFilter iStashFilter;
    private boolean		  iIgnoreItemListEvents;
    private D2ItemModel   iItemModel;
    private JTable        iTable;

    private JPanel        iContentPane;

    private JTextArea     iItemText;

    // item types
    private JCheckBox     iTypeUnique;
    private JCheckBox     iTypeSet;
    private JCheckBox     iTypeRuneWord;
    private JCheckBox     iTypeRare;
    private JCheckBox     iTypeMagical;
    private JCheckBox     iTypeCrafted;
    private JCheckBox     iTypeOther;

    // item categories
    private JRadioButton  iCatArmor;
    private JRadioButton  iCatWeapons;
    private JRadioButton  iCatSocket;
    private JRadioButton  iCatCharm;
    private JRadioButton  iCatMisc;
    private JRadioButton  iCatAll;
    
    private RandallPanel  iArmorFilter;
    private ArrayList	  iArmorFilterList;
    
    private RandallPanel  iWeaponFilter;
    private ArrayList	  iWeaponFilterList;
    
    private RandallPanel  iSocketFilter;
    private JRadioButton  iCatSocketJewel;
    private JRadioButton  iCatSocketGem;
    private JRadioButton  iCatSocketRune;
    private JRadioButton  iCatSocketAll;

    private RandallPanel  iCharmFilter;
    private JRadioButton  iCatCharmSmall;
    private JRadioButton  iCatCharmLarge;
    private JRadioButton  iCatCharmGrand;
    private JRadioButton  iCatCharmAll;

    private RandallPanel  iMiscFilter;
    private JRadioButton  iCatMiscAmulet;
    private JRadioButton  iCatMiscRing;
    private JRadioButton  iCatMiscOther;
    private JRadioButton  iCatMiscAll;
    
    private RandallPanel  iRequerementFilter;
    private JTextField	  iReqMaxLvl;
    private JTextField	  iReqMaxStr;
    private JTextField	  iReqMaxDex;

    public D2ViewStash(D2FileManager pMainFrame, String pFileName)
    {
        super(pFileName, true, true, false, true);
        
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

        iContentPane = new JPanel();
        iContentPane.setLayout(new BorderLayout());

        try
        {
            iStash = new D2Stash(pFileName);
            iStash.addD2ItemListListener(this);
            
            int lType = iFileManager.getProject().getType();
            if ( lType == D2Project.TYPE_SC && (!iStash.isSC() || iStash.isHC()) )
            {
                throw new Exception("Stash is not Softcore (SC), this is a project requirement");
            }
            if ( lType == D2Project.TYPE_HC && (iStash.isSC() || !iStash.isHC()) )
            {
                throw new Exception("Stash is not Hardcore (HC), this is a project requirement");
            }
            
            
            iStashFilter = new D2StashFilter();
            iItemModel = new D2ItemModel();
            iTable = new JTable(iItemModel);
            
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
            iTable.getColumnModel().getColumn(0).setPreferredWidth(200);
            iTable.getColumnModel().getColumn(1).setPreferredWidth(11);
            iTable.getColumnModel().getColumn(2).setPreferredWidth(11);
            iTable.getColumnModel().getColumn(3).setPreferredWidth(11);
            JScrollPane lPane = new JScrollPane(iTable);
            lPane.setPreferredSize(new Dimension(280, 100));
            iContentPane.add(lPane, BorderLayout.WEST);

            RandallPanel lButtonPanel = getButtonPanel();
            JPanel lTypePanel = getTypePanel();
            RandallPanel lCategoryPanel = getCategoryPanel();

            iRequerementFilter = new RandallPanel();
            iReqMaxLvl = new JTextField();
            iReqMaxLvl.getDocument().addDocumentListener(iStashFilter);
            iReqMaxStr = new JTextField();
            iReqMaxStr.getDocument().addDocumentListener(iStashFilter);
            iReqMaxDex = new JTextField();
            iReqMaxDex.getDocument().addDocumentListener(iStashFilter);
            
            iRequerementFilter.addToPanel(new JLabel("MaxLvl"), 0, 0, 1, RandallPanel.NONE);
            iRequerementFilter.addToPanel(iReqMaxLvl, 1, 0, 1, RandallPanel.HORIZONTAL);
            iRequerementFilter.addToPanel(new JLabel("MaxStr"), 2, 0, 1, RandallPanel.NONE);
            iRequerementFilter.addToPanel(iReqMaxStr, 3, 0, 1, RandallPanel.HORIZONTAL);
            iRequerementFilter.addToPanel(new JLabel("MaxDex"), 4, 0, 1, RandallPanel.NONE);
            iRequerementFilter.addToPanel(iReqMaxDex, 5, 0, 1, RandallPanel.HORIZONTAL);
            
            RandallPanel lTopPanel = new RandallPanel();
            lTopPanel.addToPanel(lButtonPanel, 0, 0, 1, RandallPanel.HORIZONTAL);
            lTopPanel.addToPanel(lTypePanel, 0, 1, 1, RandallPanel.HORIZONTAL);
            lTopPanel.addToPanel(lCategoryPanel, 0, 2, 1, RandallPanel.HORIZONTAL);
            lTopPanel.addToPanel(iRequerementFilter, 0, 3, 1, RandallPanel.HORIZONTAL);
            

            iContentPane.add(lTopPanel, BorderLayout.NORTH);

            JPanel lItemPanel = new JPanel();
            iItemText = new JTextArea();
            JScrollPane lItemScroll = new JScrollPane(iItemText);
            lItemPanel.setLayout(new BorderLayout());
            lItemPanel.add(lItemScroll, BorderLayout.CENTER);
            lItemPanel.setPreferredSize(new Dimension(250, 100));

            iContentPane.add(lItemPanel, BorderLayout.CENTER);
        }
        catch (Exception pEx)
        {
            D2FileManager.displayErrorDialog(pEx);
            JTextArea lError = new JTextArea();
            JScrollPane lScroll = new JScrollPane(lError);
            lError.setText(pEx.getMessage());
            iContentPane.add(lError, BorderLayout.CENTER);
        }

        setContentPane(iContentPane);

        pack();
        setSize(630, 500);
        setVisible(true);

        itemListChanged();

        if (iTable != null)
        {
            iTable.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener()
                    {
                        public void valueChanged(ListSelectionEvent e)
                        {
                            if (iTable.getSelectedRowCount() == 1)
                            {
                                iItemText.setText(iItemModel.getItem(
                                        iTable.getSelectedRow()).toString());
                            }
                            else
                            {
                                iItemText.setText("");
                            }
                        }
                    });
        }
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

        JButton lPickup = new JButton("Pickup");
        lPickup.addActionListener(new ActionListener()
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
                        iIgnoreItemListEvents = true;
                        for (int i = 0; i < lItemList.size(); i++)
                        {
                            iStash.removeItem((D2Item) lItemList.get(i));
                            D2ViewClipboard.addItem((D2Item) lItemList.get(i));
                        }
                    }
                    finally
                    {
                        iIgnoreItemListEvents = false;
                    }
                    itemListChanged();
                }
            }
        });
        lButtonPanel.addToPanel(lPickup, 0, 0, 1, RandallPanel.HORIZONTAL);

        if ( iStash instanceof D2Stash )
        {
	        JButton lDropOne = new JButton("Drop");
	        lDropOne.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent pEvent)
	            {
	                ((D2Stash) iStash).addItem(D2ViewClipboard.removeItem());
	            }
	        });
	        lButtonPanel.addToPanel(lDropOne, 1, 0, 1, RandallPanel.HORIZONTAL);
        }

        if ( iStash instanceof D2Stash )
        {
	        JButton lDropAll = new JButton("Drop All");
	        lDropAll.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent pEvent)
	            {
                    try
                    {
                        iIgnoreItemListEvents = true;
		                ArrayList lItemList = D2ViewClipboard.removeAllItems();
		                while (lItemList.size() > 0)
		                {
		                    ((D2Stash) iStash).addItem((D2Item) lItemList.remove(0));
		                }
                    }
                    finally
                    {
                        iIgnoreItemListEvents = false;
                    }
                    itemListChanged();
	            }
	        });
	        lButtonPanel.addToPanel(lDropAll, 2, 0, 1, RandallPanel.HORIZONTAL);
        }

        return lButtonPanel;
    }

    private RandallPanel getTypePanel()
    {
        RandallPanel lTypePanel = new RandallPanel(true);

        iTypeUnique = new JCheckBox("Unique");
        iTypeUnique.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeUnique, 0, 0, 1, RandallPanel.HORIZONTAL);
        iTypeSet = new JCheckBox("Set");
        iTypeSet.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeSet, 1, 0, 1, RandallPanel.HORIZONTAL);
        iTypeRuneWord = new JCheckBox("Runeword");
        iTypeRuneWord.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeRuneWord, 2, 0, 1, RandallPanel.HORIZONTAL);
        iTypeRare = new JCheckBox("Rare");
        iTypeRare.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeRare, 3, 0, 1, RandallPanel.HORIZONTAL);
        iTypeMagical = new JCheckBox("Magical");
        iTypeMagical.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeMagical, 4, 0, 1, RandallPanel.HORIZONTAL);
        iTypeCrafted = new JCheckBox("Crafted");
        iTypeCrafted.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeCrafted, 5, 0, 1, RandallPanel.HORIZONTAL);
        iTypeOther = new JCheckBox("Other");
        iTypeOther.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeOther, 6, 0, 1, RandallPanel.HORIZONTAL);

        return lTypePanel;
    }

    private RandallPanel getCategoryPanel()
    {
        RandallPanel lCategoryPanel = new RandallPanel();

        RandallPanel lCategories = getCategories();

        lCategoryPanel
                .addToPanel(lCategories, 0, 0, 1, RandallPanel.HORIZONTAL);

        return lCategoryPanel;
    }

    private RandallPanel getCategories()
    {
        ButtonGroup lCatBtnGroup = new ButtonGroup();
        RandallPanel lCategories = new RandallPanel(true);

        int lRow = 0;
        
        iCatArmor = new JRadioButton("Armor");
        iCatArmor.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatArmor, 0, lRow, 1, 0.7, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatArmor);

        iCatWeapons = new JRadioButton("Weapon");
        iCatWeapons.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatWeapons, 1, lRow, 1, 0.7, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatWeapons);

        iCatSocket = new JRadioButton("Socket Filler");
        iCatSocket.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatSocket, 2, lRow, 1,
                RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatSocket);

        iCatCharm = new JRadioButton("Charm");
        iCatCharm.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatCharm, 3, lRow, 1, 0.6, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatCharm);

        iCatMisc = new JRadioButton("Misc");
        iCatMisc.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatMisc, 4, lRow, 1, 0.5, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatMisc);

        iCatAll = new JRadioButton("All");
        iCatAll.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatAll, 5, lRow, 1, 0.5, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatAll);

        iCatAll.setSelected(true);
        lRow++;
        
        iArmorFilter = getCategoriesArmor();
        iArmorFilter.setVisible(false);
        lCategories.addToPanel(iArmorFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);
        
        iWeaponFilter = getCategoriesWeapon();
        iWeaponFilter.setVisible(false);
        lCategories.addToPanel(iWeaponFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);
        
        iSocketFilter = getCategoriesSocket();
        iSocketFilter.setVisible(false);
        lCategories.addToPanel(iSocketFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);
        
        iCharmFilter = getCategoriesCharm();
        iCharmFilter.setVisible(false);
        lCategories.addToPanel(iCharmFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);
        
        iMiscFilter = getCategoriesMisc();
        iMiscFilter.setVisible(false);
        lCategories.addToPanel(iMiscFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);
        
        return lCategories;
    }
    
    private RandallPanel getCategoriesArmor()
    {
        ButtonGroup lCatArmorBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesArmor = new RandallPanel(true);
        
        iArmorFilterList = new ArrayList();
        ArrayList lArmorFilterList = D2BodyLocations.getArmorFilterList();
        for ( int i = 0 ; i < lArmorFilterList.size() ; i++ )
        {
            D2BodyLocations lArmor = (D2BodyLocations) lArmorFilterList.get(i);
            D2RadioButton lBtn = new D2RadioButton(lArmor);
            lBtn.addActionListener(iStashFilter);
            lCategoriesArmor.addToPanel(lBtn, i, 0, 1, RandallPanel.HORIZONTAL);
            lCatArmorBtnGroup.add(lBtn);
            if ( lArmor == D2BodyLocations.BODY_ALL )
            {
                lBtn.setSelected(true);
            }
            iArmorFilterList.add(lBtn);
        }
        
        return lCategoriesArmor;
    }

    private RandallPanel getCategoriesWeapon()
    {
        ButtonGroup lCatWeaponBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesWeapon = new RandallPanel(true);
        
        int lCurrentRowNr = 0;
        RandallPanel lCurrentRow = new RandallPanel(true);
        lCategoriesWeapon.addToPanel(lCurrentRow, 0, lCurrentRowNr++, 1, RandallPanel.HORIZONTAL);
        
        iWeaponFilterList = new ArrayList();
        ArrayList lWeaponFilterList = D2WeaponTypes.getWeaponTypeList();
        for ( int i = 0 ; i < lWeaponFilterList.size() ; i++ )
        {
            if ( lWeaponFilterList.get(i) instanceof D2WeaponTypes )
            {
	            D2WeaponTypes lWeapon = (D2WeaponTypes) lWeaponFilterList.get(i);
	            D2RadioButton lBtn = new D2RadioButton(lWeapon);
	            lBtn.addActionListener(iStashFilter);
	            lCurrentRow.addToPanel(lBtn, i, 0, 1, RandallPanel.HORIZONTAL);
	            lCatWeaponBtnGroup.add(lBtn);
	            if ( lWeapon == D2WeaponTypes.WEAP_ALL )
	            {
	                lBtn.setSelected(true);
	            }
	            iWeaponFilterList.add(lBtn);
            }
            else
            {
                lCurrentRow = new RandallPanel(true);
                lCategoriesWeapon.addToPanel(lCurrentRow, 0, lCurrentRowNr++, 1, RandallPanel.HORIZONTAL);
            }
        }
        
        return lCategoriesWeapon;
    }

    private RandallPanel getCategoriesSocket()
    {
        ButtonGroup lCatSocketBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesSocket = new RandallPanel(true);
        
        iCatSocketJewel = new JRadioButton("Jewel");
        iCatSocketJewel.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketJewel, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketJewel);

        iCatSocketGem = new JRadioButton("Gem");
        iCatSocketGem.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketGem, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketGem);
        
        iCatSocketRune = new JRadioButton("Rune");
        iCatSocketRune.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketRune, 2, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketRune);
        
        iCatSocketAll = new JRadioButton("All");
        iCatSocketAll.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketAll, 3, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketAll);
        
        iCatSocketAll.setSelected(true);
        
        return lCategoriesSocket;
    }

    private RandallPanel getCategoriesCharm()
    {
        ButtonGroup lCatCharmBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesCharm = new RandallPanel(true);
        
        iCatCharmSmall = new JRadioButton("Small");
        iCatCharmSmall.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmSmall, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmSmall);

        iCatCharmLarge = new JRadioButton("Large");
        iCatCharmLarge.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmLarge, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmLarge);
        
        iCatCharmGrand = new JRadioButton("Grand");
        iCatCharmGrand.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmGrand, 2, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmGrand);
        
        iCatCharmAll = new JRadioButton("All");
        iCatCharmAll.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmAll, 3, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmAll);
        
        iCatCharmAll.setSelected(true);
        
        return lCategoriesCharm;
    }

    private RandallPanel getCategoriesMisc()
    {
        ButtonGroup lCatMiscBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesMisc = new RandallPanel(true);
        
        iCatMiscAmulet = new JRadioButton("Amulet");
        iCatMiscAmulet.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscAmulet, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscAmulet);

        iCatMiscRing = new JRadioButton("Ring");
        iCatMiscRing.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscRing, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscRing);
        
        iCatMiscOther = new JRadioButton("Other");
        iCatMiscOther.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscOther, 2, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscOther);
        
        iCatMiscAll = new JRadioButton("All");
        iCatMiscAll.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscAll, 3, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscAll);
        
        iCatMiscAll.setSelected(true);
        
        return lCategoriesMisc;
    }

    public void itemListChanged()
    {
        if ( iIgnoreItemListEvents )
        {
            return;
        }
        iItemModel.refreshData();
        String lTitle = iFileName;
        if (iStash == null || iItemModel == null)
        {
            lTitle += " (Error Reading File)";
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
        setTitle(lTitle);
    }

    class D2ItemModel implements TableModel
    {
//        private D2ItemList   iStash;
        private ArrayList iItems;
        private ArrayList iTableModelListeners = new ArrayList();
        private ArrayList iSortList = new ArrayList();

        private final Object HEADER[] = new Object[] {new Object(), new Object(), new Object(), new Object()};
        
        public D2ItemModel()
        {
//            iStash = pStash;
            iSortList.add(new Integer(0));
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
            int lMaxReqLvl = -1;
            int lMaxReqStr = -1;
            int lMaxReqDex = -1;

            if (iTypeUnique != null)
            {
                lMaxReqLvl = getInteger(iReqMaxLvl);
                lMaxReqStr = getInteger(iReqMaxStr);
                lMaxReqDex = getInteger(iReqMaxDex);
            }
                
            ArrayList lList = iStash.getItemList();
            iItems = new ArrayList();
            for (int i = 0; i < lList.size(); i++)
            {
                D2Item lItem = (D2Item) lList.get(i);
                
                boolean lAdd1 = false;
                boolean lAdd2 = false;
                
                if (iTypeUnique == null)
                {
                    // initializing, all filters to default
                    lAdd1 = true;
                    lAdd2 = true;
                }
                else
                {
                    if (iTypeUnique.isSelected() && lItem.isUnique())
                    {
                        lAdd1 = true;
                    }
                    else if (iTypeSet.isSelected() && lItem.isSet())
                    {
                        lAdd1 = true;
                    }
                    else if (iTypeRuneWord.isSelected() && lItem.isRuneWord())
                    {
                        lAdd1 = true;
                    }
                    else if (iTypeRare.isSelected() && lItem.isRare())
                    {
                        lAdd1 = true;
                    }
                    else if (iTypeMagical.isSelected() && lItem.isMagical())
                    {
                        lAdd1 = true;
                    }
                    else if (iTypeCrafted.isSelected() && lItem.isCrafted())
                    {
                        lAdd1 = true;
                    }
                    else if (iTypeOther.isSelected() && !lItem.isUnique()
                            && !lItem.isSet() && !lItem.isRuneWord()
                            && !lItem.isRare() && !lItem.isMagical()
                            && !lItem.isCrafted())
                    {
                        lAdd1 = true;
                    }
                    else if (!iTypeUnique.isSelected()
                            && !iTypeSet.isSelected()
                            && !iTypeRuneWord.isSelected()
                            && !iTypeRare.isSelected()
                            && !iTypeMagical.isSelected()
                            && !iTypeCrafted.isSelected()
                            && !iTypeOther.isSelected())
                    {
                        lAdd1 = true;
                    }

                    if (lAdd1)
                    {
                        if (iCatAll.isSelected())
                        {
                            lAdd2 = true;
                        }
                        else if (iCatArmor.isSelected() && lItem.isTypeArmor())
                        {
                            D2RadioButton lAll = (D2RadioButton) iArmorFilterList.get(iArmorFilterList.size()-1);
                            if ( lAll.isSelected() )
                            {
                                lAdd2 = true;
                            }
                            
                            for ( int j = 0 ; j < iArmorFilterList.size() - 1 ; j++ )
                            {
                                D2RadioButton lBtn = (D2RadioButton) iArmorFilterList.get(j);
                                if ( lBtn.isSelected() && lItem.isBodyLocation( (D2BodyLocations) lBtn.getData() ) )
                                {
                                    lAdd2 = true;
                                }
                            }
                        }
                        else if (iCatWeapons.isSelected()
                                && lItem.isTypeWeapon())
                        {
                            D2RadioButton lAll = (D2RadioButton) iWeaponFilterList.get(iWeaponFilterList.size()-1);
                            if ( lAll.isSelected() )
                            {
                                lAdd2 = true;
                            }
                            
                            for ( int j = 0 ; j < iWeaponFilterList.size() - 1 ; j++ )
                            {
                                D2RadioButton lBtn = (D2RadioButton) iWeaponFilterList.get(j);
                                if ( lBtn.isSelected() && lItem.isWeaponType( (D2WeaponTypes) lBtn.getData() ) )
                                {
                                    lAdd2 = true;
                                }
                            }
//                            lAdd2 = true;
                        }
                        else if (iCatSocket.isSelected()
                                && lItem.isSocketFiller())
                        {
                            if ( iCatSocketAll.isSelected() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatSocketJewel.isSelected() && lItem.isJewel() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatSocketGem.isSelected() && lItem.isGem() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatSocketRune.isSelected() && lItem.isRune() )
                            {
                                lAdd2 = true;
                            }
                        }
                        else if (iCatCharm.isSelected() && lItem.isCharm())
                        {
                            if ( iCatCharmAll.isSelected() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatCharmSmall.isSelected() && lItem.isCharmSmall() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatCharmLarge.isSelected() && lItem.isCharmLarge() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatCharmGrand.isSelected() && lItem.isCharmGrand() )
                            {
                                lAdd2 = true;
                            }
                        }
                        else if (iCatMisc.isSelected() && lItem.isTypeMisc()
                                && !lItem.isSocketFiller() && !lItem.isCharm())
                        {
                            if ( iCatMiscAll.isSelected() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatMiscAmulet.isSelected() && lItem.isBodyLocation(D2BodyLocations.BODY_NECK) )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatMiscRing.isSelected() && lItem.isBodyLocation(D2BodyLocations.BODY_LRIN) )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatMiscOther.isSelected() && !lItem.isBodyLocation(D2BodyLocations.BODY_NECK) && !lItem.isBodyLocation(D2BodyLocations.BODY_LRIN) )
                            {
                                lAdd2 = true;
                            }
                        }
                    }
                }

                if ( lAdd1 && lAdd2 )
                {
                    if ( lMaxReqLvl != -1 )
                    {
                        if ( lItem.getReqLvl() > lMaxReqLvl )
                        {
                            lAdd1 = false;
                        }
                    }
                    if ( lMaxReqStr != -1 )
                    {
                        if ( lItem.getReqStr() > lMaxReqStr )
                        {
                            lAdd1 = false;
                        }
                    }
                    if ( lMaxReqDex != -1 )
                    {
                        if ( lItem.getReqDex() > lMaxReqDex )
                        {
                            lAdd1 = false;
                        }
                    }
                    
                    if ( lAdd1 )
                    {
                        iItems.add(lItem);
                    }
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
                return "Lvl";
            case 2:
                return "Str";
            case 3:
                return "Dex";
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

    private class D2StashFilter implements ActionListener, DocumentListener
    {
        public void actionPerformed(ActionEvent pEvent)
        {
            // change layout according to filters
            if ( iCatArmor.isSelected() )
            {
                iArmorFilter.setVisible(true);
            }
            else
            {
                iArmorFilter.setVisible(false);
            }
            
            if ( iCatWeapons.isSelected() )
            {
                iWeaponFilter.setVisible(true);
            }
            else
            {
                iWeaponFilter.setVisible(false);
            }
            
            if ( iCatSocket.isSelected() )
            {
                iSocketFilter.setVisible(true);
            }
            else
            {
                iSocketFilter.setVisible(false);
            }

            if ( iCatCharm.isSelected() )
            {
                iCharmFilter.setVisible(true);
            }
            else
            {
                iCharmFilter.setVisible(false);
            }

            if ( iCatMisc.isSelected() )
            {
                iMiscFilter.setVisible(true);
            }
            else
            {
                iMiscFilter.setVisible(false);
            }

            // activate filters
            iItemModel.refreshData();
        }
        
        public void insertUpdate(DocumentEvent e)
        {
            // activate filters
            iItemModel.refreshData();
        }

        public void removeUpdate(DocumentEvent e)
        {
            // activate filters
            iItemModel.refreshData();
        }

        public void changedUpdate(DocumentEvent e)
        {
            // activate filters
            iItemModel.refreshData();
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

    public ArrayList getItemLists()
    {
        ArrayList lList = new ArrayList();
        lList.add(iStash);
        return lList;
    }
    
    public void closeView()
    {
        iFileManager.removeInternalFrame(this);
        iStash.removeD2ItemListListener(this);
    }

    public void saveView()
    {
        if (iStash.isModified())
        {
            iStash.save( iFileManager.getProject() );
        }
    }
    
}