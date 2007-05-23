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

import gomule.util.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import randall.d2files.*;
import randall.util.*;

// this class is the top-level window
// it is just a menu to open character-file windows
public class D2FileManager extends JFrame
{
    private static final String CURRENT_VERSION = "R0.16";

    private ArrayList           iOpenWindows;

    private JPanel              iContentPane;
    private JDesktopPane        iDesktopPane;

    private JToolBar            iToolbar;

    private Properties          iProperties;
    private D2Project           iProject;
    private JButton             iBtnProjectSelection;
    private D2ViewProject       iViewProject;
    
    private static D2FileManager iCurrent;
    
    public static D2FileManager getIntance()
    {
        if ( iCurrent == null )
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

        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setTitle("GoMule " + CURRENT_VERSION);

        try
        {
            iDesktopPane.add(D2MouseItem.getInstance(this));
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    closeListener();
                }
            });
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
        D2MouseItem.setProject(iProject);
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

        iToolbar.addSeparator();

        iToolbar.addSeparator();

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

            iProject = new D2Project(lCurrent);
            iBtnProjectSelection.setText(lCurrent);
        }
        catch (Exception pEx)
        {
            displayErrorDialog(pEx);
            iProject = null;
            iProperties = null;
        }
    }

    // called on exit or when this window is closed
    // zip through and make sure all the character
    // windows close properly, because character
    // windows save on close
    public void closeListener()
    {
        closeWindows();
        System.exit(0);
    }

    public void closeWindows()
    {
        D2MouseItem.save();
        if (iProject != null)
        {
            iProject.saveProject();
        }
        while ( iOpenWindows.size() > 0 )
        {
            D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(0);
            if (lItemContainer != null)
            {
                lItemContainer.closeView();
            }
        }
    }

    // called on exit or when this window is closed
    // zip through and make sure all the character
    // windows close properly, because character
    // windows save on close
    public void saveAll()
    {
        D2MouseItem.save();
        if (iProject != null)
        {
            iProject.saveProject();
        }
        for (int i = 0; i < iOpenWindows.size(); i++)
        {
            D2ItemContainer lItemContainer = (D2ItemContainer) iOpenWindows.get(i);
            if (lItemContainer != null)
            {
                lItemContainer.saveView();
            }
        }
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
            iOpenWindows.add(lCharView);
            iDesktopPane.add(lCharView);
            lCharView.toFront();
        }
        iProject.addChar(pCharName);
        iViewProject.refreshTreeModel(true, false);
    }

    public void removeInternalFrame(JInternalFrame pFrame)
    {
        iOpenWindows.remove(pFrame);
        iDesktopPane.remove(pFrame);
        repaint();
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
            D2ViewStash lStashView = handleStash(lStashChooser);
            if (lStashView != null)
            {
                // Force first save
                lStashView.setModified(true);
                lStashView.saveView();
            }
        }
    }

    private D2ViewStash handleStash(JFileChooser pStashChooser)
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
            
            return openStash(lFilename);
        }
        catch (Exception pEx)
        {
            D2FileManager.displayErrorDialog(pEx);
            return null;
        }
    }

    public D2ViewStash openStash(String pStashName)
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
            iOpenWindows.add(lStashView);
            iDesktopPane.add(lStashView);
            lStashView.toFront();
        }
        iProject.addStash(pStashName);
        iViewProject.refreshTreeModel(false, true);
        
        return lStashView;
    }

    public void about_action()
    {
        JOptionPane.showMessageDialog(this, "A java-based Diablo II muling application\n\noriniginally created by Andy Theuninck (Gohanman)\nVersion 0.1a" + "\n\ncurrent release by Randall & Silospen\nVersion "
                + CURRENT_VERSION + "\n\nAnd special thanks to:" + "\n\tHakai_no_Tenshi & Gohanman for helping me out with the file formats"
                + "\n\tSkinhead On The MBTA & nubikon for helping me beta testing", "About", JOptionPane.PLAIN_MESSAGE);
    }
    
    public static void displayErrorDialog(Exception pException)
    {
        displayErrorDialog(iCurrent, pException);
    }

	public static void displayErrorDialog(Window pParent, Exception pException)
	{
	    pException.printStackTrace();
		JDialog lDialog;
		if ( pParent instanceof JFrame )
		{
			lDialog = new JDialog((JFrame) pParent, "Error");
		}
		else
		{
			lDialog = new JDialog((JDialog) pParent, "Error");
		}
		RandallPanel lPanel = new RandallPanel();
		JTextArea lTextArea = new JTextArea();
		JScrollPane lScroll = new JScrollPane(lTextArea);
		
		lScroll.setPreferredSize(new Dimension(640,480));
		lPanel.addToPanel(lScroll,0,0,1,RandallPanel.BOTH);
		
		String lText = "Error\n\n" + pException.getMessage()+"\n";

		StackTraceElement trace[] = pException.getStackTrace();
        for (int i=0; i < trace.length ; i++)
        {
        	lText += "\tat " + trace[i] + "\n";
        }
		
		lTextArea.setText(lText);
		lTextArea.setEditable(false);
		
		lDialog.setContentPane(lPanel);
		lDialog.pack();
		lDialog.setLocation(200, 100);
		lDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		lDialog.show();
	}
	
    
}