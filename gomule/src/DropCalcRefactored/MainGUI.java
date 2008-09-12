package DropCalcRefactored;

import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainGUI extends JFrame{
	
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -7949296803364254577L;
	JComboBox monTypeList = new JComboBox();
	 JComboBox mainMonList = new JComboBox();
		JComboBox typeSelector = new JComboBox();
		 JComboBox partyBox = new JComboBox();
			JComboBox playersBox = new JComboBox();
			JTextField mfBox = new JTextField();
				
	 JTextArea monOutput = new JTextArea();
	  JScrollPane scrollingArea = new JScrollPane(monOutput);
	  ArrayList monTypeKey = new ArrayList();
	  private Monster selectedMon;
	  JButton bla = new JButton("Stupid");
	  
		Box v1 = Box.createVerticalBox();
		Box h1 = Box.createHorizontalBox();
		Box h2 = Box.createHorizontalBox();
		Box h3 = Box.createHorizontalBox();
		Box h4 = Box.createHorizontalBox();
		Box h5 = Box.createHorizontalBox();
		private JComboBox itemTypeList = new JComboBox();
		private JComboBox mainItemList = new JComboBox();
	  
	public MainGUI(){
		
	final DCNew DC = new DCNew();
	
	

	
	for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
	mainMonList.addItem((String)((Monster)DC.mainRegMonArray.get(x)).getRealName()+"--"+(String)((Monster)DC.mainRegMonArray.get(x)).getName());
	}
	
	playersBox.addItem("1");
	playersBox.addItem("2");
	playersBox.addItem("3");
	playersBox.addItem("4");
	playersBox.addItem("5");
	playersBox.addItem("6");
	playersBox.addItem("7");
	playersBox.addItem("8");
	
	partyBox.addItem("1");
	
	typeSelector.addItem("Monster");
	typeSelector.addItem("Item");
	
	monTypeList.addItem("Regular");
	monTypeList.addItem("Minion");
	monTypeList.addItem("Champion");
	monTypeList.addItem("Unique");
	monTypeList.addItem("SuperUnique");
	monTypeList.addItem("Boss");
	
	monTypeKey.add("REG");
	monTypeKey.add("MIN");
	monTypeKey.add("CHAMP");
	monTypeKey.add("UNIQ");
	monTypeKey.add("SUPUNIQ");
	monTypeKey.add("BOSS");
	
	itemTypeList.addItem("Base");
	itemTypeList.addItem("Magic");
	itemTypeList.addItem("Rare");
	itemTypeList.addItem("Set");
	itemTypeList.addItem("Uniq");
	
	
	
	mainItemList.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			
			
		}});
	
	itemTypeList.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			mainItemList.removeAllItems();
			for(int x = 0; x< DC.uniqItemArray.size();x=x+1){
				mainItemList.addItem(DC.uniqItemArray.get(x));
				}
			
		}});
	
	typeSelector.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			
			if(((String)typeSelector.getSelectedItem()).equals("Item")){
				switchMode(1);
			}else{
				switchMode(0);
			}
			
		}

});
	
	
	playersBox.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			
			System.out.println(playersBox.getSelectedIndex());
			partyBox.removeAllItems();
			
			partyBox.validate();
			for(int x = 0;x<playersBox.getSelectedIndex()+1;x=x+1){
				partyBox.addItem(new Integer(x+1));
			}
			
		}});
	
	monTypeList.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			mainMonList.removeAllItems();
			switch(monTypeList.getSelectedIndex()){
			case(0):
			{
				for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
					mainMonList.addItem(((Monster)DC.mainRegMonArray.get(x)).getRealName()+"--"+((Monster)DC.mainRegMonArray.get(x)).getName());
					}
				break;
			}
			case(1):
			{
				for(int x = 0; x< DC.mainMinMonArray.size();x=x+1){
					mainMonList.addItem(((Monster)DC.mainMinMonArray.get(x)).getRealName()+ " (" + ((Monster)DC.mainMinMonArray.get(x)).getBoss() + ") " +"--"+((Monster)DC.mainMinMonArray.get(x)).getName());
					}
				break;
			}
			case(2):
			{
				for(int x = 0; x< DC.mainChampMonArray.size();x=x+1){
					mainMonList.addItem(((Monster)DC.mainChampMonArray.get(x)).getRealName()+"--"+((Monster)DC.mainChampMonArray.get(x)).getName());
					}
				break;
			}
			case(3):
			{
				for(int x = 0; x< DC.mainUniqArray.size();x=x+1){
					mainMonList.addItem(((Monster)DC.mainUniqArray.get(x)).getRealName()+"--"+((Monster)DC.mainUniqArray.get(x)).getName());
					}
				break;
			}
			case(4):
			{
				for(int x = 0; x< DC.mainSupUniqArray.size();x=x+1){
					mainMonList.addItem(((Monster)DC.mainSupUniqArray.get(x)).getRealName()+"--"+((Monster)DC.mainSupUniqArray.get(x)).getName());
					}
				break;
			}
			case(5):
			{
				for(int x = 0; x< DC.mainBossArray.size();x=x+1){
					mainMonList.addItem(((Monster)DC.mainBossArray.get(x)).getRealName()+"--"+((Monster)DC.mainBossArray.get(x)).getName());
					
					}
				break;
			}
			}
			mainMonList.validate();
			
		}
	});
	
	mainMonList.addActionListener(new ActionListener() {

		

		public void actionPerformed(ActionEvent arg0) {
			
			if(mainMonList.getItemCount() != 0 ){
			
			
			System.out.println(monTypeList.getSelectedIndex());
			switch(monTypeList.getSelectedIndex()){
			
			case(0):
			{
				selectedMon = (Monster) (DC.mainRegMonArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(1):
			{
				selectedMon = (Monster) (DC.mainMinMonArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(2):
			{
				selectedMon = (Monster) (DC.mainChampMonArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(3):
			{
				selectedMon = (Monster) (DC.mainUniqArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(4):
			{
				selectedMon = (Monster) (DC.mainSupUniqArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(5):
			{
				selectedMon = (Monster) (DC.mainBossArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			}
			
			}
		}
	});
	
	
	bla.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			
	monOutput.setText("");
		selectedMon.clearFinal();
		selectedMon.lookupBASETCReturnATOMICTCS(playersBox.getSelectedIndex()+ 1, partyBox.getSelectedIndex()+1);
		String out = "";
		for(int x = 0;x<selectedMon.mTuples.size();x=x+1){
		
				Iterator TCIt = ((MonsterTuple)selectedMon.mTuples.get(x)).getFinalTCs().keySet().iterator();
				while(TCIt.hasNext()){
					String tcArr = (String) TCIt.next();
					
						out = out  + (selectedMon.getName() + "--"+ selectedMon.getRealName() + "--" + ((MonsterTuple)selectedMon.mTuples.get(x)).Level+"--"+ ((MonsterTuple)selectedMon.mTuples.get(x)).AreaName + "--"+((MonsterTuple)selectedMon.mTuples.get(x)).initTC +"--"+ tcArr +"--"+((MonsterTuple)selectedMon.mTuples.get(x)).getFinalTCs().get(tcArr) + "\n");

					
//			monOutput.setText(monOutput.getText() + selectedMon.getName() + "--"+ selectedMon.getRealName() + "--" + selectedMon.getLevels().get(x)+"--"+ selectedMon.getAreas().get(x) + "--"+selectedMon.getRealInitTC().get(x) +"--"+ ((ArrayList)selectedMon.getFinalTCs().get(x)).get(y) +"--"+((ArrayList)selectedMon.getFinalProbs().get(x)).get(y) + "\n");
			
			}
		}
		
		monOutput.setText(out);
//		System.out.println(out);
		}
		});
	
	
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	});

	monOutput.setRows(40);
	setLayout(new BorderLayout());


	switchMode(1);
//	this.getContentPane().add(monOutput);
	this.getContentPane().add(v1);
	
	setSize(800, 600);
	setVisible(true);

}
	
	private void switchMode(int i) {
		switch(i){
		case 0:
			
			v1.add(h1);
			v1.add(h2);
			v1.add(h3);
			v1.add(h4);
			v1.add(h5);
			h1.add(playersBox);
			h1.add(partyBox);
			h1.add(mfBox);
			
			h2.add(monTypeList);
			h2.add(typeSelector);
			
			h3.add(mainMonList);
			
			h4.add(scrollingArea);
			
			h5.add(bla);
			this.validate();
			
			break;
		case 1:
			v1.add(h1);
			v1.add(h2);
			v1.add(h3);
			v1.add(h4);
			v1.add(h5);
			h1.add(playersBox);
			h1.add(partyBox);
			h1.add(mfBox);
			
			h2.add(itemTypeList);
			h2.add(typeSelector);
			
			h3.add(mainItemList);
			
			h4.add(scrollingArea);
			
			h5.add(bla);
			this.validate();
			
			
			break;
		}
		
	}

public static void main(String[] args){
	new MainGUI();
}


}