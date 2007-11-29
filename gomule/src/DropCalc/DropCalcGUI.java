package DropCalc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DropCalcGUI extends JFrame{
	
	
	 JComboBox monTypeList = new JComboBox();
	 JComboBox mainMonList = new JComboBox();
	 JTextArea monOutput = new JTextArea();
	  JScrollPane scrollingArea = new JScrollPane(monOutput);
	  ArrayList monTypeKey = new ArrayList();
	  private MonDiff selectedMon;
	  JButton bla = new JButton("Stupid");
	  
	public DropCalcGUI(){
		
	final DropCalc DC = new DropCalc();
	

	
	for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
	mainMonList.addItem((String)((MonDiff)DC.mainRegMonArray.get(x)).getRealName()+"--"+(String)((MonDiff)DC.mainRegMonArray.get(x)).getName());
	}
	
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
	
	
	
	monTypeList.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			mainMonList.removeAllItems();
			switch(monTypeList.getSelectedIndex()){
			
			case(0):
			{
				for(int x = 0; x< DC.mainRegMonArray.size();x=x+1){
					mainMonList.addItem(((MonDiff)DC.mainRegMonArray.get(x)).getRealName()+"--"+((MonDiff)DC.mainRegMonArray.get(x)).getName());
					}
				break;
			}
			case(1):
			{
				for(int x = 0; x< DC.mainMinMonArray.size();x=x+1){
					mainMonList.addItem(((MonDiff)DC.mainMinMonArray.get(x)).getRealName()+ " (" + ((MonDiff)DC.mainMinMonArray.get(x)).getBoss() + ") " +"--"+((MonDiff)DC.mainMinMonArray.get(x)).getName());
					}
				break;
			}
			case(2):
			{
				for(int x = 0; x< DC.mainChampMonArray.size();x=x+1){
					mainMonList.addItem(((MonDiff)DC.mainChampMonArray.get(x)).getRealName()+"--"+((MonDiff)DC.mainChampMonArray.get(x)).getName());
					}
				break;
			}
			case(3):
			{
				for(int x = 0; x< DC.mainUniqArray.size();x=x+1){
					mainMonList.addItem(((MonDiff)DC.mainUniqArray.get(x)).getRealName()+"--"+((MonDiff)DC.mainUniqArray.get(x)).getName());
					}
				break;
			}
			case(4):
			{
				for(int x = 0; x< DC.mainSupUniqArray.size();x=x+1){
					mainMonList.addItem(((MonDiff)DC.mainSupUniqArray.get(x)).getRealName()+"--"+((MonDiff)DC.mainSupUniqArray.get(x)).getName());
					}
				break;
			}
			case(5):
			{
				for(int x = 0; x< DC.mainBossArray.size();x=x+1){
					mainMonList.addItem(((MonDiff)DC.mainBossArray.get(x)).getRealName()+"--"+((MonDiff)DC.mainBossArray.get(x)).getName());
					
					}
				break;
			}
			}
			mainMonList.repaint();
			
		}
	});
	
	mainMonList.addActionListener(new ActionListener() {

		

		public void actionPerformed(ActionEvent arg0) {
			
			if(mainMonList.getItemCount() != 0 ){
			
			
			System.out.println(monTypeList.getSelectedIndex());
			switch(monTypeList.getSelectedIndex()){
			
			case(0):
			{
				selectedMon = (MonDiff) (DC.mainRegMonArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(1):
			{
				selectedMon = (MonDiff) (DC.mainMinMonArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(2):
			{
				selectedMon = (MonDiff) (DC.mainChampMonArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(3):
			{
				selectedMon = (MonDiff) (DC.mainUniqArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(4):
			{
				selectedMon = (MonDiff) (DC.mainSupUniqArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			case(5):
			{
				selectedMon = (MonDiff) (DC.mainBossArray.get(mainMonList.getSelectedIndex()));
				break;
			}
			}
			
			
//			ArrayList selectedTCs = new ArrayList();
//			monOutput.setText("");
//
//				DC.lookupBASETCReturnATOMICTCS(selectedMon, 1);
//				System.out.println("SIZE: "+((ArrayList)selectedMon.getFinalProbs().get(0)).size());
//				for(int x = 0;x<selectedMon.getAreas().size();x=x+1){
//					for(int y = 0;y<((ArrayList)selectedMon.getFinalProbs().get(x)).size();y=y+1){
//					monOutput.setText(monOutput.getText() + selectedMon.getName() + "--"+ selectedMon.getRealName() + "--" + selectedMon.getLevels().get(x)+"--"+ selectedMon.getAreas().get(x) + "--"+selectedMon.getRealInitTC().get(x) +"--"+ ((ArrayList)selectedMon.getFinalTCs().get(x)).get(y) +"--"+((ArrayList)selectedMon.getFinalProbs().get(x)).get(y) + "\n");
//					}
//				}
			}
		}
	});
	
	
	bla.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			
	ArrayList selectedTCs = new ArrayList();
	monOutput.setText("");

		DC.lookupBASETCReturnATOMICTCS(selectedMon, 1);
		for(int x = 0;x<selectedMon.getAreas().size();x=x+1){
			for(int y = 0;y<((ArrayList)selectedMon.getFinalProbs().get(x)).size();y=y+1){
				if(selectedMon.getType().equals("MIN")){
					monOutput.setText(monOutput.getText() + selectedMon.getName() + " (" + selectedMon.getBoss() + ") " + "--"+ selectedMon.getRealName() + "--" + selectedMon.getLevels().get(x)+"--"+ selectedMon.getAreas().get(x) + "--"+selectedMon.getRealInitTC().get(x) +"--"+ ((ArrayList)selectedMon.getFinalTCs().get(x)).get(y) +"--"+((ArrayList)selectedMon.getFinalProbs().get(x)).get(y) + "\n");
					
					
				}else{
			monOutput.setText(monOutput.getText() + selectedMon.getName() + "--"+ selectedMon.getRealName() + "--" + selectedMon.getLevels().get(x)+"--"+ selectedMon.getAreas().get(x) + "--"+selectedMon.getRealInitTC().get(x) +"--"+ ((ArrayList)selectedMon.getFinalTCs().get(x)).get(y) +"--"+((ArrayList)selectedMon.getFinalProbs().get(x)).get(y) + "\n");
			}
			}
		}
		}
		});
	
	
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	});
	
	Box v1 = Box.createVerticalBox();
	v1.add(monTypeList);
	v1.add(mainMonList);
	v1.add(scrollingArea);
	v1.add(bla);
	
//	this.getContentPane().add(monOutput);
	this.getContentPane().add(v1);
	
	setSize(400, 170);
	setVisible(true);

}

public static void main(String[] args){
	DropCalcGUI dcG = new DropCalcGUI();
}


}