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

package gomule;

import gomule.gui.*;
import java.awt.*;
import javax.swing.*;

public class GoMule
{
	/**
	 * Main Class, runs GoMule
	 * @param args Can set L+F
	 */
	public static void main(String[] args)
	{
		try
		{

			if(System.getProperty("os.name").toLowerCase().indexOf("mac") != -1){
				UIManager.setLookAndFeel("apple.laf.AquaLookAndFeel");

			}else{
				//UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				if ( args != null )
				{
					for ( int i = 0 ; i < args.length ; i++ )
					{
						if ( args[i].equalsIgnoreCase("-system") )
						{
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						}
					}
				}
			}
			UIManager.put ("ToolTip.background", Color.black);
			UIManager.put ("ToolTip.foreground", Color.white);
			ToolTipManager.sharedInstance().setInitialDelay(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		// Randall: generally adviced for swing, doing anything with GUI inside the swing-thread
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				D2FileManager.getInstance();
			}
		});

	}
}

