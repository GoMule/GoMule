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

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RandallPanel extends JPanel {

    public static final Integer NONE = 100;
    public static final Integer HORIZONTAL = 101;
    public static final Integer VERTICAL = 102;
    public static final Integer BOTH = 103;

    public static final int ANCHOR_NORTHWEST = GridBagConstraints.NORTHWEST;
    public static final int ANCHOR_NORTHEAST = GridBagConstraints.NORTHEAST;

    private static final long serialVersionUID = -6556940562813366360L;

    private int marginXSize = 2;
    private int marginYSize = -1;
    private int yPos = 0;

    private int margin = 1;
//	private boolean iMarginAllSides = false;

    // sub panel -> all elements on X or Y of 0 does not get a top or left margin
    private boolean subPanel = false;

    private TitledBorder titledBorder = null;

    /**
     * Creates a new RandallPanel with a double buffer and a flow layout.
     */
    public RandallPanel() {
        init();
    }

    public RandallPanel(boolean subPanel) {
        this();
        if (subPanel) {
            setSubPanel();
        }
    }

    public void setBorder(String title) {
        titledBorder = new TitledBorder(new EtchedBorder(), title);
        setBorder(titledBorder);
    }

    private void init() {
        setLayout(new GridBagLayout());
    }

    public void finishDefaultPanel() {
        yPos = yPos + 100;
        addToPanel(new JPanel(), 0, 250, 1, VERTICAL);
    }

    /**
     * Set the right column nr for the panel
     */
    public void setMarginRightColumnNr(int columnNr) {
        marginXSize = columnNr;
    }

    public void setMarginBottomRowNrColumnNr(int rowNr) {
        marginYSize = rowNr;
    }

    public void setMarginRightBottom(int columnNr, int rowNr) {
        marginXSize = columnNr;
        marginYSize = rowNr;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

//	public void setMarginAllSides(boolean marginAllSides) {
//		this.marginAllSides = marginAllSides;
//	}

    /**
     * Set the right column nr for the sub panel
     * (does not add left/upper margin when left/up coordinate is 0)
     */
    public void setSubPanel() {
        subPanel = true;
    }

    public int getPanelColumnNr() {
        return marginXSize;
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, Integer constraint) {
        addToPanel(component, x, y, sizeX, 1, constraint, -1.0, -1.0, -1);
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, double weightX, Integer constraint) {
        addToPanel(component, x, y, sizeX, 1, constraint, weightX, -1.0, -1);
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY, Integer constraint) {
        addToPanel(component, x, y, sizeX, sizeY, constraint, -1.0, -1.0, -1);
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY,
                           Integer constraint, int constraintAnchor) {
        addToPanel(component, x, y, sizeX, sizeY, constraint, -1.0, -1.0, constraintAnchor);
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY, Integer constraint,
                           double weightX, double weightY, int constraintAnchor) {
        int marginRight = margin;

        if (x + sizeX < marginXSize) {
            marginRight = 0;
        }
        if (subPanel) {
            marginRight = 0;
        }

        this.add(component, new GridBagConstraints(x, y, sizeX, sizeY, calcWeightX(weightX, constraint),
                calcWeightY(weightY, constraint), calcGridbagAnchor(constraintAnchor),
                calcGridbagConstraint(constraint),
                new Insets(calcMarginTop(y), calcMarginLeft(x, component),
                        calcMarginBottom(y, sizeY), marginRight), 0, 0));

        this.yPos = y + 1;
    }

    private double calcWeightX(double xWeight, Integer constraint) {
        if (xWeight >= 0.0) {
            return xWeight;
        }
        if (HORIZONTAL.equals(constraint) || BOTH.equals(constraint)) {
            return 1.0;
        }
        return 0.0;
    }

    private double calcWeightY(double weightY, Integer constraint) {
        if (weightY >= 0.0) {
            return weightY;
        }
        if (VERTICAL.equals(constraint) || BOTH.equals(constraint)) {
            return 1.0;
        }
        return 0.0;
    }

    private int calcGridbagAnchor(int constraintAnchor) {
        return constraintAnchor >= 0 ? constraintAnchor : ANCHOR_NORTHWEST;
    }

    private int calcGridbagConstraint(Integer constraint) {
        if (HORIZONTAL.equals(constraint)) {
            return GridBagConstraints.HORIZONTAL;
        } else if (VERTICAL.equals(constraint)) {
            return GridBagConstraints.VERTICAL;
        } else if (BOTH.equals(constraint)) {
            return GridBagConstraints.BOTH;
        }
        return GridBagConstraints.NONE;
    }

    private int calcMarginTop(int y) {
        if (subPanel && y == 0) {
            return 0;
        }
        return margin;
    }

    private int calcMarginLeft(int x, JComponent component) {
        if (subPanel && x == 0) {
            return 0;
        }
//        if (component instanceof JCheckBox) {
//        	return margin - 4;
//        }
        return margin;
    }

    private int calcMarginBottom(int y, int sizeY) {
        if (marginYSize != -1 && y + sizeY == marginYSize) {
            return margin;
        }
//        if (marginAllSides) {
//			return margin;
//        }
        return 0;
    }

    /**
     * Removes the given Component from the Panel if not null
     *
     * @param pComponent The Component to be removed
     */
    public void removeFromPanel(JComponent pComponent) {
        if (pComponent != null) {
            this.remove(pComponent);
        }
    }

    public String getTitle() {
        return titledBorder.getTitle();
    }

    public void setTitle(String pTitle) {
        titledBorder.setTitle(pTitle);
    }
}