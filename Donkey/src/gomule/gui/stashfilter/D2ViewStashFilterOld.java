package gomule.gui.stashfilter;

import gomule.gui.*;
import gomule.item.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import randall.util.*;

public class D2ViewStashFilterOld implements D2ViewStashFilter
{
	private D2ViewStash	iViewStash;
	
	RandallPanel iFilterContent;
	
    private D2StashFilter iStashFilter;
	private JCheckBox iTypeSocketed;
    
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
    
	private JCheckBox iQualEth;
	private JCheckBox iQualNorm;
	private JCheckBox iQualExce;
	private AbstractButton iQualEli;
	private AbstractButton iQualAll;
	private JCheckBox iQualOther;
	
	private RandallPanel iSockFilter;
	private JCheckBox iCatSock1;
	private JCheckBox iCatSock2;
	private JCheckBox iCatSock3;
	private JCheckBox iCatSock4;
	private JCheckBox iCatSock5;
	private JCheckBox iCatSock6;
	private JCheckBox iCatSockAll;

	private JButton iCusFilter;

	private String filterString = "";
	private int filterVal = -1337;
	private boolean filterOn = false;
	private boolean filterMin = true;
	
	public D2ViewStashFilterOld(D2ViewStash pViewStash)
	{
		iViewStash = pViewStash;
        iStashFilter = new D2StashFilter();
        
        JPanel lTypePanel = getTypePanel();
        JPanel lQualityPanel = getQualityPanel();
        RandallPanel lCategoryPanel = getCategoryPanel();

        iRequerementFilter = new RandallPanel();
        iReqMaxLvl = new JTextField();
        iReqMaxLvl.getDocument().addDocumentListener(iStashFilter);
        iReqMaxStr = new JTextField();
        iReqMaxStr.getDocument().addDocumentListener(iStashFilter);
        iReqMaxDex = new JTextField();
        iReqMaxDex.getDocument().addDocumentListener(iStashFilter);
        
        iCusFilter = new JButton("Filter...");
        
        iCusFilter.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
            	filterPopUp();
            }

			private void filterPopUp() {
				
				
				
				final JFrame filterPanel = new JFrame();
				filterPanel.setTitle("Item Filter");
				filterPanel.setLocation((int)iViewStash.getDisplay().getLocationOnScreen().getX() + 100,(int)iViewStash.getDisplay().getLocationOnScreen().getY() + 100);
				filterPanel.setSize(500,300);
				filterPanel.setVisible(true);
				filterPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				Box hRoot = Box.createHorizontalBox();
				Box vControl = Box.createVerticalBox();
				Box hVal = Box.createHorizontalBox();
				Box hButtons = Box.createHorizontalBox();
				Box hLabel1 = Box.createHorizontalBox();
				Box hLabel2 = Box.createHorizontalBox();
				Box hLabel3 = Box.createHorizontalBox();
							
				final JTextField fStrIn = new JTextField();
				final JTextField fNumIn = new JTextField();
				final JRadioButton fMin = new JRadioButton("Min");
				final JRadioButton fMax = new JRadioButton("Max");
				JButton fOk = new JButton("Ok");
				if(filterVal == -1337){
					fNumIn.setText("");
				}else{
					fNumIn.setText(filterVal + "");
				}
				fStrIn.setText(filterString);
				
				
				if(filterMin){
					fMin.setSelected(true);
					fMax.setSelected(false);
				}else{
					fMax.setSelected(true);
					fMin.setSelected(false);
				}
				
				fMin.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent arg0) {
						
						if(fMax.isSelected()){
							fMax.setSelected(false);
						}
						fMin.setSelected(true);
						filterMin = true;
					}
					
					
				});
				
				fMax.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent arg0) {
						
						
						if(fMin.isSelected()){
							fMin.setSelected(false);
						}
						
						fMax.setSelected(true);
						filterMin = false;
					}
					
					
				});
				
				fOk.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent pEvent)
	            {
				
	            	filterString = fStrIn.getText();
	            	try{
	            		if(fNumIn.getText().equals("")){
	            		
	            			filterVal = -1337;
	            			
	            		}else{
	            	filterVal = Integer.parseInt(fNumIn.getText());
	            		}
	            		
						filterOn = true;
//						iItemModel.filterString = "getting magic";
//						iItemModel.filterVal = 10;
						
						itemListChanged();
		            	
		            	
		            	filterPanel.dispose();
		            	
	            	}catch(NumberFormatException e){
	            		e.printStackTrace();
	            		filterVal = 0;
	            		fNumIn.setBackground(Color.red);
	            	}
	            	

	            	
	            }
				
				});
				
				JButton fClear = new JButton("Clear");
				
				fClear.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent pEvent)
	            {
				
	            	fNumIn.setBackground(Color.white);
					filterOn = false;
					filterString = "";
					filterVal = 0;
					
	            	fStrIn.setText("");
	            	
	            	fNumIn.setText("");	            	
					
	            	itemListChanged();
	            	
					
	            	
	            }
				
				});
				
				JButton fCancel = new JButton("Cancel");
				
				fCancel.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent pEvent)
	            {
				
	            	filterPanel.dispose();
	            	
	            }
				
				});
				
				filterPanel.getContentPane().add(hRoot);
