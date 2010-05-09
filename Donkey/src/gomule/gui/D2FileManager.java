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
import gomule.d2x.*;
import gomule.dropCalc.gui.*;
import gomule.gui.desktop.frames.GoMuleDesktopInternalFrame;
import gomule.gui.desktop.generic.*;
import gomule.gui.desktop.tabs.*;
import gomule.item.*;
import gomule.util.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.*;

import randall.d2files.*;
import randall.flavie.*;
import randall.util.*;

/**
 * this class is the top-level administrative window. 
 * It contains all internal frames
 * it contains all open files
 */ 
public class D2FileManager extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4010435064410504579L;

	private static final String  CURRENT_VERSION = "R0.3: Donkey";

	private HashMap				 iItemLists = new HashMap();
//	private ArrayList            iOpenWindows;
	private JMenuBar			 iMenuBar;
	private JPanel               iContentPane;
	private GoMuleDesktop        iDesktopPane;
	private JToolBar             iToolbar;
	private Properties           iProperties;
	private D2Project            iProject;
//	private JButton              iBtnProjectSelection;
	private D2ViewProject        iViewProject;
	private final static D2FileManager iCurrent = new D2FileManager();
	private D2ViewClipboard      iClipboard;
	private D2ViewStash          iViewAll;
	private boolean				 iIgnoreCheckAll = false;
