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
//	public int check_bit(int num, int bit_num)
//	{
//	int result = num << (bit_num - 1) >>> 31;
//	return result;
//	}

//	public void print_flags(int flags)
//	{
//	System.out
//	.println("FLAGS: 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32");
//	System.out.print("FLAGS: ");
//	for (int i = 0; i < 32; i++)
//	{
//	int digit = check_bit(flags, i + 1);
//	if (digit == 1)
//	System.out.print("1  ");
//	else
//	System.out.print("   ");
//	}
//	System.out.println();
//	}

//	public void print_d2s(String filename)
//	{
//	try
//	{
//	FileInputStream in = new FileInputStream(filename);
//	byte[] bytes = new byte[16];
//	while (in.read(bytes) != -1)
//	{
//	for (int i = 0; i < 16; i++)
//	{
//	int unsigned = 0x000000ff & bytes[i];
//	if (unsigned > 20 && unsigned < 125)
//	System.out.print((char) unsigned + "\t");
//	else
//	System.out.print(unsigned + "\t");
//	}
//	System.out.println();
//	}
//	} catch (Exception ex)
//	{
//	System.out.println("I/O error");
//	}
//	}

	public static void main(String[] args)
	{
		try
		{

			if(System.getProperty("os.name").toLowerCase().contains("mac")){
				UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
			}else{
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
				UIManager.put ("ToolTip.background", Color.black);
				UIManager.put ("ToolTip.foreground", Color.white);
				ToolTipManager.sharedInstance().setInitialDelay(0);

			}
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
				D2FileManager.getIntance();
			}
		});

	}
}

