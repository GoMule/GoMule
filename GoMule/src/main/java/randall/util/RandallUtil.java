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
package randall.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RandallUtil {

    private static final String DEFAULT_SEPARATOR = ",";

//	private static final String ICON_PATH = "/randall/images/";
//	private static HashMap iIcons = new HashMap();

//   public static ImageIcon getIcon( String iconName )
//    throws IOException
//    {
//        if( iIcons.containsKey( iconName))
//        {
//            return (ImageIcon) iIcons.get( iconName);
//        }
//
//        ImageIcon icon = null;
//        icon = loadImageIcon( iconName);
//        iIcons.put( iconName, icon);
//        return icon;
//    }

//    private static ImageIcon loadImageIcon( String iconName)
//    throws IOException
//	{
//	    ImageIcon icon = null;
//	    //Class klasse = getClass();
//	    InputStream inputStream =  InputStream.class.getResourceAsStream(ICON_PATH + iconName);
//	    if (inputStream != null)
//	    {
//	        byte[] buffer = new byte[0];
//	        byte[] tmpbuf = new byte[1024];
//	        while (true)
//	        {
//	            int laenge = inputStream.read(tmpbuf);
//	            if (laenge <= 0)
//	            {
//	                break;
//	            }
//	            byte[] newbuf = new byte[buffer.length + laenge];
//	            System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
//	            System.arraycopy(tmpbuf, 0, newbuf, buffer.length, laenge);
//	            buffer = newbuf;
//	        }
//	        //create image
//	        icon = new ImageIcon(buffer);
//	        inputStream.close();
//	    }
//	    return icon;
//	}

    public static String merge(List<String> strings, String delimiter) {
        return String.join(delimiter, strings);
    }

    //	public static int count(String pString, String pOccurance, boolean pIgnoreCase)
//	{
//	    int lCount = 0;
//
//	    int lIndex = pString.indexOf(pOccurance, 0);
//	    while ( lIndex != -1 )
//	    {
//	        lCount++;
//	        lIndex = pString.indexOf(pOccurance, lIndex+1);
//	    }
//
//	    return lCount;
//	}

    public static List<String> split(String sourceString, String separator, boolean ignoreCase) {
        if (sourceString == null) {
            return new ArrayList<>();
        }
        String nonNullSeparator = separator == null ? DEFAULT_SEPARATOR : separator;

        if (ignoreCase) {
            String lowerCaseString = sourceString.toLowerCase();
            String lowerCaseSeparator = nonNullSeparator.toLowerCase();
            if (lowerCaseString.length() == sourceString.length()
                    && lowerCaseSeparator.length() == nonNullSeparator.length()) {
                return split(sourceString, lowerCaseString, lowerCaseSeparator);
            }
        }
        return split(sourceString, sourceString, nonNullSeparator);
    }

    @NotNull
    private static List<String> split(String stringToSplit, String stringToSearch, String separator) {
        List<String> result = new ArrayList<>();
        for (int startIndex = 0, endIndex;
             startIndex < stringToSearch.length(); startIndex = endIndex + separator.length()) {

            endIndex = stringToSearch.indexOf(separator, startIndex);
            if (endIndex == -1) {
                result.add(stringToSplit.substring(startIndex).trim());
                break;
            }
            result.add(stringToSplit.substring(startIndex, endIndex).trim());
        }
        return result;
    }

    public static String intToString(int pValue, int pDigits) {
        String lValue = Integer.toString(pValue);

        if (lValue.length() > pDigits) {
            return lValue.substring(lValue.length() - pDigits);
        }

        while (lValue.length() < pDigits) {
            lValue = "0" + lValue;
        }

        return lValue;
    }

    public static void checkDir(String pDir) throws Exception {
        File lDir = new File(pDir);

        if (!lDir.exists()) {
            if (!lDir.mkdirs()) {
                throw new Exception("Can not create backup dir: " + pDir);
            }
        }
        if (!lDir.isDirectory()) {
            throw new Exception("File exists with name of backup dir: " + pDir);
        }
        if (!lDir.canRead()) {
            throw new Exception("Can not read backup dir: " + pDir);
        }
        if (!lDir.canWrite()) {
            throw new Exception("Can not write backup dir: " + pDir);
        }
    }
}