//	private JMenuBar D2JMenu;
//	private JMenu file;
//	private JMenu edit;

	private JPanel iRightPane;
	private RandallPanel iLeftPane;

	private DefaultComboBoxModel iProjectModel;

	private JComboBox iChangeProject;

	private JButton dropAll;

	private JButton pickFrom;

	private JComboBox pickChooser;

	private JButton dropTo;

	private JComboBox dropChooser;

	private JButton pickAll;

	private JButton dumpBut;

	private JButton flavieSingle;

	public static D2FileManager getInstance()
	{
		return iCurrent;
	}
	/**
	 * Constructor - creates a new File Manager, starting up all of the processes
	 *
	 */
	private D2FileManager()
	{
		D2TxtFile.constructTxtFiles("d2111");
		D2TblFile.readAllFiles("d2111");

//		iOpenWindows = new ArrayList();
		iContentPane = new JPanel();
//		iDesktopPane = new GoMuleDesktopInternalFrame();
		iDesktopPane = new GoMuleDesktopTabs();

		iContentPane.setLayout(new BorderLayout());

		createToolbar();
		createMenubar();
		createLeftPane();
		createRightPane();

		JSplitPane lSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, iLeftPane, iDesktopPane.getDisplay());
		JSplitPane rSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, lSplit, iRightPane);

		lSplit.setDividerLocation(200);
		rSplit.setDividerLocation(1024 - 210);
		rSplit.setResizeWeight(1.0);
		iContentPane.add(rSplit, BorderLayout.CENTER);

		setContentPane(iContentPane);
		setSize(1024,768);
		setTitle("GoMule " + CURRENT_VERSION);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getGlassPane().setVisible(false);
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				closeListener();
			}
			public void windowActivated(WindowEvent e) 
			{
				checkAll(false);
			}
		});
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		iClipboard.scrollbarBottom();
	}
	/**
	 * Sets the current GoMule project
	 * @param pProject Project name of project to set GoMule to
	 */
	protected void setProject(String pProject)
	{
		try
		{
			iProject.saveProject();
			iProject = new D2Project(this, pProject);
			this.setProject(iProject);
			if(iProjectModel.getIndexOf(pProject) == -1){
				iProjectModel.addElement(pProject);
				iChangeProject.setSelectedItem(iProject.getProjectName());
			}
		}
		catch (Exception pEx)
		{
			D2FileManager.displayErrorDialog(pEx);
		}
	}

	/**
	 * Ensures that the projects are all present, readable and writable. Adds projects to project model
	 *
	 */
	private void checkProjectsModel()
	{
		iProjectModel.removeAllElements();
		File lProjectsDir = new File(D2Project.PROJECTS_DIR);
		if (!lProjectsDir.exists())
		{
			lProjectsDir.mkdir();
		}
		else
		{
			File lList[] = lProjectsDir.listFiles();
			for (int i = 0; i < lList.length; i++)
			{
				if (lList[i].isDirectory() && lList[i].canRead() && lList[i].canWrite())
				{
					iProjectModel.addElement(lList[i].getName());
				}
			}
		}

	}
	/**
	 * Adds all the elements to the left panel of the main GUI
	 *
	 */
	private void createLeftPane() {

		iViewProject = new D2ViewProject(this);
		iViewProject.setPreferredSize(new Dimension(190, 500));
		iViewProject.setProject(iProject);
		iViewProject.refreshTreeModel(true, true);
		iLeftPane = new RandallPanel();

		iProjectModel = new DefaultComboBoxModel();
		checkProjectsModel();
		iChangeProject = new JComboBox(iProjectModel);
		iChangeProject.setPreferredSize(new Dimension(190,20));
		iChangeProject.setSelectedItem(iProject.getProjectName());
		iChangeProject.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent arg0) {

				if(arg0.getStateChange() == ItemEvent.SELECTED){
					closeWindows();
					setProject((String) iChangeProject.getSelectedItem());
				}
			}
		});

		RandallPanel projControl = new RandallPanel();
		projControl.setPreferredSize(new Dimension(190, 150));
		projControl.setBorder(new TitledBorder(null, ("Project Control"),	TitledBorder.LEFT, TitledBorder.TOP, iLeftPane.getFont(), Color.gray));

		JButton newProj = new JButton("New Proj");

		newProj.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {

				String lNewName = JOptionPane.showInputDialog(iContentPane,
						"Enter the project name:",
						"New Project",
						JOptionPane.QUESTION_MESSAGE);

				if(checkNewFilename(lNewName)){
					setProject(lNewName);
				}else{
					JOptionPane.showMessageDialog(iContentPane,"Please enter a valid project name.","Error!",JOptionPane.ERROR_MESSAGE);

				}

			}

			private boolean checkNewFilename(String lNewName) {

				if(lNewName == null){
					return false;
				}
				if (lNewName.trim().equals("")){
					return false;
				}
				for (int i = 0; i < iProjectModel.getSize(); i++){
					if (lNewName.equalsIgnoreCase((String) iProjectModel.getElementAt(i))){
						return false;
					}
				}

				Pattern projectNamePattern = Pattern.compile("[^/?*:;{}\\\\]+", Pattern.UNIX_LINES);
				Matcher projectNamePatternMatcher = projectNamePattern.matcher(lNewName);

				if(!projectNamePatternMatcher.matches()){
					return false;
				}
				return true;

			}
		});

		JButton delProj = new JButton("Del Proj");

		delProj.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				if(iChangeProject.getSelectedItem().equals("GoMule")){
					JOptionPane.showMessageDialog(iContentPane,"Cannot delete default project!","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}
				D2Project delProjName = iProject;
				if(JOptionPane.showConfirmDialog(iContentPane,"Are you sure you want to delete this project? (Your clipboard will be lost!)", "Really?", JOptionPane.YES_NO_OPTION) == 0){
					setProject("GoMule");			
					if(!delProjName.delProj()){
						JOptionPane.showMessageDialog(iContentPane,"Error deleting project!","Error!",JOptionPane.ERROR_MESSAGE);
					}else{
						checkProjectsModel();
					}
				}
			}
		});

		JButton clProj = new JButton("Clear Proj");

		clProj.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				if(JOptionPane.showConfirmDialog(iContentPane,"Are you sure you want to clear this project?", "Really?", JOptionPane.YES_NO_OPTION) == 0){
					closeWindows();
					if(!iProject.clearProj()){
						JOptionPane.showMessageDialog(iContentPane,"Error clearing project!","Error!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		JButton lFlavie = new JButton("Proj Flavie Report");

		lFlavie.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{

				ArrayList dFileNames = new ArrayList();

				ArrayList lCharList = iProject.getCharList();
				if ( lCharList != null )
				{
					dFileNames.addAll( lCharList );
				}
				ArrayList lStashList = iProject.getStashList();
				if ( lStashList != null )
				{
					dFileNames.addAll( lStashList );
				}
				if(dFileNames.size() < 1){
					JOptionPane.showMessageDialog(iContentPane,
							"No Chars/Stashes in Project!", 
							"Fail!", JOptionPane.ERROR_MESSAGE);
				}else{
					flavieDump(dFileNames, false);
				}

			}
		});

		JButton projTextDump = new JButton("Proj Txt Dump");

		projTextDump.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				workCursor();
				ArrayList lDumpList = iProject.getCharList();
				String errStr = "";
				if ( lDumpList != null )
				{
					for(int x = 0;x<lDumpList.size();x++){
						try{
							D2Character d2Char = new D2Character((String) lDumpList.get(x));
							if(!projTxtDump((String) lDumpList.get(x),(D2ItemList) d2Char, iProject.getProjectName()+"Dumps")){
								errStr = errStr + "Char: " + (String) lDumpList.get(x) + " failed.\n";
							}
						}catch(Exception e){
							errStr = errStr + "Char: " + (String) lDumpList.get(x) + " failed.\n";
							e.printStackTrace();
						}
					}
				}

				lDumpList = iProject.getStashList();
				if ( lDumpList != null )
				{
					for(int x = 0;x<lDumpList.size();x++){
						try{
							D2Stash d2Stash = new D2Stash((String) lDumpList.get(x));
							if(!projTxtDump((String) lDumpList.get(x),(D2ItemList) d2Stash, iProject.getProjectName()+"Dumps")){
								errStr = errStr + "Stash: " + (String) lDumpList.get(x) + " failed.\n";
							}
						}catch(Exception e){
							errStr = errStr + "Stash: " + (String) lDumpList.get(x) + " failed.\n";
							e.printStackTrace();
						}
					}					
				}
				if((iProject.getCharList().size() + iProject.getStashList().size()) < 1){	
					JOptionPane.showMessageDialog(iContentPane,
							"No Chars/Stashes in Project!", 
							"Fail!", JOptionPane.ERROR_MESSAGE);
				}else if(errStr.equals("")){
					JOptionPane.showMessageDialog(iContentPane,
							"Dumps generated successfully.\nOutput Folder: " + System.getProperty("user.dir") + File.separatorChar + iProject.getProjectName()+"Dumps", 
							"Success!", JOptionPane.INFORMATION_MESSAGE);	
				}else{
					JOptionPane.showMessageDialog(iContentPane,
							"Some txt dumps failed (error msg below).\nOutput Folder: " + System.getProperty("user.dir") + File.separatorChar + iProject.getProjectName()+"Dumps" + "\n\nError: \n" + errStr, 
							"Fail!", JOptionPane.ERROR_MESSAGE);
				}
				defaultCursor();
			}
		});

		projControl.addToPanel(newProj,0,0,1,RandallPanel.HORIZONTAL);
		projControl.addToPanel(delProj,1,0,1,RandallPanel.HORIZONTAL);
		projControl.addToPanel(clProj,0,1,2,RandallPanel.HORIZONTAL);
		projControl.addToPanel(lFlavie,0,2,2,RandallPanel.HORIZONTAL);
		projControl.addToPanel(projTextDump,0,3,2,RandallPanel.HORIZONTAL);

		iLeftPane.addToPanel(iChangeProject,0,0,1,RandallPanel.HORIZONTAL);
		iLeftPane.addToPanel(iViewProject,0,1,1,RandallPanel.BOTH);
		iLeftPane.addToPanel(projControl,0,2,1,RandallPanel.NONE);
	}
	/**
	 * Creates a Flavie HTML dump of D2Files.
	 * @param dFileNames ArrayList of filenames to dump
	 * @param singleDump Bool, if set dump is current window, else dump is project.
	 */

	private void flavieDump(ArrayList dFileNames, boolean singleDump){
		try{
			String reportName = null;
			if(singleDump){

				GoMuleView lView = iDesktopPane.getSelectedView();
				if ( lView != null )
				{
					if ( lView instanceof GoMuleViewChar )
					{
						reportName = ((GoMuleViewChar) lView).getViewChar().getChar().getCharName() + iProject.getReportName();
					}else if ( lView instanceof GoMuleViewStash ) {
						reportName = ((GoMuleViewStash) lView).getViewStash().getStashName() + iProject.getReportName();
						reportName = reportName.replace(".d2x", "");
					}
				}
			}else{
				reportName = iProject.getProjectName() + iProject.getReportName();
			}
			if ( reportName != null )
			{
				new Flavie(
						reportName, iProject.getReportTitle(), 
						iProject.getDataName(), iProject.getStyleName(),
						dFileNames,
						iProject.isCountAll(), iProject.isCountEthereal(),
						iProject.isCountStash(), iProject.isCountChar()
				);
			}

//			JOptionPane.showMessageDialog(iContentPane,
//			"Flavie says reports generated successfully.\nFile: " + System.getProperty("user.dir") + File.separatorChar + reportName + ".html", 
//			"Success!", JOptionPane.INFORMATION_MESSAGE);			
		}
		catch (Exception pEx)
		{
			JOptionPane.showMessageDialog(iContentPane,
					"Flavie report failed!", 
					"Fail!", JOptionPane.ERROR_MESSAGE);
			displayErrorDialog(pEx);
		}
	}
	/**
	 * Adds all the elements to the right panel of the main GUI
	 *
	 */
	private void createRightPane() {

		iRightPane = new JPanel();
		iRightPane.setPreferredSize(new Dimension(190,768));
		iRightPane.setMinimumSize(new Dimension(190,0));
		iRightPane.setLayout(new BoxLayout(iRightPane, BoxLayout.Y_AXIS));
		try
		{
			iClipboard = D2ViewClipboard.getInstance(this);
		}
		catch (Exception pEx)
		{
			pEx.printStackTrace();
			JTextArea lText = new JTextArea();
			lText.setText(pEx.getMessage());
			JScrollPane lScroll = new JScrollPane(lText);
			iContentPane.removeAll();
			iContentPane.add(lScroll, BorderLayout.CENTER);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(java.awt.event.WindowEvent e)
				{
					System.exit(0);
				}
			});
		}

		RandallPanel itemControl = new RandallPanel();
		itemControl.setBorder(new TitledBorder(null, ("Item Control"),	TitledBorder.LEFT, TitledBorder.TOP, iRightPane.getFont(), Color.gray));

		itemControl.setPreferredSize(new Dimension(190, 160));
		itemControl.setSize(new Dimension(190, 160));
		itemControl.setMaximumSize(new Dimension(190, 160));
		itemControl.setMinimumSize(new Dimension(190, 160));

		pickAll = new JButton("Pick All");
		pickAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {

				GoMuleView lView = iDesktopPane.getSelectedView();
				if ( lView != null )
				{
					D2ItemList lList = lView.getItemLists();
					try
					{
						lList.ignoreItemListEvents();

						if(lList.getFilename().endsWith(".d2s") && getProject().getIgnoreItems()){

							for(int x = 0;x<lList.getNrItems();x++){

								if(((D2Item)lList.getItemList().get(x)).isMoveable()){
									moveToClipboard(((D2Item)lList.getItemList().get(x)), lList);
									x--;
								}
							}

						}else{

							for(int x = 0;x<lList.getNrItems();x++){
								moveToClipboard(((D2Item)lList.getItemList().get(x)), lList);
								x--;
							}

						}

					}finally{
						lList.listenItemListEvents();
						lList.fireD2ItemListEvent();
					}


				}
			}
		});

		dropAll = new JButton("Drop All");
		dropAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				GoMuleView lView = iDesktopPane.getSelectedView();
				if ( lView != null )
