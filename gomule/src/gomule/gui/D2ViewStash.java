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
public class D2ViewStash extends JInternalFrame implements D2ItemContainer
{
    private D2FileManager iFileManager;
    private D2Stash       iStash;
    private boolean       iModified = false;
    private String        iFileName;

    private D2StashFilter iStashFilter;
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
    private JRadioButton  iCatArmorHead;
    private JRadioButton  iCatArmorTors;
    private JRadioButton  iCatArmorShield;
    private JRadioButton  iCatArmorGlov;
    private JRadioButton  iCatArmorBelt;
    private JRadioButton  iCatArmorFeet;
    private JRadioButton  iCatArmorAll;
    
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

    public D2ViewStash(D2FileManager pMainFrame, String pFileName)
    {
        super(pFileName, true, true, false, true);
        iFileManager = pMainFrame;
        iFileName = pFileName;

        iContentPane = new JPanel();
        iContentPane.setLayout(new BorderLayout());

        try
        {
            iStash = new D2Stash(pFileName);
            
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
            iItemModel = new D2ItemModel(iStash);
            iTable = new JTable(iItemModel);
            iTable.setDefaultRenderer(String.class, new D2CellStringRenderer() );
            iTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane lPane = new JScrollPane(iTable);
            lPane.setPreferredSize(new Dimension(200, 100));
            iContentPane.add(lPane, BorderLayout.WEST);

            RandallPanel lButtonPanel = getButtonPanel();
            JPanel lTypePanel = getTypePanel();
            RandallPanel lCategoryPanel = getCategoryPanel();

            RandallPanel lTopPanel = new RandallPanel();
            lTopPanel
                    .addToPanel(lButtonPanel, 0, 0, 1, RandallPanel.HORIZONTAL);
            lTopPanel.addToPanel(lTypePanel, 0, 1, 1, RandallPanel.HORIZONTAL);
            lTopPanel.addToPanel(lCategoryPanel, 0, 2, 1,
                    RandallPanel.HORIZONTAL);

            iContentPane.add(lTopPanel, BorderLayout.NORTH);

            JPanel lItemPanel = new JPanel();
            iItemText = new JTextArea();
            JScrollPane lItemScroll = new JScrollPane(iItemText);
            lItemPanel.setLayout(new BorderLayout());
            lItemPanel.add(lItemScroll, BorderLayout.CENTER);
            lItemPanel.setPreferredSize(new Dimension(200, 100));

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

        addInternalFrameListener(new InternalFrameAdapter()
        {
            public void internalFrameClosing(InternalFrameEvent e)
            {
                closeView();
            }
        });

        pack();
        setSize(500, 500);
        setVisible(true);

        setTitle();

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

    public void setModified(boolean pModified)
    {
        iModified = pModified;

        setTitle();
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
                    for (int i = 0; i < lItemList.size(); i++)
                    {
                        iStash.removeItem((D2Item) lItemList.get(i));
                        D2MouseItem.addItem((D2Item) lItemList.get(i));
                    }
                    iItemModel.refreshData();
                    setModified(true);
                }
            }
        });
        lButtonPanel.addToPanel(lPickup, 0, 0, 1, RandallPanel.HORIZONTAL);

        JButton lDropOne = new JButton("Drop");
        lDropOne.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                iStash.addItem(D2MouseItem.removeItem());
                iItemModel.refreshData();
                setModified(true);
            }
        });
        lButtonPanel.addToPanel(lDropOne, 1, 0, 1, RandallPanel.HORIZONTAL);

        JButton lDropAll = new JButton("Drop All");
        lDropAll.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                ArrayList lItemList = D2MouseItem.getItemList();
                while (lItemList.size() > 0)
                {
                    iStash.addItem((D2Item) lItemList.remove(0));
                }
                iItemModel.refreshData();
                D2MouseItem.refreshData();
                setModified(true);
            }
        });
        lButtonPanel.addToPanel(lDropAll, 2, 0, 1, RandallPanel.HORIZONTAL);

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
        lCategories.addToPanel(iCatArmor, 0, lRow, 1, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatArmor);

        iCatWeapons = new JRadioButton("Weapon");
        iCatWeapons.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatWeapons, 1, lRow, 1, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatWeapons);

        iCatSocket = new JRadioButton("Socket Filler");
        iCatSocket.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatSocket, 2, lRow, 1,
                RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatSocket);

        iCatCharm = new JRadioButton("Charm");
        iCatCharm.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatCharm, 3, lRow, 1, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatCharm);

        iCatMisc = new JRadioButton("Misc");
        iCatMisc.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatMisc, 4, lRow, 1, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatMisc);

        iCatAll = new JRadioButton("All");
        iCatAll.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatAll, 5, lRow, 1, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatAll);

        iCatAll.setSelected(true);
        lRow++;
        
        iArmorFilter = getCategoriesArmor();
        iArmorFilter.setVisible(false);
        lCategories.addToPanel(iArmorFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);
        
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
        
        iCatArmorHead = new JRadioButton("Head");
        iCatArmorHead.addActionListener(iStashFilter);
        lCategoriesArmor.addToPanel(iCatArmorHead, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatArmorBtnGroup.add(iCatArmorHead);

        iCatArmorTors = new JRadioButton("Body");
        iCatArmorTors.addActionListener(iStashFilter);
        lCategoriesArmor.addToPanel(iCatArmorTors, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatArmorBtnGroup.add(iCatArmorTors);
        
        iCatArmorShield = new JRadioButton("Shield");
        iCatArmorShield.addActionListener(iStashFilter);
        lCategoriesArmor.addToPanel(iCatArmorShield, 2, 0, 1, RandallPanel.HORIZONTAL);
        lCatArmorBtnGroup.add(iCatArmorShield);
        
        iCatArmorGlov = new JRadioButton("Gloves");
        iCatArmorGlov.addActionListener(iStashFilter);
        lCategoriesArmor.addToPanel(iCatArmorGlov, 3, 0, 1, RandallPanel.HORIZONTAL);
        lCatArmorBtnGroup.add(iCatArmorGlov);
        
        iCatArmorBelt = new JRadioButton("Belt");
        iCatArmorBelt.addActionListener(iStashFilter);
        lCategoriesArmor.addToPanel(iCatArmorBelt, 4, 0, 1, RandallPanel.HORIZONTAL);
        lCatArmorBtnGroup.add(iCatArmorBelt);
        
        iCatArmorFeet = new JRadioButton("Boots");
        iCatArmorFeet.addActionListener(iStashFilter);
        lCategoriesArmor.addToPanel(iCatArmorFeet, 5, 0, 1, RandallPanel.HORIZONTAL);
        lCatArmorBtnGroup.add(iCatArmorFeet);
        
        iCatArmorAll = new JRadioButton("All");
        iCatArmorAll.addActionListener(iStashFilter);
        lCategoriesArmor.addToPanel(iCatArmorAll, 6, 0, 1, RandallPanel.HORIZONTAL);
        lCatArmorBtnGroup.add(iCatArmorAll);
        
        iCatArmorAll.setSelected(true);
        
        return lCategoriesArmor;
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

    public void setTitle()
    {
        String lTitle = iFileName;
        if (iStash == null || iItemModel == null)
        {
            lTitle += " (Error Reading File)";
        }
        else
        {
            lTitle += " (" + iItemModel.getRowCount() + "/";
            lTitle += iStash.getNrItems() + ")" + ((iModified) ? "*" : "");
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
        private D2Stash   iStash;
        private ArrayList iItems;
        private ArrayList iTableModelListeners = new ArrayList();

        public D2ItemModel(D2Stash pStash)
        {
            iStash = pStash;
            refreshData();
        }

        public void refreshData()
        {
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
                            if ( iCatArmorAll.isSelected() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatArmorHead.isSelected() && lItem.isBodyHead() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatArmorTors.isSelected() && lItem.isBodyTors() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatArmorShield.isSelected() && lItem.isBodyRArm() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatArmorGlov.isSelected() && lItem.isBodyGlov() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatArmorBelt.isSelected() && lItem.isBodyBelt() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatArmorFeet.isSelected() && lItem.isBodyFeet() )
                            {
                                lAdd2 = true;
                            }
                        }
                        else if (iCatWeapons.isSelected()
                                && lItem.isTypeWeapon())
                        {
                            lAdd2 = true;
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
                            else if ( iCatMiscAmulet.isSelected() && lItem.isBodyNeck() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatMiscRing.isSelected() && lItem.isBodyLRin() )
                            {
                                lAdd2 = true;
                            }
                            else if ( iCatMiscOther.isSelected() && !lItem.isBodyNeck() && !lItem.isBodyLRin() )
                            {
                                lAdd2 = true;
                            }
                        }
                    }
                }

                if (lAdd1 && lAdd2)
                {
                    iItems.add(lItem);
                }
            }
            Collections.sort(iItems);
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
            return 1;
        }

        public String getColumnName(int pCol)
        {
            switch (pCol)
            {
            case 0:
                return "Name";
            case 1:
                return "Fingerprint";
            case 2:
                return "iLvl";
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
                return new D2CellValue( lItem.getFingerprint(), lItem, iFileManager.getProject());
            case 2:
                return new D2CellValue( lItem.getILvl(), lItem, iFileManager.getProject());
            default:
                return "";
            }
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
            setTitle();
        }

    }

    private class D2StashFilter implements ActionListener
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
    }

    public String getFileName()
    {
        return iFileName;
    }

    public void closeView()
    {
        saveView();
        iFileManager.removeInternalFrame(this);
    }

    public void saveView()
    {
        if (iModified)
        {
            iStash.save();
            setModified(false);
        }
    }

}