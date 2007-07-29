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
import gomule.util.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import randall.d2files.*;
import randall.util.*;

/**
 * this class is the top-level administrative window. 
 * It contains all internal frames
 * it contains all open files
 */ 
public class D2FileManager extends JFrame
{
    private static final String  CURRENT_VERSION = "R0.18";

    private HashMap				 iItemLists = new HashMap();
    
    private ArrayList            iOpenWindows;

    private JPanel               iContentPane;
    private JDesktopPane         iDesktopPane;

    private JToolBar             iToolbar;

    private Properties           iProperties;
    private D2Project            iProject;
    private JButton              iBtnProjectSelection;
    private D2ViewProject        iViewProject;

    private static D2FileManager iCurrent;

    private D2ViewClipboard      iClipboard;
    private D2ViewStash          iViewAll;
    
    private boolean				 iIgnoreCheckAll = false;

    public static D2FileManager getIntance()
    {
        if (iCurrent == null)
        {
            iCurrent = new D2FileManager();
        }
        return iCurrent;
    }

    private D2FileManager()
    {
        super();

        D2TxtFile.readAllFiles("d2111");
        D2TblFile.readAllFiles("d2111");

        createToolbar();

        iOpenWindows = new ArrayList();

        iContentPane = new JPanel();
        iDesktopPane = new JDesktopPane();

        iContentPane.setLayout(new BorderLayout());
        iContentPane.add(iToolbar, BorderLayout.NORTH);

        iViewProject = new D2ViewProject(this);
        iViewProject.setPreferredSize(new Dimension(100, 100));
        iViewProject.setProject(iProject);
        iViewProject.refreshTreeModel(true, true);
        JSplitPane lSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, iViewProject, iDesktopPane);
        lSplit.setDividerLocation(200);
        iContentPane.add(lSplit, BorderLayout.CENTER);

        setContentPane(iContentPane);

        Dimension lSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(lSize.width, lSize.height - 50 );
        setTitle("GoMule " + CURRENT_VERSION);

        try
        {
            iClipboard = D2ViewClipboard.getInstance(this);
            iDesktopPane.add(iClipboard);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    closeListener();
                }
//                public void windowDeactivated(WindowEvent e) 
//                {
//                    saveAll();
//                }
                public void windowActivated(WindowEvent e) 
                {
                    checkAll(false);
                }
            });
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        catch (Exception pEx)
        {
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
        //        pack();

        //        compareFile("save\\daniel1.d2s", "save\\daniel2.d2s");

        setVisible(true);
    }

    public D2Project getProject()
    {
        return iProject;
    }

    public void setProject(D2Project pProject) throws Exception
    {
        iProject = pProject;
        iClipboard.setProject(iProject);
        iBtnProjectSelection.setText(iProject.getProjectName());
        iViewProject.setProject(pProject);
    }

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
                openChar();
            }
        });
        iToolbar.add(lOpenD2S);

        iToolbar.addSeparator();

        iToolbar.add(new JLabel("Stash"));

        JButton lNewD2X = new JButton(D2ImageCache.getIcon("new.gif"));
        lNewD2X.setToolTipText("New ATMA Stash");
        lNewD2X.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                newStash();
            }
        });
        iToolbar.add(lNewD2X);

        JButton lOpenD2X = new JButton(D2ImageCache.getIcon("open.gif"));
        lOpenD2X.setToolTipText("Open ATMA Stash");
        lOpenD2X.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                openStash();
            }
        });
        iToolbar.add(lOpenD2X);

        iToolbar.addSeparator();

        iToolbar.add(new JLabel("          "));

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

//        iToolbar.addSeparator();

        iToolbar.add(new JLabel("Projects"));

        iBtnProjectSelection = new JButton("Default");
        iBtnProjectSelection.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                D2ProjectSettingsDialog lDialog = new D2ProjectSettingsDialog(D2FileManager.this);
                lDialog.setVisible(true);
            }
        });
        iToolbar.add(iBtnProjectSelection);
        checkProjects();

        iToolbar.addSeparator();

        JButton lHelp = new JButton("About");
        lHelp.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                about_action();
            }
        });
        iToolbar.add(lHelp);

    }

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
            iBtnProjectSelection.setText(lCurrent);
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
    
