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

import gomule.util.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import randall.flavie.*;

public class D2ViewProject extends JPanel
{
    private D2FileManager          iFileManager;
    private D2Project              iProject;

    private JTree                  iTree;
    private DefaultTreeModel       iTreeModel;
    private DefaultMutableTreeNode iChars;
    private DefaultMutableTreeNode iStashes;

    public D2ViewProject(D2FileManager pFileManager)
    {
        iFileManager = pFileManager;
        setLayout(new BorderLayout());
        iTreeModel = refreshTree();
        iTree = new JTree(iTreeModel)
        {
            public void scrollRectToVisible(Rectangle aRect)
            {
                // disable scrolling
            }
        };
        iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        JScrollPane lScroll = new JScrollPane(iTree);
        add(lScroll, BorderLayout.CENTER);
        
        JButton lFlavie = new JButton("Flavie");
        lFlavie.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                try
                {
                    ArrayList lFileNames = new ArrayList();
                    
                    ArrayList lCharList = iProject.getCharList();
                    if ( lCharList != null )
                    {
                        lFileNames.addAll( lCharList );
                    }
                    ArrayList lStashList = iProject.getStashList();
                    if ( lStashList != null )
                    {
                        lFileNames.addAll( lStashList );
                    }
                    
                    new Flavie(
                        iProject.getReportName(), iProject.getReportTitle(), 
                        iProject.getDataName(), iProject.getStyleName(),
                        lFileNames,
                        iProject.isCountAll(), iProject.isCountEthereal(),
                        iProject.isCountStash(), iProject.isCountChar()
                        );
                }
                catch (Exception pEx)
                {
                    iFileManager.displayErrorDialog(pEx);
                }
            }
        });
        add(lFlavie, BorderLayout.SOUTH);

        iTree.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 )
                {
	                TreePath lPath = iTree.getPathForLocation(e.getX(), e.getY());
	                Object lPathObjects[] = lPath.getPath();
	                Object lLast = lPathObjects[lPathObjects.length - 1];
	                if (lLast instanceof CharTreeNode)
	                {
	                    String lFilename = ((CharTreeNode) lLast).getFilename();
	                    if ( lFilename.toLowerCase().endsWith(".d2s") )
	                    {
	                        iFileManager.openChar(lFilename);
	                    }
	                    else if ( lFilename.toLowerCase().endsWith(".d2x") )
	                    {
	                        iFileManager.openStash(lFilename);
	                    }
	                }
                }
            }
        });
        iTree.addKeyListener(new KeyAdapter()
        {
            public void keyReleased(KeyEvent e) 
            {
                if ( e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE )
                {
	                ArrayList lDelete = new ArrayList();
	                int lSelected[] = iTree.getSelectionRows();
	                for ( int i = 0 ; i < lSelected.length ; i++ )
	                {
	                    TreePath lPath = iTree.getPathForRow(lSelected[i]);
	                    Object lPathObjects[] = lPath.getPath();
		                Object lLast = lPathObjects[lPathObjects.length - 1];
		                if (lLast instanceof CharTreeNode)
		                {
		                    lDelete.add( ((CharTreeNode) lLast).getFilename() );
		                }
	                }
	                for ( int i = 0 ; i < lDelete.size() ; i++ )
	                {
	                    iProject.deleteCharStash((String) lDelete.get(i));
	                }
	                refreshTreeModel(false, false);
                }
            }
        });
    }
    
    public void setProject(D2Project pProject)
    {
        iProject = pProject;
        refreshTreeModel(true, true);
    }

    public void refreshTreeModel(boolean pExpandChar, boolean pExpandStash)
    {
        boolean lChar = false;
        boolean lStash = false;
        for (int i = 0; i < iTree.getRowCount(); i++)
        {
            TreePath lPath = iTree.getPathForRow(i);
            Object lPathObjects[] = lPath.getPath();
            if (lPathObjects.length > 1 && lPathObjects[lPathObjects.length - 1] == iChars)
            {
                lChar = iTree.isExpanded(i);
            }
            if (lPathObjects.length > 1 && lPathObjects[lPathObjects.length - 1] == iStashes)
            {
                lStash = iTree.isExpanded(i);
            }
        }
        iTreeModel = refreshTree();
        iTree.setModel(iTreeModel);
        for (int i = 0; i < iTree.getRowCount(); i++)
        {
            TreePath lPath = iTree.getPathForRow(i);
            Object lPathObjects[] = lPath.getPath();
            if (lPathObjects.length > 1 && lPathObjects[lPathObjects.length - 1] == iChars)
            {
                if (lChar || pExpandChar)
                {
                    iTree.expandRow(i);
                }
            }
            if (lPathObjects.length > 1 && lPathObjects[lPathObjects.length - 1] == iStashes)
            {
                if (lStash || pExpandStash)
                {
                    iTree.expandRow(i);
                }
            }
        }
    }

    private DefaultTreeModel refreshTree()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("D2");

        DefaultMutableTreeNode parent;

        iChars = new DefaultMutableTreeNode("characters");
        root.add(iChars);
        if ( iProject != null )
        {
	        ArrayList lCharList = iProject.getCharList();
	        for (int i = 0; i < lCharList.size(); i++)
	        {
	            iChars.add(new CharTreeNode((String) lCharList.get(i)));
	        }
        }

        iStashes = new DefaultMutableTreeNode("stashes");
        root.add(iStashes);
        if ( iProject != null )
        {
            ArrayList lStashList = iProject.getStashList();
	        for (int i = 0; i < lStashList.size(); i++)
	        {
	            iStashes.add(new CharTreeNode((String) lStashList.get(i)));
	        }
        }

        return new DefaultTreeModel(root);
    }

    class CharTreeNode extends DefaultMutableTreeNode
    {
        private String iFileName;

        public CharTreeNode(String pFileName)
        {
            super(getCharStr(pFileName));
            iFileName = pFileName;
        }
        
        private String getFilename()
        {
            return iFileName;
        }

    } // end class CharTreeNode

    private static String getCharStr(String pFileName)
    {
        File lFile = new File(pFileName);
        return lFile.getName() + " (" + pFileName + ")";
    }

}