//					if(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()) > -1)
				{
					D2ItemList lList = lView.getItemLists();
//					D2ItemList iList = ((D2ItemContainer) iOpenWindows.get(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()))).getItemLists();
					try{
						lList.ignoreItemListEvents();

						if ( lView instanceof GoMuleViewChar )
						{
							D2ViewChar iCharacter = ((GoMuleViewChar) lView).getViewChar();
							for(int x = 2;x> -1;x--){
								iCharacter.putOnCharacter(x,  D2ViewClipboard.getItemList());
							}
						}
						else
						{
							ArrayList lItemList = D2ViewClipboard.removeAllItems();
							while (lItemList.size() > 0)
							{
								lList.addItem((D2Item) lItemList.remove(0));
							}

						}
					}finally{
						lList.listenItemListEvents();
						lList.fireD2ItemListEvent();
					}
				}
			}
		});

		pickFrom = new JButton("Pickup From ...");
		pickChooser = new JComboBox(new String[]{"Stash", "Inventory", "Cube", "Equipped"});
		pickFrom.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				GoMuleView lView = iDesktopPane.getSelectedView();
				if ( lView != null )
//					if(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()) > -1)
				{
					D2ItemList lList = lView.getItemLists();
//					D2ItemList iList = ((D2ItemContainer) iOpenWindows.get(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()))).getItemLists();
					try{
						lList.ignoreItemListEvents();

						for(int x = 0;x<lList.getNrItems();x++){
							D2Item remItem  = ((D2Item)lList.getItemList().get(x));
							if(!remItem.isMoveable() && pickChooser.getSelectedIndex()!=3 && getProject().getIgnoreItems()){
								continue;
							}
							switch(pickChooser.getSelectedIndex()){
							case 0:

								if(remItem.get_location() == 0 && remItem.get_panel() == 5){
									moveToClipboard(remItem, lList);
									x--;
								}
								break;
							case 1:
								if(remItem.get_location() == 0 && remItem.get_panel() == 1){
									moveToClipboard(remItem, lList);
									x--;
								}
								break;
							case 2:
								if(remItem.get_location() == 0 && remItem.get_panel() == 4){
									moveToClipboard(remItem, lList);
									x--;
								}
								break;
							case 3:
								if(remItem.get_location() == 1){
									moveToClipboard(remItem, lList);
									x--;
								}
								break;
							}
						}
					}finally{
						lList.listenItemListEvents();
						lList.fireD2ItemListEvent();
					}
				}
			}
		});

		dropTo = new JButton("Drop To ...");
		dropChooser = new JComboBox(new String[]{"Stash", "Inventory", "Cube"});

		dropTo.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				GoMuleView lView = iDesktopPane.getSelectedView();
				if ( lView instanceof GoMuleViewChar )
