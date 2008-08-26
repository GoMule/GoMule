package DropCalcRefactored;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;

public class RealGUI extends JFrame{

	JComboBox nPlayers = new JComboBox();
	JComboBox nGroup = new JComboBox();
	JComboBox nSearch = new JComboBox();
	JComboBox nClass = new JComboBox();
	JComboBox nType = new JComboBox();
	JComboBox nMonster = new JComboBox();
	JComboBox nDiff = new JComboBox();
	JTextArea oResult = new JTextArea();
	JScrollPane oScrollingArea = new JScrollPane(oResult);
	JTextField nMF = new JTextField();
	ArrayList  nClassKey = new ArrayList();
	JButton bSearch = new JButton("Find");
	JButton bClear = new JButton("Clear");
	final DCNew DC = new DCNew();
	ArrayList nMonsterKey = new ArrayList();

	public RealGUI(){


		Box vMain = Box.createVerticalBox();
		Box hSettings = Box.createHorizontalBox();
		vMain.add(hSettings);

		//Player information
		JPanel pPlayers = new JPanel();
		pPlayers.setBorder((new TitledBorder(null, ("Basics"), 
				TitledBorder.LEFT, TitledBorder.TOP)));
		Box vPlayers = Box.createVerticalBox();
		pPlayers.add(vPlayers);
		Box hP1 = Box.createHorizontalBox();
		hP1.add(new JLabel("N Players"));
		hP1.add(nPlayers);
		Box hP2 = Box.createHorizontalBox();
		hP2.add(new JLabel("N Group"));
		hP2.add(nGroup);
		Box hP3 = Box.createHorizontalBox();
		hP3.add(new JLabel("MF"));
		hP3.add(nMF);
		Box hP4 = Box.createHorizontalBox();
		hP4.add(bSearch);
		hP4.add(Box.createRigidArea(new Dimension(3,0)));
		hP4.add(bClear);
		vPlayers.add(hP1);
		vPlayers.add(Box.createRigidArea(new Dimension(0,8)));
		vPlayers.add(hP2);
		vPlayers.add(Box.createRigidArea(new Dimension(0,8)));
		vPlayers.add(hP3);
		vPlayers.add(Box.createRigidArea(new Dimension(0,8)));
		vPlayers.add(hP4);
		hSettings.add(pPlayers);
		for(int x = 1;x<9;x=x+1){
			nGroup.addItem(Integer.toString(x));
			nPlayers.addItem(Integer.toString(x));
		}
		nGroup.setSelectedIndex(0);
		nPlayers.setSelectedIndex(0);
		//Search information
		JPanel pSearch = new JPanel();
		pSearch.setBorder((new TitledBorder(null, ("Filter..."), 
				TitledBorder.LEFT, TitledBorder.TOP)));
		Box vSearch = Box.createVerticalBox();
		pSearch.add(vSearch);
		Box hS1 = Box.createHorizontalBox();
		hS1.add(new JLabel("Search for..."));
		hS1.add(nSearch);
		Box hS2 = Box.createHorizontalBox();
		hS2.add(new JLabel("Mon Class"));
		hS2.add(nClass);
		Box hS3 = Box.createHorizontalBox();
		hS3.add(new JLabel("Difficulty"));
		hS3.add(nDiff);
		vSearch.add(hS1);
		vSearch.add(Box.createRigidArea(new Dimension(0,8)));
		vSearch.add(hS2);
		vSearch.add(Box.createRigidArea(new Dimension(0,8)));
		vSearch.add(hS3);
		hSettings.add(pSearch);

		nSearch.addItem("Monster");
		nSearch.addItem("Item");
		nSearch.setSelectedIndex(0);

		nClass.addItem("Regular");
		nClass.addItem("Minion");
		nClass.addItem("Champion");
		nClass.addItem("Unique");
		nClass.addItem("SuperUnique");
		nClass.addItem("Boss");
		nClass.setSelectedIndex(0);
		nClassKey.add("REG");
		nClassKey.add("MIN");
		nClassKey.add("CHAMP");
		nClassKey.add("UNIQ");
		nClassKey.add("SUPUNIQ");
		nClassKey.add("BOSS");

		nDiff.addItem("All");
		nDiff.addItem("Normal");
		nDiff.addItem("Nightmare");
		nDiff.addItem("Hell");
		nDiff.setSelectedIndex(0);
		//Monster information
		JPanel pMonsters = new JPanel();
		pMonsters.setBorder((new TitledBorder(null, ("Monsters"), 
				TitledBorder.LEFT, TitledBorder.TOP)));
		Box vMonsters = Box.createVerticalBox();
		pMonsters.add(vMonsters);
		Box hM1 = Box.createHorizontalBox();
		hM1.add(new JLabel("Type"));
		hM1.add(nType);
		Box hM2 = Box.createHorizontalBox();
		hM2.add(new JLabel("Monster"));
		hM2.add(nMonster);
		vMonsters.add(hM1);
		vMonsters.add(Box.createRigidArea(new Dimension(0,8)));
		vMonsters.add(hM2);
		hSettings.add(pMonsters);

		nType.addItem("Atomic TC");
		nType.addItem("Item");
		nType.setSelectedIndex(0);

		refreshMonsterField();
		//Item information

		//Results box
		vMain.add(oScrollingArea);
		oResult.setRows(29);
		//Other window stuff
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setupListeners();
		getContentPane().add(vMain);
		setSize(900, 650);
		setVisible(true);

	}

