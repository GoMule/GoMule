/*******************************************************************************
 *
 * Copyright 2007 Randall (oriniginal the Flavie Reader)
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
package randall.d2files;

import gomule.gui.D2FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Marco
 */
public class D2FileReader {

    protected byte[] buffer;

    //    private String fileName;
    private int counterPosition = 0;
    private int counterBit = 0;

    public D2FileReader(String pathToFile) {
        File file = new File(pathToFile);
        if (file.exists() && file.isFile() && file.canRead()) {
            try (InputStream fileInputStream = new FileInputStream(file)) {
                buffer = new byte[(int) file.length()];
                fileInputStream.read(buffer);
            } catch (Exception pEx) {
                buffer = null;
                D2FileManager.displayErrorDialog(pEx);
            }
        }
    }

    public D2FileReader(byte[] buffer) {
        this.buffer = buffer;
    }

    public void increaseCounter(int pNrBits) {
        for (int i = 0; i < pNrBits; i++) {
            counterBit++;
            if (counterBit > 7) {
                counterBit = 0;
                counterPosition++;
            }
        }
    }

    public int getCounterPos() {
        return counterPosition;
    }

    public int getCounterBit() {
        return counterBit;
    }

    public void setCounter(int counterPosition, int counterBit) {
        this.counterPosition = counterPosition;
        this.counterBit = counterBit;
    }

    private boolean getCounterBoolean() {
        int lNr = 1;
        if (counterBit > 0) {
            lNr = lNr << counterBit;
        }
        boolean lBoolean = ((buffer[counterPosition] & lNr) > 0);
        increaseCounter(1);
        return lBoolean;
    }

    public String getCounterString() {
        StringBuilder builder = new StringBuilder();
        int lInt = 0;
        try {
            lInt = getCounterInt(8);
        } catch (Exception pEx) {
            return null;
        }
        while (lInt != 0) {
            try {
                builder.append((char) lInt);
                lInt = getCounterInt(8);
            } catch (Exception pEx) {
                lInt = 0;
            }
        }
        return builder.toString();
    }

    public String getCounterString(int pCharNr) {
        char[] lBuffer = new char[pCharNr];
        for (int lCharNr = 0; lCharNr < pCharNr; lCharNr++) {
            lBuffer[lCharNr] = 0;
            for (int lBitNr = 0; lBitNr < 8; lBitNr++) {
                // first read
                boolean lBit = getCounterBoolean();
                if (lBit) {
                    // now set
                    int lNr = 1;
                    if (lBitNr > 0) {
                        lNr = 1 << lBitNr;
                    }
                    lBuffer[lCharNr] = (char) (lBuffer[lCharNr] | lNr);
                }
            }
        }
        return new String(lBuffer);
    }

    public long getCounterLong(int pBitNr) {
        long lInt = 0;
        boolean[] lIntCount = new boolean[pBitNr];

        for (int i = 0; i < pBitNr; i++) {
            lIntCount[i] = getCounterBoolean();
        }
        for (int i = pBitNr - 1; i >= 0; i--) {
            lInt = lInt << 1;
            if (lIntCount[i]) {
                lInt++;
            }
        }
        return lInt;
    }

    public int getCounterInt(int pBitNr) {
        int lInt = 0;
        boolean[] lIntCount = new boolean[pBitNr];

        for (int i = 0; i < pBitNr; i++) {
            lIntCount[i] = getCounterBoolean();
        }
        for (int i = pBitNr - 1; i >= 0; i--) {
            lInt = lInt << 1;
            if (lIntCount[i]) {
                lInt++;
            }
        }
        return lInt;
    }
}