//				hRoot.add(Box.createRigidArea(new Dimension(250,0)));
				
				
				vControl.add(hLabel3);
				hLabel3.add(new JLabel("The superfantastic finder machine."));
				hLabel3.add(Box.createRigidArea(new Dimension(10,0)));
				vControl.add(Box.createRigidArea(new Dimension(0,50)));
				hLabel1.add(new JLabel("Filter String:"));
				hLabel1.add(Box.createRigidArea(new Dimension(168,0)));
				vControl.add(hLabel1);
				hLabel2.add(new JLabel("Filter Value:"));
				hLabel2.add(Box.createRigidArea(new Dimension(168,0)));
				vControl.add(fStrIn);
				vControl.add(Box.createRigidArea(new Dimension(0,25)));
				vControl.add(hLabel2);
				vControl.add(hVal);
				vControl.add(Box.createRigidArea(new Dimension(0,50)));
				vControl.add(hButtons);
				vControl.add(Box.createRigidArea(new Dimension(0,50)));
			
				hVal.add(fNumIn);
				hVal.add(fMin);
				hVal.add(fMax);
				
				hButtons.add(fOk);
				hButtons.add(fClear);
				hButtons.add(fCancel);
				
				Box lazy = Box.createVerticalBox();
				
				lazy.add(new JLabel("I'm too lazy to code"));
				lazy.add(new JLabel("what should be here."));
				
				hRoot.add(lazy);
				hRoot.add(Box.createRigidArea(new Dimension(100, 0)));
				hRoot.add(vControl);
				filterPanel.validate();
			}
        });
        
        
        iRequerementFilter.addToPanel(new JLabel("MaxLvl"), 0, 0, 1, RandallPanel.NONE);
        iRequerementFilter.addToPanel(iReqMaxLvl, 1, 0, 1, RandallPanel.HORIZONTAL);
        iRequerementFilter.addToPanel(new JLabel("MaxStr"), 2, 0, 1, RandallPanel.NONE);
        iRequerementFilter.addToPanel(iReqMaxStr, 3, 0, 1, RandallPanel.HORIZONTAL);
        iRequerementFilter.addToPanel(new JLabel("MaxDex"), 4, 0, 1, RandallPanel.NONE);
        iRequerementFilter.addToPanel(iReqMaxDex, 5, 0, 1, RandallPanel.HORIZONTAL);
        iRequerementFilter.addToPanel(iCusFilter, 6, 0, 1, RandallPanel.HORIZONTAL);
        
        iFilterContent = new RandallPanel();
        iFilterContent.addToPanel(lQualityPanel, 0, 1, 1, RandallPanel.HORIZONTAL);
        iFilterContent.addToPanel(lTypePanel, 0, 2, 1, RandallPanel.HORIZONTAL);
        iFilterContent.addToPanel(lCategoryPanel, 0, 3, 1, RandallPanel.HORIZONTAL);
        iFilterContent.addToPanel(iRequerementFilter, 0, 4, 1, RandallPanel.HORIZONTAL);
        
	}
	
	public JComponent getDisplay()
	{
		return iFilterContent;
	}
	
	public void itemListChanged()
	{
		iViewStash.itemListChanged();
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

    public ArrayList filterItems(ArrayList pList)
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
            
        ArrayList iItems = new ArrayList();
        if ( pList != null )
        {
            ArrayList lList = pList;
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
                    else if(iTypeSocketed.isSelected() && lItem.isSocketed() && !iTypeUnique.isSelected()
                            && !iTypeSet.isSelected()
                            && !iTypeRuneWord.isSelected()
                            && !iTypeRare.isSelected()
                            && !iTypeMagical.isSelected()
                            && !iTypeCrafted.isSelected()
                            && !iTypeOther.isSelected()){
                    	lAdd1 = true;
                    }
                    else if (!iTypeUnique.isSelected()
                            && !iTypeSet.isSelected()
                            && !iTypeRuneWord.isSelected()
                            && !iTypeRare.isSelected()
                            && !iTypeMagical.isSelected()
                            && !iTypeCrafted.isSelected()
                            && !iTypeOther.isSelected()
                            && !iTypeSocketed.isSelected())
                    {
                        lAdd1 = true;
                    }
                    

                    
                
                    if(lItem.getItemQuality().equals("normal")){
                    	
                		if(!iQualNorm.isSelected() && !iQualAll.isSelected()){
                			lAdd1 = false;
                		}
                    	
                    }else if(lItem.getItemQuality().equals("exceptional")){
                    	
                		if(!iQualExce.isSelected() && !iQualAll.isSelected()){
                			lAdd1 = false;
                		}
                    	
                    }else if(lItem.getItemQuality().equals("elite")){
                    	
                    	
                		if(!iQualEli.isSelected() && !iQualAll.isSelected()){
                			lAdd1 = false;
                		}
                    	
                    }else if(lItem.getItemQuality().equals("none")){
                    	
                    	
                		if(!iQualOther.isSelected() && !iQualAll.isSelected()){
                			lAdd1 = false;
                		}
                    	
                    }
                    
                    if(!lItem.isEthereal()){
                    	if(iQualEth.isSelected()){
                    		lAdd1 = false;
                    	}
                    }
                    
                    
                    
//                    if(iQualNorm.isSelected()){
//                    	if(!lItem.getItemQuality().equals("normal")){
//                    		lAdd1 = false;
//                    	}
//                    }else
//                    if(iQualExce.isSelected()){
//                    	if(!lItem.getItemQuality().equals("exceptional")){
//                    		lAdd1 = false;
//                    	}
//                    }else
//                    if(iQualEli.isSelected()){
//                    	if(!lItem.getItemQuality().equals("elite")){
//                    		lAdd1 = false;
//                    	}
//                    }else
//                    if(iQualOther.isSelected()){
//                    	if(!lItem.getItemQuality().equals("none")){
//                    		lAdd1 = false;
//                    	}
//                    }
                    
                    if(iTypeSocketed.isSelected()){
                    	
                    	switch((int)lItem.getSocketNrTotal()){
                    	
                    	case 0:
                    		lAdd1 = false;
                    		break;
                    	case 1:
                    		if(!iCatSock1.isSelected() && !iCatSockAll.isSelected()){
                    			lAdd1 = false;
                    		}
                    		break;
                    	case 2:
                    		if(!iCatSock2.isSelected() && !iCatSockAll.isSelected()){
                    			lAdd1 = false;
                    		}
                    		break;
                    	case 3:
                    		if(!iCatSock3.isSelected() && !iCatSockAll.isSelected()){
                    			lAdd1 = false;
                    		}
                    		break;
                    	case 4:
                    		if(!iCatSock4.isSelected() && !iCatSockAll.isSelected()){
                    			lAdd1 = false;
                    		}
                    		break;
                    	case 5:
                    		if(!iCatSock5.isSelected() && !iCatSockAll.isSelected()){
                    			lAdd1 = false;
                    		}
                    		break;
                    	case 6:
                    		if(!iCatSock6.isSelected() && !iCatSockAll.isSelected()){
                    			lAdd1 = false;
                    		}
                    		break;
                    	
                    	}
                    	
                    	
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
					if(filterOn){
                	
						if(!lItem.conforms(filterString, filterVal, filterMin )){
							lAdd1 = false;
						}
						
                	}
                	
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
        }
        
        return iItems;
    }
	
	private RandallPanel getTypePanel()
    {
        RandallPanel lTypePanel = new RandallPanel(true);

        iTypeUnique = new JCheckBox("Uniq");
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
        iTypeMagical = new JCheckBox("Magic");
        iTypeMagical.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeMagical, 4, 0, 1, RandallPanel.HORIZONTAL);
        iTypeCrafted = new JCheckBox("Craft");
        iTypeCrafted.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeCrafted, 5, 0, 1, RandallPanel.HORIZONTAL);
        iTypeSocketed = new JCheckBox("Socketed");
        iTypeSocketed.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeSocketed, 6, 0, 1, RandallPanel.HORIZONTAL);
        iTypeOther = new JCheckBox("Other");
        iTypeOther.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeOther, 7, 0, 1, RandallPanel.HORIZONTAL);

        return lTypePanel;
    }

    
    private RandallPanel getQualityPanel()
    {
        RandallPanel lQualPanel = new RandallPanel(true);
        
        iQualNorm = new JCheckBox("Normal");
        iQualNorm.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualNorm, 0, 0, 1, RandallPanel.HORIZONTAL);
        iQualExce = new JCheckBox("Exceptional");
        iQualExce.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualExce, 1, 0, 1, RandallPanel.HORIZONTAL);
        iQualEli = new JCheckBox("Elite");
        iQualEli.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualEli, 2, 0, 1, RandallPanel.HORIZONTAL);
        iQualEth = new JCheckBox("Ethereal");
        iQualEth.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualEth, 3, 0, 1, RandallPanel.HORIZONTAL);
        iQualOther = new JCheckBox("Other");
        iQualOther.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualOther, 4, 0, 1, RandallPanel.HORIZONTAL);
        iQualAll = new JCheckBox("All");
        iQualAll.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualAll, 5, 0, 1, RandallPanel.HORIZONTAL);
        
        iQualAll.setSelected(true);

        return lQualPanel;
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
        
        iSockFilter = getCategoriesSock();
        iSockFilter.setVisible(false);
        lCategories.addToPanel(iSockFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);
        
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
            lBtn.setForeground(Color.BLUE);
            lBtn.addActionListener(iStashFilter);
            lCategoriesArmor.addToPanel(lBtn, i, 0, 1, RandallPanel.HORIZONTAL);
            lCatArmorBtnGroup.add(lBtn);
            if ( lArmor == D2BodyLocations.BODY_ALL )
            {
            	lBtn.setForeground(Color.BLACK);
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
	            lBtn.setForeground(Color.BLUE);
	            lBtn.addActionListener(iStashFilter);
	            lCurrentRow.addToPanel(lBtn, i, 0, 1, RandallPanel.HORIZONTAL);
	            lCatWeaponBtnGroup.add(lBtn);
	            if ( lWeapon == D2WeaponTypes.WEAP_ALL )
	            {
	            	lBtn.setForeground(Color.BLACK);
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
        iCatSocketJewel.setForeground(Color.BLUE);
        iCatSocketJewel.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketJewel, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketJewel);

        iCatSocketGem = new JRadioButton("Gem");
        iCatSocketGem.setForeground(Color.BLUE);
        iCatSocketGem.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketGem, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketGem);
        
        iCatSocketRune = new JRadioButton("Rune");
        iCatSocketRune.setForeground(Color.BLUE);
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
        iCatCharmSmall.setForeground(Color.BLUE);
        iCatCharmSmall.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmSmall, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmSmall);

        iCatCharmLarge = new JRadioButton("Large");
        iCatCharmLarge.setForeground(Color.BLUE);
        iCatCharmLarge.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmLarge, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmLarge);
        
        iCatCharmGrand = new JRadioButton("Grand");
        iCatCharmGrand.setForeground(Color.BLUE);
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
        iCatMiscAmulet.setForeground(Color.BLUE);
        iCatMiscAmulet.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscAmulet, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscAmulet);

        iCatMiscRing = new JRadioButton("Ring");
        iCatMiscRing.setForeground(Color.BLUE);
        iCatMiscRing.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscRing, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscRing);
        
        iCatMiscOther = new JRadioButton("Other");
        iCatMiscOther.setForeground(Color.BLUE);
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
    
    private RandallPanel getCategoriesSock()
    {
//        ButtonGroup lCatMiscBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesSock = new RandallPanel(true);
        
       
        
        iCatSock1 = new JCheckBox("1 Sock");
        iCatSock1.setForeground(Color.GREEN.darker().darker());
        iCatSock1.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock1, 0, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock1);

        iCatSock2 = new JCheckBox("2 Sock");
        iCatSock2.setForeground(Color.GREEN.darker().darker());
        iCatSock2.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock2, 1, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock2);
        
        iCatSock3 = new JCheckBox("3 Sock");
        iCatSock3.setForeground(Color.GREEN.darker().darker());
        iCatSock3.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock3, 2, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock3);
        
        iCatSock4 = new JCheckBox("4 Sock");
        iCatSock4.setForeground(Color.GREEN.darker().darker());
        iCatSock4.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock4, 3, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock4);
        
        iCatSock5 = new JCheckBox("5 Sock");
        iCatSock5.setForeground(Color.GREEN.darker().darker());
        iCatSock5.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock5, 4, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock5);
        
        iCatSock6 = new JCheckBox("6 Sock");
        iCatSock6.setForeground(Color.GREEN.darker().darker());
        iCatSock6.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock6, 5, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock6);
        
        iCatSockAll = new JCheckBox("All");
        iCatSockAll.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSockAll, 6, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSockAll);
        
        iCatSockAll.setSelected(true);
        return lCategoriesSock;
    }

    private class D2StashFilter implements ActionListener, DocumentListener
    {
        public void actionPerformed(ActionEvent pEvent)
        {
        	
        	if((iCatSockAll.isSelected() && iCatSock1.isSelected()) || (iCatSockAll.isSelected() && iCatSock2.isSelected()) || (iCatSockAll.isSelected() && iCatSock3.isSelected()) || (iCatSockAll.isSelected() && iCatSock4.isSelected()) || (iCatSockAll.isSelected() && iCatSock5.isSelected()) || (iCatSockAll.isSelected() && iCatSock6.isSelected())){
        		iCatSockAll.setSelected(false);
        	}
        	
        	if(pEvent.getSource().equals(iCatSockAll)){
        		if(!iCatSockAll.isSelected()){
        			iCatSockAll.setSelected(true);
        			iCatSock1.setSelected(false);
        			iCatSock2.setSelected(false);
        			iCatSock3.setSelected(false);
        			iCatSock4.setSelected(false);
        			iCatSock5.setSelected(false);
        			iCatSock6.setSelected(false);
        			
        		}
        	}
        	
        	
        	if((iQualAll.isSelected() && iQualNorm.isSelected()) || (iQualAll.isSelected() && iQualExce.isSelected()) || (iQualAll.isSelected() && iQualEli.isSelected())|| (iQualAll.isSelected() && iQualOther.isSelected())){
        		iQualAll.setSelected(false);
        	}
        	
        	if(pEvent.getSource().equals(iQualAll)){
        		if(!iQualAll.isSelected()){
        			iQualAll.setSelected(true);
        			iQualNorm.setSelected(false);
        			iQualExce.setSelected(false);
        			iQualOther.setSelected(false);
        			iQualEli.setSelected(false);
        			iQualEth.setSelected(false);
        			
        			
        		}
        	}
        	
        	
            // change layout according to filters
            if ( iCatArmor.isSelected() )
            {
            	iCatArmor.setForeground(Color.red);
                iArmorFilter.setVisible(true);
            }
            else
            {
            	iCatArmor.setForeground(Color.black);
                iArmorFilter.setVisible(false);
            }
            
            if ( iCatWeapons.isSelected() )
            {
            	iCatWeapons.setForeground(Color.red);
                iWeaponFilter.setVisible(true);
            }
            else
            {
            	iCatWeapons.setForeground(Color.black);
                iWeaponFilter.setVisible(false);
            }
            
            if ( iCatSocket.isSelected() )
            {
            	iCatSocket.setForeground(Color.red);
                iSocketFilter.setVisible(true);
            }
            else
            {
            	iCatSocket.setForeground(Color.black);
                iSocketFilter.setVisible(false);
            }

            if ( iCatCharm.isSelected() )
            {
            	iCatCharm.setForeground(Color.red);
                iCharmFilter.setVisible(true);
            }
            else
            {
            	iCatCharm.setForeground(Color.black);
                iCharmFilter.setVisible(false);
            }

            if ( iCatMisc.isSelected() )
            {
            	iCatMisc.setForeground(Color.red);
                iMiscFilter.setVisible(true);
            }
            else
            {
            	iCatMisc.setForeground(Color.black);
                iMiscFilter.setVisible(false);
            }
            if ( iTypeSocketed.isSelected() )
            {
            	iTypeSocketed.setForeground(Color.red);
                iSockFilter.setVisible(true);
            }
            else
            {
            	iTypeSocketed.setForeground(Color.black);
            	iSockFilter.setVisible(false);
            }
            

            // activate filters
            itemListChanged();
        }
        
        public void insertUpdate(DocumentEvent e)
        {
            // activate filters
            itemListChanged();
        }

        public void removeUpdate(DocumentEvent e)
        {
            // activate filters
            itemListChanged();
        }

        public void changedUpdate(DocumentEvent e)
        {
            // activate filters
            itemListChanged();
        }
        
    }


}