	private void setupListeners() {

		nDiff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				resetArrays();
				refreshMonsterField();

			}});

		nClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				resetArrays();
				refreshMonsterField();

			}});

		bSearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				long Stime = System.currentTimeMillis();
				resetDisplay();

				Monster mSelected = ((Monster)nMonsterKey.get(nMonster.getSelectedIndex()));
				mSelected.lookupBASETCReturnATOMICTCS(nPlayers.getSelectedIndex()+ 1, nGroup.getSelectedIndex()+1);
				String out = "";
				for(int x = 0;x<mSelected.mTuples.size();x=x+1){

					Iterator TCIt = ((MonsterTuple)mSelected.mTuples.get(x)).getFinalTCs().keySet().iterator();
					while(TCIt.hasNext()){
						String tcArr = (String) TCIt.next();
						MonsterTuple tSelected = ((MonsterTuple)mSelected.mTuples.get(x));
						out = out  + (mSelected.getName() + "--"+ mSelected.getRealName() + "--" + tSelected.Level+"--"+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",tSelected.AreaName).get("LevelName")) + "--"+tSelected.initTC +"--"+ tcArr +"--"+tSelected.getFinalTCs().get(tcArr) + "\n");

					}
				}

				oResult.setText(out);
				long ttime = System.currentTimeMillis();
				System.out.println(ttime -Stime);
			}});

	}




	private void resetArrays(){
		nMonster.removeAllItems();
		nMonsterKey.clear();
	}

	private void resetDisplay(){

		oResult.setText("");
		((Monster)nMonsterKey.get(nMonster.getSelectedIndex())).clearFinal();

	}

	private void refreshMonsterField(){


		switch(nClass.getSelectedIndex()){
		case 0:
			switch(nDiff.getSelectedIndex()){
			case 0:
				for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
					nMonsterKey.add(DC.mainRegMonArray.get(x));
					nMonster.addItem((String)((Monster)DC.mainRegMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainRegMonArray.get(x)).getName());
				}
				break;
			case 1:
				for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainRegMonArray.get(x)).getName()).contains("NORMAL")){
						nMonsterKey.add(DC.mainRegMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainRegMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainRegMonArray.get(x)).getName());
					}
				}
				break;
			case 2:
				for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainRegMonArray.get(x)).getName()).contains("NIGHTMARE")){
						nMonsterKey.add(DC.mainRegMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainRegMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainRegMonArray.get(x)).getName());
					}
				}
				break;
			case 3:
				for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainRegMonArray.get(x)).getName()).contains("HELL")){		
						nMonsterKey.add(DC.mainRegMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainRegMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainRegMonArray.get(x)).getName());
					}
				}
				break;
			}
			break;
		case 1:
			switch(nDiff.getSelectedIndex()){
			case 0:
				for(int x = 0; x< DC.mainMinMonArray.size();x=x+1){
					nMonsterKey.add(DC.mainMinMonArray.get(x));
					nMonster.addItem((String)((Monster)DC.mainMinMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainMinMonArray.get(x)).getName());
				}
				break;
			case 1:
				for(int x = 0; x< DC.mainMinMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainMinMonArray.get(x)).getName()).contains("NORMAL")){
						nMonsterKey.add(DC.mainMinMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainMinMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainMinMonArray.get(x)).getName());
					}
				}
				break;
			case 2:
				for(int x = 0; x< DC.mainMinMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainMinMonArray.get(x)).getName()).contains("NIGHTMARE")){
						nMonsterKey.add(DC.mainMinMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainMinMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainMinMonArray.get(x)).getName());
					}
				}
				break;
			case 3:
				for(int x = 0; x< DC.mainMinMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainMinMonArray.get(x)).getName()).contains("HELL")){	
						nMonsterKey.add(DC.mainMinMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainMinMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainMinMonArray.get(x)).getName());
					}
				}
				break;
			}
			break;
		case 2:
			switch(nDiff.getSelectedIndex()){
			case 0:
				for(int x = 0; x< DC.mainChampMonArray.size();x=x+1){
					nMonsterKey.add(DC.mainChampMonArray.get(x));
					nMonster.addItem((String)((Monster)DC.mainChampMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainChampMonArray.get(x)).getName());
				}
				break;
			case 1:
				for(int x = 0; x< DC.mainChampMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainChampMonArray.get(x)).getName()).contains("NORMAL")){
						nMonsterKey.add(DC.mainChampMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainChampMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainChampMonArray.get(x)).getName());
					}
				}
				break;
			case 2:
				for(int x = 0; x< DC.mainChampMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainChampMonArray.get(x)).getName()).contains("NIGHTMARE")){
						nMonsterKey.add(DC.mainChampMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainChampMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainChampMonArray.get(x)).getName());
					}
				}
				break;
			case 3:
				for(int x = 0; x< DC.mainChampMonArray.size();x=x+1){
					if(((String)((Monster)DC.mainChampMonArray.get(x)).getName()).contains("HELL")){		
						nMonsterKey.add(DC.mainChampMonArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainChampMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainChampMonArray.get(x)).getName());
					}
				}
				break;
			}
			break;
		case 3:
			switch(nDiff.getSelectedIndex()){
			case 0:
				for(int x = 0; x< DC.mainUniqArray.size();x=x+1){
					nMonsterKey.add(DC.mainUniqArray.get(x));
					nMonster.addItem((String)((Monster)DC.mainUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainUniqArray.get(x)).getName());
				}
				break;
			case 1:
				for(int x = 0; x< DC.mainUniqArray.size();x=x+1){
					if(((String)((Monster)DC.mainUniqArray.get(x)).getName()).contains("NORMAL")){
						nMonsterKey.add(DC.mainUniqArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainUniqArray.get(x)).getName());
					}
				}
				break;
			case 2:
				for(int x = 0; x< DC.mainUniqArray.size();x=x+1){
					if(((String)((Monster)DC.mainUniqArray.get(x)).getName()).contains("NIGHTMARE")){
						nMonsterKey.add(DC.mainUniqArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainUniqArray.get(x)).getName());
					}
				}
				break;
			case 3:
				for(int x = 0; x< DC.mainUniqArray.size();x=x+1){
					if(((String)((Monster)DC.mainUniqArray.get(x)).getName()).contains("HELL")){		
						nMonsterKey.add(DC.mainUniqArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainUniqArray.get(x)).getName());
					}
				}
				break;
			}
			break;
		case 4:
			switch(nDiff.getSelectedIndex()){
			case 0:
				for(int x = 0; x< DC.mainSupUniqArray.size();x=x+1){
					nMonsterKey.add(DC.mainSupUniqArray.get(x));
					nMonster.addItem((String)((Monster)DC.mainSupUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainSupUniqArray.get(x)).getName());
				}
				break;
			case 1:
				for(int x = 0; x< DC.mainSupUniqArray.size();x=x+1){
					if(((String)((Monster)DC.mainSupUniqArray.get(x)).getName()).contains("NORMAL")){
						nMonsterKey.add(DC.mainSupUniqArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainSupUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainSupUniqArray.get(x)).getName());
					}
				}
				break;
			case 2:
				for(int x = 0; x< DC.mainSupUniqArray.size();x=x+1){
					if(((String)((Monster)DC.mainSupUniqArray.get(x)).getName()).contains("NIGHTMARE")){
						nMonsterKey.add(DC.mainSupUniqArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainSupUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainSupUniqArray.get(x)).getName());
					}
				}
				break;
			case 3:
				for(int x = 0; x< DC.mainSupUniqArray.size();x=x+1){
					if(((String)((Monster)DC.mainSupUniqArray.get(x)).getName()).contains("HELL")){			
						nMonsterKey.add(DC.mainSupUniqArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainSupUniqArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainSupUniqArray.get(x)).getName());
					}
				}
				break;
			}
			break;
		case 5:
			switch(nDiff.getSelectedIndex()){
			case 0:
				for(int x = 0; x< DC.mainBossArray.size();x=x+1){
					nMonsterKey.add(DC.mainBossArray.get(x));
					nMonster.addItem((String)((Monster)DC.mainBossArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainBossArray.get(x)).getName());
				}
				break;
			case 1:
				for(int x = 0; x< DC.mainBossArray.size();x=x+1){
					if(((String)((Monster)DC.mainBossArray.get(x)).getName()).contains("NORMAL")){
						nMonsterKey.add(DC.mainBossArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainBossArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainBossArray.get(x)).getName());
					}
				}
				break;
			case 2:
				for(int x = 0; x< DC.mainBossArray.size();x=x+1){
					if(((String)((Monster)DC.mainBossArray.get(x)).getName()).contains("NIGHTMARE")){
						nMonsterKey.add(DC.mainBossArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainBossArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainBossArray.get(x)).getName());
					}
				}
				break;
			case 3:
				for(int x = 0; x< DC.mainBossArray.size();x=x+1){
					if(((String)((Monster)DC.mainBossArray.get(x)).getName()).contains("HELL")){		
						nMonsterKey.add(DC.mainBossArray.get(x));
						nMonster.addItem((String)((Monster)DC.mainBossArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainBossArray.get(x)).getName());
					}
				}
				break;
			}
			break;
		}




//		nMonster.setSelectedIndex(0);	

	}

	public static void main(String [] args){


		RealGUI RG = new RealGUI();
		
		RG.DC.findMonstersTC(87);
	}

}
