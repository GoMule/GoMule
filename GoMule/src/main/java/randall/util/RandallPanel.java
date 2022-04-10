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

    public enum Constraint {
        NONE, HORIZONTAL, VERTICAL, BOTH
    }

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
        addToPanel(new JPanel(), 0, 250, 1, Constraint.VERTICAL);
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

    public void addToPanel(JComponent component, int x, int y, int sizeX, Constraint constraint) {
        addToPanel(component, x, y, sizeX, 1, -1.0, -1.0, constraint, -1);
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, double weightX, Constraint constraint) {
        addToPanel(component, x, y, sizeX, 1, weightX, -1.0, constraint, -1);
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY, Constraint constraint) {
        addToPanel(component, x, y, sizeX, sizeY, -1.0, -1.0, constraint, -1);
    }

    public void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY,
                           Constraint constraint, int constraintAnchor) {
        addToPanel(component, x, y, sizeX, sizeY, -1.0, -1.0, constraint, constraintAnchor);
    }

    private void addToPanel(JComponent component, int x, int y, int sizeX, int sizeY, double weightX, double weightY,
                            Constraint constraint, int constraintAnchor) {

        Insets insets = new Insets(
                calcMarginTop(y),
                calcMarginLeft(x, component),
                calcMarginBottom(y, sizeY),
                calcMarginRight(x, sizeX));

        GridBagConstraints constraints = new GridBagConstraints(
                x, y, sizeX, sizeY,
                calcWeightX(weightX, constraint),
                calcWeightY(weightY, constraint),
                calcGridbagAnchor(constraintAnchor),
                calcGridbagConstraint(constraint),
                insets, 0, 0);

        this.add(component, constraints);
        this.yPos = y + 1;
    }

    private double calcWeightX(double xWeight, Constraint constraint) {
        if (xWeight >= 0.0) {
            return xWeight;
        }
        if (Constraint.HORIZONTAL == constraint || Constraint.BOTH == constraint) {
            return 1.0;
        }
        return 0.0;
    }

    private double calcWeightY(double weightY, Constraint constraint) {
        if (weightY >= 0.0) {
            return weightY;
        }
        if (Constraint.VERTICAL == constraint || Constraint.BOTH == constraint) {
            return 1.0;
        }
        return 0.0;
    }

    private int calcGridbagAnchor(int constraintAnchor) {
        return constraintAnchor >= 0 ? constraintAnchor : GridBagConstraints.NORTHWEST;
    }

    private int calcGridbagConstraint(Constraint constraint) {
        if (Constraint.HORIZONTAL == constraint) {
            return GridBagConstraints.HORIZONTAL;
        } else if (Constraint.VERTICAL == constraint) {
            return GridBagConstraints.VERTICAL;
        } else if (Constraint.BOTH == constraint) {
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

    private int calcMarginRight(int x, int sizeX) {
        if (subPanel || x + sizeX < marginXSize) {
            return 0;
        }
        return margin;
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