//					if(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()) > -1)
				{
					D2ViewChar lCharacter = ((GoMuleViewChar) lView).getViewChar();
//					D2ViewChar iCharacter = ((D2ViewChar) iOpenWindows.get(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame())));
					lCharacter.putOnCharacter(dropChooser.getSelectedIndex(),  D2ViewClipboard.getItemList());
				}
			}
		});



		itemControl.addToPanel(pickAll,0,0,1,RandallPanel.HORIZONTAL);
		itemControl.addToPanel(dropAll,1,0,1,RandallPanel.HORIZONTAL);

		itemControl.addToPanel(pickFrom,0,1,2,RandallPanel.HORIZONTAL);

		itemControl.addToPanel(pickChooser,0,2,2,RandallPanel.HORIZONTAL);

		itemControl.addToPanel(dropTo,0,3,2,RandallPanel.HORIZONTAL);

		itemControl.addToPanel(dropChooser,0,4,2,RandallPanel.HORIZONTAL);



		RandallPanel charControl = new RandallPanel();
		charControl.setBorder(new TitledBorder(null, ("Output Control"),	TitledBorder.LEFT, TitledBorder.TOP, iRightPane.getFont(), Color.gray));
		charControl.setPreferredSize(new Dimension(190, 80));
		charControl.setSize(new Dimension(190, 80));
		charControl.setMaximumSize(new Dimension(190, 80));
		charControl.setMinimumSize(new Dimension(190, 80));


		dumpBut = new JButton("Perform txt Dump");
		dumpBut.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				GoMuleView lView = iDesktopPane.getSelectedView();
				if ( lView != null )
