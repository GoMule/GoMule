/*
 * Created on 23-mei-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gomule.util;

import gomule.gui.D2FileManager;
import randall.util.RandallUtil;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Marco
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class D2Backup {
    public static void backup(D2Project pProject, String pFileName, D2BitReader pContent) {
        try {
            int lBackup = pProject.getBackup();
            if (lBackup == D2Project.BACKUP_NONE) {
                return;
            }

            File lFile = new File(pFileName);

            String lFileName = lFile.getName();
            String lPathName = lFile.getParent();

            GregorianCalendar lCalendar = new GregorianCalendar();
            String lExtra1;

            if (lBackup == D2Project.BACKUP_DAY) {
                lExtra1 = "D"
                        + intToString(lCalendar.get(Calendar.YEAR), 4)
                        + "." + intToString(lCalendar.get(Calendar.MONTH) + 1, 2)
                        + "." + intToString(lCalendar.get(Calendar.DAY_OF_MONTH), 2);
            } else if (lBackup == D2Project.BACKUP_MONTH) {
                lExtra1 = "M"
                        + intToString(lCalendar.get(Calendar.YEAR), 4)
                        + intToString(lCalendar.get(Calendar.MONTH) + 1, 2);
            } else {
                GregorianCalendar lWeek = new GregorianCalendar();

                while (lWeek.get(Calendar.DAY_OF_WEEK) != lWeek.getFirstDayOfWeek()) {
                    lWeek.add(Calendar.DAY_OF_MONTH, -1);
                }
                lExtra1 = "W"
                        + intToString(lWeek.get(Calendar.YEAR), 4)
                        + "." + intToString(lWeek.get(Calendar.MONTH) + 1, 2)
                        + "." + intToString(lWeek.get(Calendar.DAY_OF_MONTH), 2);
            }

            String lExtra2 =
                    intToString(lCalendar.get(Calendar.YEAR), 4)
                            + "." + intToString(lCalendar.get(Calendar.MONTH) + 1, 2)
                            + "." + intToString(lCalendar.get(Calendar.DAY_OF_MONTH), 2)
                            + "-"
                            + intToString(lCalendar.get(Calendar.HOUR_OF_DAY), 2)
                            + "." + intToString(lCalendar.get(Calendar.MINUTE), 2)
                            + "." + intToString(lCalendar.get(Calendar.SECOND), 2);

            String lBackupDir = lPathName + File.separator + "GoMule.backup";
            String lBackupSubDir = lBackupDir + File.separator + lExtra1;

            String lNewFileName = lFileName.substring(0, lFileName.length() - 4) + "." + lExtra2 + lFileName.substring(lFileName.length() - 4) + ".org";

            String lBackupName = lBackupSubDir + File.separator + lNewFileName;

            RandallUtil.checkDirectory(lBackupSubDir);

            pContent.save(lBackupName);
        } catch (Exception pEx) {
            pEx.printStackTrace();
            D2FileManager.displayErrorDialog(pEx);
        }

//        if ( pContent.isNewFile() )
//        {
//            return;
//        }
//        int backup_max = -1;
//        for (int i = 0; i <= 9; i++)
//        {
//            if (new java.io.File(pFileName + "." + i).exists())
//                backup_max = i;
//            else
//                break;
//        }
//
//        // shift the backups down
//        // (backup 9 is overwritten, 0 comes free)
//        for (int i = backup_max; i >= 0; i--)
//        {
//            if (i < 9)
//            {
//                D2BitReader src = new D2BitReader(pFileName + "." + i);
//                src.save(pFileName + "." + (i + 1));
//            }
//        }
//        
//        // save the file to backup 0
//        pContent.save(pFileName + ".0");

    }

    protected static String intToString(int number, int maxLength) {
        String string = Integer.toString(number);
        int length = string.length();
        return length > maxLength
                ? string.substring(length - maxLength)
                : String.format("%0" + maxLength + "d", number);
    }
}