//    public D2ItemListAll getAllItemList()
//    {
//        if ( iItemLists.containsKey("all") )
//        {
//            return (D2ItemListAll) iItemLists.get("all");
//        }
//        return null;
//    }
    
    public void closeFileName(String pFileName)
    {
        saveAll();
        for ( int i = 0 ; i < iOpenWindows.size() ; i++ )
        {
            D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(i);
            
            if ( lItemContainer.getFileName().equalsIgnoreCase(pFileName) )
            {
                lItemContainer.closeView();
            }
        }
    }
    
    public void fullDump(String pFileName)
    {
        D2ItemList lList = null;
        String lFileName = null;
        
        if ( pFileName.equalsIgnoreCase("all") )
        {
            if ( iViewAll != null )
            {
	            lFileName = "." + File.separator + "all.txt";
	            lList = iViewAll.getItemLists();
            }
        }
        else
        {
	        lList = (D2ItemList) iItemLists.get(pFileName);
	        lFileName = pFileName+".txt";
        }
        if ( lList != null && lFileName != null )
        {
            try
            {
                File lFile = new File(lFileName);
                System.err.println("File: " + lFile.getCanonicalPath() );
                
	            PrintWriter lWriter = new PrintWriter(new FileWriter( lFile.getCanonicalPath() ));
	            
	            lList.fullDump(lWriter);
	            
	            lWriter.flush();
	            lWriter.close();
            }
            catch ( Exception pEx )
            {
                pEx.printStackTrace();
            }
        }
    }

    public void closeWindows()
    {
        saveAll();
        while (iOpenWindows.size() > 0)
        {
            D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(0);
            if (lItemContainer != null)
            {
                lItemContainer.closeView();
            }
        }
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
    
    public void cancelAll()
    {
        checkAll(true);
    }

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
	//        return true;
	//        boolean lSomethingChanged = true;
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
	                displayTextDialog("Info", "Changes on file system detected (also changed in GoMule), reloading File System changed files or all files modified in GoMule" );
	            }
	            else
	            {
	                displayTextDialog("Info", "Changes on file system detected (not changed in GoMule), reloading File System changed files" );
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
	
		        for ( int i = 0 ; i < iOpenWindows.size() ; i++ )
		        {
		            D2ItemContainer lContainer = (D2ItemContainer) iOpenWindows.get(i);
		            D2ItemList lList = lContainer.getItemLists();
		            if ( lList != iViewAll )
		            {
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
    
    private void handleLoadError(String pFileName, Exception pEx)
    {
        // close this view & all view
        for ( int i = 0 ; i < iOpenWindows.size() ; i++ )
        {
            D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(i);
            
            if ( lItemContainer.getFileName().equalsIgnoreCase(pFileName) || lItemContainer.getFileName().toLowerCase().equals("all") )
            {
                lItemContainer.closeView();
            }
        }
        displayErrorDialog( pEx );
    }

    private JFileChooser getCharDialog()
    {
        return iProject.getCharDialog();
    }

    private JFileChooser getStashDialog()
    {
        return iProject.getStashDialog();
    }

    // file->open callback
    // throw up a dialog for picking d2s files
    // then open that character and add it to the
    // vector of character windows
    public void openChar()
    {
        JFileChooser lCharChooser = getCharDialog();
        if (lCharChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            java.io.File lFile = lCharChooser.getSelectedFile();
            try
            {
                String lFilename = lFile.getAbsolutePath();
                openChar(lFilename);
            }
            catch (Exception pEx)
            {
                D2FileManager.displayErrorDialog(pEx);
            }
        }
    }

    public void openChar(String pCharName)
    {
        D2ItemContainer lExisting = null;
        for (int i = 0; i < iOpenWindows.size(); i++)
        {
            D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(i);
            if (lItemContainer.getFileName().equals(pCharName))
            {
                lExisting = lItemContainer;
            }
        }

        if (lExisting != null)
        {
            ((JInternalFrame) lExisting).toFront();
        }
        else
        {
            D2ViewChar lCharView = new D2ViewChar(D2FileManager.this, pCharName);
            lCharView.setLocation(100, 100);
            addToOpenWindows(lCharView);
            lCharView.toFront();
        }
        iProject.addChar(pCharName);
    }
    
    public D2ViewProject getViewProject()
    {
        return iViewProject;
    }
    
    private void addToOpenWindows(D2ItemContainer pContainer)
    {
        iOpenWindows.add( pContainer );
        iDesktopPane.add( (JInternalFrame) pContainer );
        iViewProject.notifyFileOpened( pContainer.getFileName() );
        
        if ( pContainer.getFileName().equalsIgnoreCase("all") )
        {
            iViewAll = (D2ViewStash) pContainer;
        }
    }

    public void removeFromOpenWindows(D2ItemContainer pContainer)
    {
        iOpenWindows.remove( pContainer );
        iDesktopPane.remove( (JInternalFrame) pContainer );
        iViewProject.notifyFileClosed( pContainer.getFileName() );
        repaint();

        if ( pContainer.getFileName().equalsIgnoreCase("all") )
        {
            iViewAll = null;
        }
    }

    public void openStash()
    {
        JFileChooser lStashChooser = getStashDialog();
        if (lStashChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            handleStash(lStashChooser);
        }
    }

    public void newStash()
    {
        JFileChooser lStashChooser = getStashDialog();
        if (lStashChooser.showDialog(this, "New Stash") == JFileChooser.APPROVE_OPTION)
        {
            String lFileName = handleStash(lStashChooser);
            if (lFileName != null)
            {
                D2ItemList lList = (D2ItemList) iItemLists.get(lFileName);
                
                lList.save(iProject);
            }
        }
    }

    private String handleStash(JFileChooser pStashChooser)
    {
        java.io.File lFile = pStashChooser.getSelectedFile();
        try
        {
            String lFilename = lFile.getAbsolutePath();
            if (!lFilename.endsWith(".d2x"))
            {
                // force stash name to end with .d2x
                lFilename += ".d2x";
            }

            openStash(lFilename);
            return lFilename;
        }
        catch (Exception pEx)
        {
            D2FileManager.displayErrorDialog(pEx);
            return null;
        }
    }

    public void openStash(String pStashName)
    {
        D2ItemContainer lExisting = null;
        for (int i = 0; i < iOpenWindows.size(); i++)
        {
            D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(i);
            if (lItemContainer.getFileName().equals(pStashName))
            {
                lExisting = lItemContainer;
            }
        }

        D2ViewStash lStashView;
        if (lExisting != null)
        {
            lStashView = ((D2ViewStash) lExisting);
            lStashView.toFront();
        }
        else
        {
            lStashView = new D2ViewStash(D2FileManager.this, pStashName);
            lStashView.setLocation(100, 100);
            addToOpenWindows(lStashView);
            lStashView.toFront();
        }
        iProject.addStash(pStashName);
//        iViewProject.refreshTreeModel(false, true);

//        return lStashView;
    }

    public void about_action()
    {
        JOptionPane.showMessageDialog(this, "A java-based Diablo II muling application\n\noriniginally created by Andy Theuninck (Gohanman)\nVersion 0.1a"
                + "\n\ncurrent release by Randall & Silospen\nVersion " + CURRENT_VERSION + "\n\nAnd special thanks to:" + "\n\tHakai_no_Tenshi & Gohanman for helping me out with the file formats"
                + "\n\tSkinhead On The MBTA & nubikon for helping me beta testing", "About", JOptionPane.PLAIN_MESSAGE);
    }
    
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
//            iViewProject.notifyItemListOpened("all");
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
    
    public D2ItemList getItemList(String pFileName)
    {
        return (D2ItemList) iItemLists.get(pFileName);
    }
    
    public void removeItemList(String pFileName, D2ItemListListener pListener)
    {
        D2ItemList lList = getItemList(pFileName);
        if ( pListener != null )
        {
            lList.removeD2ItemListListener(pListener);
        }
        if ( !lList.hasD2ItemListListener() )
        {
            System.err.println("Remove file: " + pFileName );
//            saveAll();
            iItemLists.remove(pFileName);
            iViewProject.notifyItemListClosed(pFileName);
        }
    }
    
    public static void displayErrorDialog(Exception pException)
    {
        displayErrorDialog(iCurrent, pException);
    }

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

    public static void displayTextDialog(String pTitle, String pText)
    {
        displayTextDialog(iCurrent, pTitle, pText);
    }
    
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

        lScroll.setPreferredSize(new Dimension(640, 480));
        lPanel.addToPanel(lScroll, 0, 0, 1, RandallPanel.BOTH);

        lTextArea.setText(pText);
        if ( pText.length() > 1 )
        {
            lTextArea.setCaretPosition(0);
        }
        lTextArea.setEditable(false);

        lDialog.setContentPane(lPanel);
        lDialog.pack();
        lDialog.setLocation(200, 100);
        lDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        lDialog.show();
    }

}