//					if(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()) > -1)
				{
					if ( singleTxtDump( lView.getItemContainer().getFileName()) )
					{

//						if(singleTxtDump(((D2ItemContainer)iOpenWindows.get(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()))).getFileName())){
						JOptionPane.showMessageDialog(iContentPane,
								"Char/Stash dump was a success.\nFile: " + lView.getItemContainer().getFileName() + ".txt", 
								"Success!", JOptionPane.INFORMATION_MESSAGE);

					}else{
						JOptionPane.showMessageDialog(iContentPane,
								"Char/Stash dump failed!", 
								"Fail!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		flavieSingle = new JButton("Single Flavie Report");
		flavieSingle.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				GoMuleView lView = iDesktopPane.getSelectedView();
				if ( lView != null )
//					if(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()) > -1)
				{
					ArrayList dFileNames = new ArrayList();
					dFileNames.add( lView.getItemContainer().getFileName() );
					flavieDump(dFileNames, true);
				}

			}
		});

		charControl.addToPanel(dumpBut,0,0,1,RandallPanel.HORIZONTAL);
		charControl.addToPanel(flavieSingle,0,1,1,RandallPanel.HORIZONTAL);

		iRightPane.add(iClipboard);
		iRightPane.add(itemControl);
		iRightPane.add(charControl);
		iRightPane.add(Box.createVerticalGlue());
	}

	/**
	 * Moves an item to the clipboard, first removing it from the itemList it is currently in
	 * @param remItem D2Item to move to clipboard
	 * @param iList D2ItemList that contains the item
	 */

	private void moveToClipboard(D2Item remItem , D2ItemList iList) {
		iList.removeItem(remItem);
		GoMuleView lView = iDesktopPane.getSelectedView();
		if ( lView instanceof GoMuleViewChar )
//			if(((D2ItemContainer) iOpenWindows.get(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()))).getFileName().endsWith(".d2s"))
		{
			((D2Character)iList).unequipItem(remItem);
//			((D2ViewChar) iOpenWindows.get(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()))).paintCharStats();
			((GoMuleViewChar) lView).getViewChar().paintCharStats();
		}
		D2ViewClipboard.addItem(remItem);
	}
	/**
	 * Creates the top menubar of the GUI
	 *
	 */
	private void createMenubar() {

		iMenuBar = new JMenuBar();
		iMenuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
		JMenu fileMenu = new JMenu("File");

		JMenuItem openChar = new JMenuItem("Open Character");
		JMenuItem newStash = new JMenuItem("New Stash");
		JMenuItem openStash = new JMenuItem("Open Stash");
		JMenuItem saveAll = new JMenuItem("Save All");
		JMenuItem exitProg = new JMenuItem("Exit");

		JMenu projMenu = new JMenu("Project");
		JMenuItem projOpt = new JMenuItem("Preferences");
		JMenu aboutMenu = new JMenu("About...");
		iMenuBar.add(fileMenu);
		iMenuBar.add(projMenu);
		iMenuBar.add(aboutMenu);

		fileMenu.add(openChar);
		fileMenu.addSeparator();
		fileMenu.add(newStash);
		fileMenu.add(openStash);
		fileMenu.addSeparator();
		fileMenu.add(saveAll);
		fileMenu.addSeparator();
		fileMenu.add(exitProg);

		projMenu.add(projOpt);

		this.setJMenuBar(iMenuBar);

		aboutMenu.addMouseListener(new MouseAdapter(){

			public void mousePressed(MouseEvent e){

				displayAbout();
			}
		});

		projOpt.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e){

				D2ProjectSettingsDialog lDialog = new D2ProjectSettingsDialog(D2FileManager.this);
				lDialog.setVisible(true);
			}
		});

		openChar.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e){
				openChar(true);

			}
		});

		newStash.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e){
				newStash(true);

			}
		});

		openStash.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e){
				openStash(true);

			}
		});

		saveAll.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e){
				saveAll();

			}
		});

		exitProg.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e){
				closeListener();

			}
		});




		checkProjects();

	}
	/**
	 * Gets current project
	 * @return Active D2Project
	 */
	public D2Project getProject()
	{
		return iProject;
	}
	/**
	 * Sets the current project to pProject
	 * @param pProject D2Project to set current project to
	 * @throws Exception If there is a bad filename in project
	 */

	//TODO Fix EXCEPTIONS!
	public void setProject(D2Project pProject) throws Exception
	{
		iProject = pProject;
		iClipboard.setProject(iProject);
		iViewProject.setProject(pProject);
	}

	/**
	 * Creates toolbar at the top of the GUI
	 *
	 */
	private void createToolbar()
	{
		iToolbar = new JToolBar();
		// sets whether the toolbar can be made to float
		iToolbar.setFloatable(false);

		// sets whether the border should be painted
		iToolbar.setBorderPainted(false);
		iToolbar.add(new JLabel("Character"));

		JButton lOpenD2S = new JButton(D2ImageCache.getIcon("open.gif"));
		lOpenD2S.setToolTipText("Open Character");

		lOpenD2S.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				openChar(true);
			}
		});
		iToolbar.add(lOpenD2S);

		JButton lAddD2S = new JButton(D2ImageCache.getIcon("add.gif"));
		lAddD2S.setToolTipText("Add Character");
		lAddD2S.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				openChar(false);
			}
		});
		iToolbar.add(lAddD2S);
		iToolbar.addSeparator();

		iToolbar.add(new JLabel("Stash"));

		JButton lNewD2X = new JButton(D2ImageCache.getIcon("new.gif"));
		lNewD2X.setToolTipText("New ATMA Stash");
		lNewD2X.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				newStash(true);
			}
		});
		iToolbar.add(lNewD2X);

		JButton lOpenD2X = new JButton(D2ImageCache.getIcon("open.gif"));
		lOpenD2X.setToolTipText("Open ATMA Stash");
		lOpenD2X.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				openStash(true);
			}
		});
		iToolbar.add(lOpenD2X);

		JButton lAddD2X = new JButton(D2ImageCache.getIcon("add.gif"));
		lAddD2X.setToolTipText("Add ATMA Stash");
		lAddD2X.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				openStash(false);
			}
		});
		iToolbar.add(lAddD2X);

		iToolbar.addSeparator();

		iToolbar.add(new JLabel("     "));

		JButton lSaveAll = new JButton(D2ImageCache.getIcon("save.gif"));
		lSaveAll.setToolTipText("Save All");
		lSaveAll.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				saveAll();
			}
		});
		iToolbar.add(lSaveAll);



		JButton lDropCalc = new JButton(D2ImageCache.getIcon("dc.gif"));
		lDropCalc.setToolTipText("Run Drop Calculator");
		lDropCalc.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				workCursor();
				try{
					new RealGUI();
				}finally{
					defaultCursor();
				}


			}
		});
		iToolbar.add(lDropCalc);

		JButton lCancelAll = new JButton(D2ImageCache.getIcon("cancel.gif"));
		lCancelAll.setToolTipText("Cancel (reload all)");
		lCancelAll.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				cancelAll();
			}
		});
		iToolbar.add(lCancelAll);

		iToolbar.addSeparator();

		iContentPane.add(iToolbar, BorderLayout.NORTH);
	}
	/**
	 * Checks current project exists and loads project properties
	 *
	 */
	private void checkProjects()
	{
		try
		{
			File lProjectsDir = new File(D2Project.PROJECTS_DIR);
			if (!lProjectsDir.exists())
			{
				lProjectsDir.mkdir();
			}

			File lProps = new File(D2Project.PROJECTS_DIR + File.separator + "projects.properties");
			if (!lProps.exists())
			{
				lProps.createNewFile();
			}
			FileInputStream lPropsIn = new FileInputStream(lProps);
			iProperties = new Properties();
			iProperties.load(lPropsIn);
			lPropsIn.close();

			String lCurrent = iProperties.getProperty("current-project"); // ,
			// "DefaultProject");

			if (lCurrent != null)
			{
				String lProjectDirStr = D2Project.PROJECTS_DIR + File.separator + lCurrent;
				File lProjectDir = new File(lProjectDirStr);
				if (!lProjectDir.exists())
				{
					lCurrent = null;
				}
			}
			if (lCurrent == null)
			{
				lCurrent = "GoMule";
			}

			iProject = new D2Project(this, lCurrent);
//			iBtnProjectSelection.setText(lCurrent);
		}
		catch (Exception pEx)
		{
			displayErrorDialog(pEx);
			iProject = null;
			iProperties = null;
		}
	}

	/** called on exit or when this window is closed
	 * zip through and make sure all the character
	 * windows close properly, because character
	 * windows save on close
	 */
	public void closeListener()
	{
		closeWindows();
		System.exit(0);
	}
	/**
	 * Saves and closes an open file
	 * @param pFileName Open file that needs to be closed
	 */
	public void closeFileName(String pFileName)
	{
		saveAll();
		iDesktopPane.closeView(pFileName);
//		for ( int i = 0 ; i < iOpenWindows.size() ; i++ )
//		{
//		D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(i);

//		if ( lItemContainer.getFileName().equalsIgnoreCase(pFileName) )
//		{
//		lItemContainer.closeView();
//		}
//		}
	}

	/**
	 * Dumps a file to individual txt file
	 * @param pFileName Individual file name
	 * @param lList D2ItemList which contains item objs
	 * @param folder Project name to dump to (if dumping project, not individual)
	 * @return success/faliure of dump
	 */
	public boolean projTxtDump(String pFileName, D2ItemList lList, String folder)
	{
		String lFileName = null;
		if(folder == null){

			lFileName = pFileName+".txt";

		}else{

			if(lList.getFilename().endsWith(".d2s")){
				lFileName = ((D2Character)lList).getCharName() + ".d2s";
			}else{
				lFileName = ((D2Stash)lList).getFileNameEnd();
			}
			lFileName = folder + File.separator + lFileName + ".txt";

			File lFile = new File(folder);
			if(!lFile.exists()){
				if(!lFile.mkdir()){
					return false;
				}
			}

		}
		return writeTxtDump(lFileName, lList);
	}
	/**
	 * Dumps a file to txt format
	 * @param pFileName file to dump
	 * @return success/faliure of dump
	 */

	//TODO Remove this? See above
	public boolean singleTxtDump(String pFileName)
	{
		D2ItemList lList = null;
		String lFileName = null;

		if ( pFileName.equalsIgnoreCase("all") ){
			if ( iViewAll != null ){
				lFileName = "." + File.separator + "all.txt";
				lList = iViewAll.getItemLists();
			}

		}else{

			lList = (D2ItemList) iItemLists.get(pFileName);
			lFileName = pFileName+".txt";

		}

		return writeTxtDump(lFileName, lList);

	}
	/**
	 * Dumps a file to txt
	 * @param lFileName File name to dump
	 * @param lList Item list to dump
	 * @return success/faliure of dump
	 */

	//TODO Remove this? See above
	private boolean writeTxtDump(String lFileName, D2ItemList lList){
		if ( lList != null && lFileName != null ){
			try{
				File lFile = new File(lFileName);
				System.err.println("File: " + lFile.getCanonicalPath() );

				PrintWriter lWriter = new PrintWriter(new FileWriter( lFile.getCanonicalPath() ));

				lList.fullDump(lWriter);
				lWriter.flush();
				lWriter.close();
				return true;
			}catch ( Exception pEx ){
				pEx.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Saves and closes all currently open windows
	 *
	 */
	public void closeWindows()
	{
		saveAll();
		iDesktopPane.closeViewAll();
	}

	/** called on exit or when this window is closed
	 * zip through and make sure all the character
	 * windows close properly, because character
	 * windows save on close
	 */
	public void saveAll()
	{
		if (iProject != null)
		{
			iProject.saveProject();
		}
		saveAllItemLists();
	}
	/**
	 * Saves all item lists in their current state
	 *
	 */
	public void saveAllItemLists()
	{
		checkAll(false);

		iClipboard.saveView();
		Iterator lIterator = iItemLists.keySet().iterator();
		while ( lIterator.hasNext() )
		{
			String lFileName = (String) lIterator.next();
			D2ItemList lList = getItemList(lFileName);
			if ( lList.isModified() )
			{
				lList.save(iProject);
			}
		}
	}
	/**
	 * Cancels all changes
	 *
	 */
	public void cancelAll()
	{
		checkAll(true);
	}
	/**
	 * Resets all changes, reloading all files
	 * @param pCancel Not sure.
	 */
	private void checkAll(boolean pCancel)
	{
		if ( iIgnoreCheckAll )
		{
			return;
		}
		try
		{
			iIgnoreCheckAll = true;
			boolean lChanges = pCancel;
			boolean lModifiedChanges = pCancel;
			D2ItemList lClipboardStash = iClipboard.getItemLists();

			if ( !lClipboardStash.checkTimestamp() )
			{
				lChanges = true;
				if ( iClipboard.isModified() )
				{
					lModifiedChanges = true;
				}
			}

			Iterator lIterator = iItemLists.keySet().iterator();
			while ( lIterator.hasNext() )
			{
				String lFileName = (String) lIterator.next();
				D2ItemList lList = (D2ItemList) iItemLists.get(lFileName);
				if ( !(lList instanceof D2ItemListAll) && !lList.checkTimestamp() )
				{
					lChanges = true;
					if ( lList.isModified() )
					{
						lModifiedChanges = true;
					}
				}
			}

			if ( lChanges )
			{
				if ( pCancel )
				{
					displayTextDialog("Info", "Reloading on request" );
				}
				else if ( lModifiedChanges )
				{
					displayTextDialog("Info", "Changes on file system detected, reloading files." );
				}
				else
				{
					displayTextDialog("Info", "Changes on file system detected, reloading files." );
				}
			}

			if ( lChanges )
			{
				if ( !lClipboardStash.checkTimestamp() || ( lModifiedChanges && lClipboardStash.isModified() ) )
				{
					try
					{
						iClipboard.setProject(iProject);
					}
					catch ( Exception pEx )
					{
						displayErrorDialog( pEx );
					}
				}

				Iterator lViewIterator = iDesktopPane.getIteratorView();
				while ( lViewIterator.hasNext() )
				{
					GoMuleView lView = (GoMuleView) lViewIterator.next();
					D2ItemContainer lContainer = lView.getItemContainer();
					D2ItemList lList = lContainer.getItemLists();
					if (iViewAll != null && lList == iViewAll.getStash() )
					{
						lContainer.disconnect(null);
						lContainer.connect();
					}else{
						if ( !lList.checkTimestamp() || ( lModifiedChanges && lList.isModified() ) )
						{
							String lFileName = lList.getFilename();
							lContainer.disconnect(null);
							if ( iViewAll != null && iViewAll.getItemLists() instanceof D2ItemListAll )
							{
								((D2ItemListAll) iViewAll.getItemLists()).disconnect( lFileName );
							}

							lContainer.connect();
							if ( iViewAll != null && iViewAll.getItemLists() instanceof D2ItemListAll )
							{
								((D2ItemListAll) iViewAll.getItemLists()).connect( lFileName );
							}
						}
					}
				}
			}
		}
		catch ( Exception pEx )
		{
			pEx.printStackTrace();
		}
		finally
		{
			iIgnoreCheckAll = false;
		}
	}

//	private void handleLoadError(String pFileName, Exception pEx){
//	// close this view & all view
//	for ( int i = 0 ; i < iOpenWindows.size() ; i++ ){
//	D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(i);
//	if (lItemContainer.getFileName().equalsIgnoreCase(pFileName) || lItemContainer.getFileName().toLowerCase().equals("all")){
//	lItemContainer.closeView();
//	}
//	}
//	displayErrorDialog( pEx );
//	}

	/**
	 * Opens file chooser for user to add a character to project
	 * @return file chooser dialog
	 */
	private JFileChooser getCharDialog(){
		return iProject.getCharDialog();
	}
	/**
	 * Opens file chooser for user to add a stash to project
	 * @return file chooser dialog
	 */
	private JFileChooser getStashDialog(){
		return iProject.getStashDialog();
	}

	// file->open callback
	// throw up a dialog for picking d2s files
	// then open that character and add it to the
	// vector of character windows

	/**
	 * Opens dialog to add new character, adds character to current project
	 * @param load If true, loads and adds the character, if not, just adds the character
	 */
	public void openChar(boolean load){
		JFileChooser lCharChooser = getCharDialog();
		lCharChooser.setMultiSelectionEnabled(true);
		if (lCharChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
//			String[] fNamesOut = new String[lCharChooser.getSelectedFiles().length];
			for(int x = 0;x<lCharChooser.getSelectedFiles().length;x=x+1){
				java.io.File lFile = lCharChooser.getSelectedFiles()[x];

				try
				{
					String lFilename = lFile.getAbsolutePath();
					openChar(lFilename, load);
				}
				catch (Exception pEx)
				{
					D2FileManager.displayErrorDialog(pEx);
				}
			}
		}
	}
	/**
	 * Opens characters based on the filename
	 * @param pCharName filename of char
	 * @param load If true, loads and adds the character, if not, just adds the character
	 */
	public void openChar(String pCharName, boolean load)
	{
		GoMuleView lExisting = null;
		Iterator lViewIterator = iDesktopPane.getIteratorView();
		while ( lViewIterator.hasNext() )
		{
			GoMuleView lView = (GoMuleView) lViewIterator.next();

			if (lView.getItemContainer().getFileName().equals(pCharName))
			{
				lExisting = lView;
			}
		}
		if(load){
			if (lExisting != null)
			{
//				internalWindowForward(((JInternalFrame) lExisting));
				iDesktopPane.showView( lExisting );
			}
			else
			{
				D2ViewChar lCharView = new D2ViewChar(D2FileManager.this, pCharName);
//				lCharView.setLocation(10 + (iOpenWindows.size() * 10), 10+ (iOpenWindows.size() * 10));
				addToOpenWindows(lCharView);
//				internalWindowForward(lCharView);
//				iDesktopPane.showView( lExisting );
			}
		}
		iProject.addChar(pCharName);
	}

//	private void internalWindowForward(D2ViewChar pView) {

//	iDesktopPane.showView( pView );
////	frame.toFront();
////	try {
////	frame.setSelected(true);
////	} catch (PropertyVetoException e) {
////	//Shouldn't worry too much if this happens I guess?
////	e.printStackTrace();
////	}
//	}
	/**
	 * Returns project tree view
	 */
	public D2ViewProject getViewProject()
	{
		return iViewProject;
	}
	/**
	 * Adds pane to current open windows
	 * @param pView
	 */
	private void addToOpenWindows(GoMuleView pView)
	{
//		iOpenWindows.add( pView );
		iDesktopPane.addView( pView );

		pView.getDisplayHandler().addDesktopListener( new GoMuleDesktopListener() 
		{

			public void viewClosing(GoMuleView pView) 
			{
				saveAll();
				pView.getItemContainer().closeView();
			}

			public void viewActivated(GoMuleView pView) 
			{
				if ( pView instanceof  GoMuleViewStash)
				{
					pickFrom.setEnabled(false);
					pickChooser.setEnabled(false);
					dropTo.setEnabled(false);
					dropChooser.setEnabled(false);
					dropAll.setEnabled(true);
					flavieSingle.setEnabled(true);
					dumpBut.setEnabled(true);
				}
//				else if(((D2ItemContainer) iOpenWindows.get(iOpenWindows.indexOf(iDesktopPane.getSelectedFrame()))).getFileName().endsWith(".d2s"))
				else if ( pView instanceof GoMuleViewChar )
				{
					pickFrom.setEnabled(true);
					pickChooser.setEnabled(true);
					dropTo.setEnabled(true);
					dropChooser.setEnabled(true);
					dropAll.setEnabled(true);
					flavieSingle.setEnabled(true);
					dumpBut.setEnabled(true);
				}
				else
				{
					pickFrom.setEnabled(false);
					pickChooser.setEnabled(false);
					dropTo.setEnabled(false);
					dropChooser.setEnabled(false);
					dropAll.setEnabled(false);
					flavieSingle.setEnabled(false);
					dumpBut.setEnabled(false);
				}
			}
		});
//		((JInternalFrame) pContainer).setOpaque(true);
//		((JInternalFrame) pContainer).addInternalFrameListener(new InternalFrameListener(){

//		public void internalFrameActivated(InternalFrameEvent arg0) {
//		}
//		public void internalFrameClosed(InternalFrameEvent arg0) {}
//		public void internalFrameClosing(InternalFrameEvent arg0) {}
//		public void internalFrameDeactivated(InternalFrameEvent arg0) {}
//		public void internalFrameDeiconified(InternalFrameEvent arg0) {}
//		public void internalFrameIconified(InternalFrameEvent arg0) {}
//		public void internalFrameOpened(InternalFrameEvent arg0) {}
//		});
		iViewProject.notifyFileOpened( pView.getItemContainer().getFileName() );

		if ( pView instanceof GoMuleViewStash && pView.getItemContainer().getFileName().equalsIgnoreCase("all") )
		{
			iViewAll = ((GoMuleViewStash) pView).getViewStash();
		}
	}
	/**
	 * Removes from open windows
	 * @param pView View to remove
	 */
	public void removeFromOpenWindows(GoMuleView pView)
	{
		iDesktopPane.removeView(pView);
//		iOpenWindows.remove( pContainer );
//		iDesktopPane.remove( (JInternalFrame) pContainer );
		iViewProject.notifyFileClosed( pView.getItemContainer().getFileName() );
		repaint();

		if ( pView.getItemContainer().getFileName().equalsIgnoreCase("all") )
		{
			iViewAll = null;
		}

//		System.gc();
	}
	/**
	 * Opens a stash
	 * @param load If true, loads and adds the stash, if not, just adds the stash
	 */
	public void openStash(boolean load)
	{
		JFileChooser lStashChooser = getStashDialog();
		lStashChooser.setMultiSelectionEnabled(true);
		if (lStashChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			handleStash(lStashChooser, load);
		}
	}
	/**
	 * Creates a new stash
	 * @param load  If true, loads and adds the stash, if not, just adds the stash
	 */
	public void newStash(boolean load)
	{
		JFileChooser lStashChooser = getStashDialog();
		lStashChooser.setMultiSelectionEnabled(true);
		if (lStashChooser.showDialog(this, "New Stash") == JFileChooser.APPROVE_OPTION)
		{
			String[] lFileName = handleStash(lStashChooser, load);
			for(int x = 0;x<lFileName.length;x=x+1){
				if (lFileName[x] != null)
				{
					D2ItemList lList = (D2ItemList) iItemLists.get(lFileName[x]);

					lList.save(iProject);
				}
			}
		}
	}
	/**
	 * Handles loading of a stash
	 * @param pStashChooser File chooser object which contains the selected files
	 * @param load  If true, loads and adds the stash, if not, just adds the stash
	 * @return String array of opened file names
	 */
	private String[] handleStash(JFileChooser pStashChooser, boolean load)
	{

		String[] fNamesOut = new String[pStashChooser.getSelectedFiles().length];
		File[] stashList = pStashChooser.getSelectedFiles();
		if(pStashChooser.getSelectedFiles().length == 0 && pStashChooser.getSelectedFile()!=null){
			stashList = new File[]{pStashChooser.getSelectedFile()};
			fNamesOut = new String[1];
		}

		for(int x = 0;x<stashList.length;x=x+1){
			System.out.println(stashList.length);
			java.io.File lFile = stashList[x];
			try
			{
				String lFilename = lFile.getAbsolutePath();
				if (!lFilename.endsWith(".d2x"))
				{
					// force stash name to end with .d2x
					lFilename += ".d2x";
				}

				openStash(lFilename, load);
				fNamesOut[x] = lFilename;
			}
			catch (Exception pEx)
			{
				D2FileManager.displayErrorDialog(pEx);
				fNamesOut[x] = null;
			}
		}
		return fNamesOut;
	}
	/**
	 * Opens a stash
	 * @param pStashName Stash name to open
	 * @param load  If true, loads and adds the stash, if not, just adds the stash
	 */
	public void openStash(String pStashName, boolean load)
	{
		GoMuleView lExisting = null;
		Iterator lViewIterator = iDesktopPane.getIteratorView();
		while ( lViewIterator.hasNext() )
		{
			GoMuleView lView = (GoMuleView) lViewIterator.next();

			if ( lView.getItemContainer().getFileName().equals(pStashName) )
			{
				lExisting = lView;
			}
		}

		if(load){
			if (lExisting != null)
			{
				iDesktopPane.showView( lExisting );
			}
			else
			{
				D2ViewStash lStashView = new D2ViewStash(D2FileManager.this, pStashName);
				addToOpenWindows(lStashView);
			}
		}

		iProject.addStash(pStashName);
	}
	/**
	 * Toggles the 'about' pane
	 *
	 */
	public void displayAbout()
	{
		JOptionPane.showMessageDialog(this, "A java-based Diablo II muling application\n\noriniginally created by Andy Theuninck (Gohanman)\nVersion 0.1a"
				+ "\n\ncurrent release by Randall & Silospen\nVersion " + CURRENT_VERSION + "\n\nAnd special thanks to:" + "\n\tHakai_no_Tenshi & Gohanman for helping me out with the file formats"
				+ "\nRTB for all his help.\n\tThe Super Beta Testers:\nSkinhead On The MBTA\nnubikon\nOscuro\nThyiad\nMoiselvus\nPurpleLocust\nAnd anyone else I've forgotten..!", "About", JOptionPane.PLAIN_MESSAGE);
	}
	/**
	 * Adds an item list to teh project
	 * @param pFileName File itemlist belongs to 
	 * @param pListener Item list listner
	 * @return item list
	 * @throws Exception if list is incorrent type
	 */
	public D2ItemList addItemList(String pFileName, D2ItemListListener pListener) throws Exception
	{
		D2ItemList lList;

		if ( iItemLists.containsKey(pFileName) )
		{
			lList = getItemList(pFileName);
		}
		else if ( pFileName.equalsIgnoreCase("all") )
		{
			lList = new D2ItemListAll(this, iProject);
//			iViewProject.notifyItemListOpened("all");
		}
		else if ( pFileName.toLowerCase().endsWith(".d2s") )
		{
			lList = new D2Character(pFileName);

			int lType = getProject().getType();
			if (lType == D2Project.TYPE_SC && (!lList.isSC() || lList.isHC()))
			{
				throw new Exception("Character is not Softcore (SC), this is a project requirement");
			}
			if (lType == D2Project.TYPE_HC && (lList.isSC() || !lList.isHC()))
			{
				throw new Exception("Character is not Hardcore (HC), this is a project requirement");
			}

			System.err.println("Add Char: " + pFileName );
			iItemLists.put(pFileName, lList);
			iViewProject.notifyItemListRead(pFileName);
		}
		else if ( pFileName.toLowerCase().endsWith(".d2x") )
		{
			lList = new D2Stash(pFileName);

			int lType = getProject().getType();
			if ( lType == D2Project.TYPE_SC && (!lList.isSC() || lList.isHC()) )
			{
				throw new Exception("Stash is not Softcore (SC), this is a project requirement");
			}
			if ( lType == D2Project.TYPE_HC && (lList.isSC() || !lList.isHC()) )
			{
				throw new Exception("Stash is not Hardcore (HC), this is a project requirement");
			}
			System.err.println("Add Stash: " + pFileName );
			iItemLists.put(pFileName, lList);
			iViewProject.notifyItemListRead(pFileName);
		}
		else 
		{
			throw new Exception("Incorrect filename: " + pFileName );
		}

		if ( pListener != null )
		{
			lList.addD2ItemListListener(pListener);
		}

		return lList;
	}
	/**
	 * Returns the item list associated with a filename
	 * @param pFileName Filename of the char/stash
	 * @return itemlist of the char/stash
	 */
	public D2ItemList getItemList(String pFileName)
	{
		return (D2ItemList) iItemLists.get(pFileName);
	}
	/**
	 * Removes item list from project
	 * @param pFileName filename of char/stash
	 * @param pListener item list listner associated with item list
	 */
	public void removeItemList(String pFileName, D2ItemListListener pListener)
	{
		D2ItemList lList = getItemList(pFileName);
		if(lList == null){ 
			return;
		}
		if ( pListener != null )
		{
			lList.removeD2ItemListListener(pListener);
		}
		if ( !lList.hasD2ItemListListener() )
		{
			System.err.println("Remove file: " + pFileName );
			iItemLists.remove(pFileName);
			iViewProject.notifyItemListClosed(pFileName);
		}
	}

	/**
	 * Throws an error dialog
	 * @param pException
	 */
	public static void displayErrorDialog(Exception pException)
	{
		displayErrorDialog(iCurrent, pException);
	}
	/**
	 * Throws error dialog
	 * @param pParent
	 * @param pException
	 */
	public static void displayErrorDialog(Window pParent, Exception pException)
	{
		pException.printStackTrace();

		String lText = "Error\n\n" + pException.getMessage() + "\n";

		StackTraceElement trace[] = pException.getStackTrace();
		for (int i = 0; i < trace.length; i++)
		{
			lText += "\tat " + trace[i] + "\n";
		}


		displayTextDialog( pParent, "Error", lText );
	}
	/**
	 * Throws a text dialog
	 * @param pTitle Title
	 * @param pText Content
	 */
	public static void displayTextDialog(String pTitle, String pText)
	{
		displayTextDialog(iCurrent, pTitle, pText);
	}
	/**
	 * Throws a text dialog
	 * @param pParent
	 * @param pTitle Title
	 * @param pText Content
	 */
	public static void displayTextDialog(Window pParent, String pTitle, String pText)
	{
		JDialog lDialog;
		if (pParent instanceof JFrame)
		{
			lDialog = new JDialog((JFrame) pParent, pTitle, true);
		}
		else
		{
			lDialog = new JDialog((JDialog) pParent, pTitle, true);
		}
		RandallPanel lPanel = new RandallPanel();
		JTextArea lTextArea = new JTextArea();
		JScrollPane lScroll = new JScrollPane(lTextArea);

		if ( pTitle.equalsIgnoreCase("error") )
		{
			lScroll.setPreferredSize(new Dimension(640, 480));
		}
		lPanel.addToPanel(lScroll, 0, 0, 1, RandallPanel.BOTH);

		lTextArea.setText(pText);
		if ( pText.length() > 1 )
		{
			lTextArea.setCaretPosition(0);
		}
		lTextArea.setEditable(false);

		lDialog.setContentPane(lPanel);
		lDialog.pack();
		lDialog.setLocationRelativeTo(null);
		lDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		lDialog.setVisible(true);
	}
	/**
	 * Sets cursor to wait cursor
	 *
	 */
	public void workCursor(){
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}
	/**
	 * Sets cursor to default cursor
	 *
	 */
	public void defaultCursor(){
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	class D2MenuListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {

			new RandallPanel();

		}
	}

}


