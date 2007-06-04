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
package gomule.item;

import gomule.d2s.*;

import java.util.*;

import randall.d2files.*;

/**
 * @author Marco
 */
public class D2ItemProperty
{
    private static final Integer FIRST       = new Integer(0);
    private static final Integer SECOND      = new Integer(1);
    private static final Integer THIRD       = new Integer(2);
    private static final Integer FOURTH      = new Integer(3);
    private static final Integer FIFTH      = new Integer(4);

    private static final Integer COUNTER[]   = new Integer[] { FIRST, SECOND, THIRD, FOURTH , FIFTH};
    private int                  iCounter;

    private HashMap              iProperties = new HashMap();
    private boolean              iNoValue;
    private String               iValue;
    private int                  iProp;
    private long                 iCharLvl;
    private String               iItemName;
    private int					 iBitSet;
    private D2TxtFileItemProperties iItemStatCost;
    

    public D2ItemProperty(int pProp, long pCharLvl, String pItemName)
    {
        iProp = pProp;
        iCharLvl = pCharLvl;
        iItemName = pItemName;
    }

    public void setCharLvl(long pCharLvl)
    {
        iCharLvl = pCharLvl;
        iValue = null;
    }

    public int[] getPropNrs()
    {
        int lRead[];
        if (iProp == 17)
        {
            lRead = new int[] { 17, 18 };
        }
        else if (iProp == 48)
        {
            lRead = new int[] { 48, 49 };
        }
        else if (iProp == 50)
        {
            lRead = new int[] { 50, 51 };
        }
        else if (iProp == 52)
        {
            lRead = new int[] { 52, 53 };
        }
        else if (iProp == 54)
        {
            lRead = new int[] { 54, 55, 56 };
        }
        else if (iProp == 57)
        {
            lRead = new int[] { 57, 58, 59 };
        }
        else
        {
            lRead = new int[] { iProp };
        }

        return lRead;
    }

    public void set(int pProps, D2TxtFileItemProperties pItemStatCost, int pBitSet, long pValue)
    {
        // just archive
    	iBitSet = pBitSet;
    	iItemStatCost = pItemStatCost;
        iProperties.put(COUNTER[iCounter++], new  PropValue(pProps, pItemStatCost, pBitSet, pValue));
//        System.out.println(pProps + " , " + pValue);
        
    }

    class PropValue
    {
        int                     iProps;
        D2TxtFileItemProperties iItemStatCost;
        int                     iBitSet;
        long                    iValue;

        public PropValue(int pProps, D2TxtFileItemProperties pItemStatCost, int pBitSet, long pValue)
        {
            iProps = pProps;
            iItemStatCost = pItemStatCost;
            iBitSet = pBitSet;
            iValue = pValue;
        }

        public String getValueString()
        {
            return Long.toString(iValue);
        }
    }
    
    public int getiProp(){
    	return iProp;
    }
    
    public int getiPropertiesLength(){
    	
    	return iProperties.size();
    }
    
    public int getRealValue(){
    	return (int)((PropValue) iProperties.get(FIRST)).iValue;
    }
    
    public void setRealValue(int newiValue){
    	((PropValue) iProperties.get(FIRST)).iValue = newiValue;
    }

    public String getValueInternal()
    {
        try
        {
            // the do not display properties
        	//ADDED 140 DUE TO GOREFOOT - NEEDS DOUBLE CHECK.
        	if (iProp == 23 || iProp == 24 || iProp == 159 || iProp == 160 || iProp == 140)
            {
                iNoValue = true;
                return null;
            }
        	
        	/**
        	 * MANA POTS
        	 */
        	
        	if(iProp == 26){
        		PropValue l26 = (PropValue) iProperties.get(FIRST);
        		
        		return l26.iValue + " Replenish Mana";
        		
        	}

        	
            if (iProp == 48)
            {
                PropValue l48 = (PropValue) iProperties.get(FIRST);
                PropValue l49 = (PropValue) iProperties.get(SECOND);
                {
                    return "+" + l48.iValue + " - " +l49.iValue + " Fire Damage";
                }
            }
            
            if (iProp == 50)
            {
                PropValue l50 = (PropValue) iProperties.get(FIRST);
                PropValue l51 = (PropValue) iProperties.get(SECOND);
                {
                    return "+" + l50.iValue + " - " +l51.iValue + " Lightning Damage";
                }
            }
        	
        	/**
        	 * + MAGIC DAMAGE
        	 */
        	if(iProp ==52){

                PropValue l52 = (PropValue) iProperties.get(FIRST);
                PropValue l53 = (PropValue) iProperties.get(SECOND);
                {
                    return "+" + l52.iValue + " - " +l53.iValue + " Magic Damage";
                }
        		
        	}
        	
            if (iProp == 54)
            {
                PropValue l54 = (PropValue) iProperties.get(FIRST);
                PropValue l55 = (PropValue) iProperties.get(SECOND);
                PropValue l56 = (PropValue) iProperties.get(THIRD);
                double lTime = l56.iValue / 25.0;
                {
                    return "+" + l54.iValue + " - " +l55.iValue + " cold damage with " + Math.round(lTime) + " sec Duration";
                }
            }
        	
            if (iProp == 57)
            {
                PropValue l57 = (PropValue) iProperties.get(FIRST);
                PropValue l58 = (PropValue) iProperties.get(SECOND);
                PropValue l59 = (PropValue) iProperties.get(THIRD);

                long lMin = l57.iValue;
                long lMax = l58.iValue;
                double lTime = l59.iValue / 25.0;
                if (lMin == lMax)
                {
                    double lDam = (lMin * lTime) / 10.25;
                    return "+" + Math.round(lDam) + " poison damage over " + Math.round(lTime) + " seconds";
                }
                else
                {
                    double lDam1 = (lMin * lTime) / 10.25;
                    double lDam2 = (lMax * lTime) / 10.25;
                    return "+" + Math.round(lDam1) + "-" + Math.round(lDam2) + " poison damage over " + Math.round(lTime) + " seconds";
                }
            }
            
            if (iProp == 73)
            {
                PropValue lValue = (PropValue) iProperties.get(FIRST);
                
                return  "+" + new Long(lValue.iValue) + " Maximum Durability";
            }
            
            if (iProp == 83)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                PropValue lValue2 = (PropValue) iProperties.get(SECOND);

                return "+" + lValue2.iValue + " to " + D2Character.getCharacterCode((int) lValue1.iValue) + " Skill Levels";
            }
            
            if (iProp == 92)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                return "Required Lvl +" + lValue1.iValue;
            }
            /**
             * THIS IS +SKILLS
             */
            if (iProp == 107||iProp == 97)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                PropValue lValue2 = (PropValue) iProperties.get(SECOND);

                D2TxtFileItemProperties lSkill = D2TxtFile.SKILL_DESC.getRow((int) lValue1.iValue);
//                System.out.println(lSkill.get("str name"));
//               System.out.println(D2TblFile.getString(lSkill.get("str name")));
                return "+" + lValue2.iValue + " to " + D2TblFile.getString(lSkill.get("str name"));
            }
            
            if(iProp == 111 ){
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);                
               
                return D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos")) + " +"+ lValue1.iValue;
            }
            
            /**
             * THIS IS AURAS
             */
            if (iProp == 151)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                PropValue lValue2 = (PropValue) iProperties.get(SECOND);

                D2TxtFileItemProperties lSkill = D2TxtFile.SKILL_DESC.getRow((int) lValue1.iValue);
//                System.out.println(D2TblFile.getString(lSkill.get("str name")));
                return "Level " + lValue2.iValue + " "+ D2TblFile.getString(lSkill.get("str name")) + " Aura When Equipped";
            }
            
            if (iProp == 126)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                PropValue lValue2 = (PropValue) iProperties.get(SECOND);

                int lSkillNr = (int) lValue1.iValue;
                int lSkillCount = (int) lValue2.iValue;
                String lSkillTree = null;
                switch (lSkillNr)
                {
                case 1:
                    lSkillTree = "Fire";
                    break;
                default:
                    iValue = "Item " + iItemName + " with unknown Tree Skill for property " + iProp + ": " + lSkillNr;
                    System.err.println(iValue);
                    return null;
                }
                return "+" + lSkillCount + " to " + lSkillTree + " Skills";
            }
            
            if (iProp == 188)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                PropValue lValue2 = (PropValue) iProperties.get(SECOND);

                int lSkillNr = (int) lValue1.iValue;
                int lSkillCount = (int) lValue2.iValue;

                String lSkillTree = null;
                switch (lSkillNr)
                {
                case 0:
                    lSkillTree = "Bow and Crossbow Skills (Amazon Only)";
                    break;
                case 1:
                    lSkillTree = "Passive and Magic Skills (Amazon Only)";
                    break;
                case 2:
                    lSkillTree = "Javelin and Spear Skills (Amazon Only)";
                    break;
                case 8:
                    lSkillTree = "Fire Skills (Sorceress Only)";
                    break;
                case 9:
                    lSkillTree = "Lightning Skills (Sorceress Only)";
                    break;
                case 10:
                    lSkillTree = "Cold Skills (Sorceress Only)";
                    break;
                case 16:
                    lSkillTree = "Curses (Necromancer only)";
                    break;
                case 17:
                    lSkillTree = "Poison and Bone Skills (Necromancer Only)";
                    break;
                case 18:
                    lSkillTree = "Summoning Skills (Necromancer Only)";
                    break;
                case 24:
                    lSkillTree = "Combat Skills (Paladin Only)";
                    break;
                case 25:
                    lSkillTree = "Offensive Aura Skills (Paladin Only)";
                    break;
                case 26:
                    lSkillTree = "Defensive Aura Skills (Paladin Only)";
                    break;
                case 32:
                    lSkillTree = "Combat Skills (Barbarian Only)";
                    break;
                case 33:
                    lSkillTree = "Masteries Skills (Barbarian Only)";
                    break;
                case 34:
                    lSkillTree = "Warcry Skills (Barbarian Only)";
                    break;
                case 40:
                    lSkillTree = "Summoning Skills (Druid Only)";
                    break;
                case 41:
                    lSkillTree = "Shape-Shifting Skills (Druid Only)";
                    break;
                case 42:
                    lSkillTree = "Elemental Skills (Druid Only)";
                    break;
                case 49:
                    lSkillTree = "Shadow Discipline Skills (Assassin Only)";
                    break;
                case 50:
                    lSkillTree = "Martial Art Skills (Assassin Only)";
                    break;
                default:
                    String lValue = "Item " + iItemName + " with unknown Tree Skill for property " + iProp + ": " + lSkillNr;
                    System.err.println(lValue);
                    return lValue;
                }
                return "+" + lSkillCount + " to " + lSkillTree;
            }
            

//            if(iProp == 198){
//                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
//                PropValue lValue2 = (PropValue) iProperties.get(SECOND);
//                
//                String lNiceString = D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
//                lNiceString = lNiceString.replaceFirst("%d%", lValue2.getValueString());
//                System.out.print("h");
//                return lNiceString;
//            }
            

            
//            if(iProp == 197 ||iProp == 199 || iProp == 195){
//                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
//                PropValue lValue2 = (PropValue) iProperties.get(SECOND);
//                
//                String lNiceString = D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
//                lNiceString = lNiceString.replaceFirst("%d%", lValue2.getValueString());
//                
//                
//                /**
//                 * BAD BAD BAD BAD BAD BAD FIX
//                 * Need to find some correlation between facet + skills. No idea how.
//                 */
//                
//                /**
//                 * DIE
//                 */
//                if(iProp == 197){
//                	
//                	switch((int)lValue1.iValue){
//                	
//                	case 3813:
//            			lNiceString = lNiceString.replaceFirst("%d", "37");
//            			lNiceString = lNiceString.replaceFirst("%s", "Blizzard");
//                		break;
//                	
//                	case 3615:
//            			lNiceString = lNiceString.replaceFirst("%d", "31");
//            			lNiceString = lNiceString.replaceFirst("%s", "Meteor");
//                    	break;
//                    	
//                	case 5939:
//            			lNiceString = lNiceString.replaceFirst("%d", "51");
//            			lNiceString = lNiceString.replaceFirst("%s", "Poison Nova");
//                    	break;
//                    	
//                	case 3439:
//            			lNiceString = lNiceString.replaceFirst("%d", "47");
//            			lNiceString = lNiceString.replaceFirst("%s", "Chain Lightning");
//                    	break;
//                	
//                	
//                	}
////                	System.out.println("DIE "+lValue2.iProps + " " + lValue2.iValue + " "+lValue1.iProps + " " + lValue1.iValue);
//                	
//                	}
//                
//                /**
//                 * LVL UP
//                 */
//                if(iProp == 199){
//                	
//                	switch((int)lValue1.iValue){
//                	
//            		case 3113:
//            			lNiceString = lNiceString.replaceFirst("%d", "41");
//            			lNiceString = lNiceString.replaceFirst("%s", "Nova");
//                		break;
//                	
//                	case 2973:
//            			lNiceString = lNiceString.replaceFirst("%d", "29");
//            			lNiceString = lNiceString.replaceFirst("%s", "Blaze");
//                    	break;
//                    	
//                	case 2859:
//            			lNiceString = lNiceString.replaceFirst("%d", "43");
//            			lNiceString = lNiceString.replaceFirst("%s", "Frost Nova");
//                    	break;
//                    	
//                	case 17815:
//            			lNiceString = lNiceString.replaceFirst("%d", "23");
//            			lNiceString = lNiceString.replaceFirst("%s", "Venom");
//                    	break;
//                    	
//                	}
//                	
////                	System.out.println("LEVEL "+lValue2.iProps + " " + lValue2.iValue + " "+lValue1.iProps + " " + lValue1.iValue);
//                	
//                }
//                
////                System.out.print("h");
//                return lNiceString;
//            }
//            
            /**
             * CTC
             */
            if (iProp == 201||iProp == 197 ||iProp == 199 || iProp == 195||iProp == 198|| iProp == 196)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                PropValue lValue2 = (PropValue) iProperties.get(SECOND);
                PropValue lValue3 = (PropValue) iProperties.get(THIRD);

                iValue = D2TblFile.getString(lValue3.iItemStatCost.get("descstrpos"));
                iValue = iValue.replaceFirst("%d%", lValue3.getValueString());
                iValue = iValue.replaceFirst("%d", lValue1.getValueString());

                D2TxtFileItemProperties lSkills = D2TxtFile.SKILLS.getRow((int) lValue2.iValue);
                String lDesc = lSkills.get("skilldesc");
                D2TxtFileItemProperties lSkill = D2TxtFile.SKILL_DESC.searchColumns("skilldesc", lDesc);
                return iValue.replaceFirst("%s", D2TblFile.getString(lSkill.get("str name")));
            }
            
            if (iProp == 204)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                PropValue lValue2 = (PropValue) iProperties.get(SECOND);
                PropValue lValue3 = (PropValue) iProperties.get(THIRD);
                PropValue lValue4 = (PropValue) iProperties.get(FOURTH);

                String lNiceString = D2TblFile.getString(lValue4.iItemStatCost.get("descstrpos"));

                D2TxtFileItemProperties lSkills = D2TxtFile.SKILLS.getRow((int) lValue2.iValue);
                String lDesc = lSkills.get("skilldesc");
                D2TxtFileItemProperties lSkill = D2TxtFile.SKILL_DESC.searchColumns("skilldesc", lDesc);
                lNiceString = lNiceString.replaceFirst("%d", lValue3.getValueString());
                lNiceString = lNiceString.replaceFirst("%d", lValue4.getValueString());
                return "Level " + lValue1.iValue + " " + D2TblFile.getString(lSkill.get("str name")) + " " + lNiceString;
            }
            
                        
            if (iProp == 240)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                
                
                String lNiceString = D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
                return Long.toString(Math.round((lValue1.iValue * 0.125) * (iCharLvl - 1))) + "% " + lNiceString;
            }
            
//            if (iProp == 242)
//            {
//                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
//                
//                String lNiceString = D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
//                System.out.println(lNiceString);
//               return iCharLvl + " to " + lNiceString;
//            }
            
            /**
             * DURA OVER TIME
             */
            
            if (iProp == 252)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                
                String lNiceString = D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
                return "Repair 1 Durability in " + Long.toString(Math.round(100.0 / lValue1.iValue)) + " Seconds";
            }
            
            /**
             * REPLENISHES QUANTITY - NOT WORTH DEALING WITH AMOUNT
             */
            
            if(iProp == 253){
            	
            	PropValue lValue1 = (PropValue) iProperties.get(FIRST);
            	return D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
            	
            }

            
            if (iProp == 333)
            {
                PropValue lValue1 = (PropValue) iProperties.get(FIRST);
                
                String lNiceString = D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
                return lValue1.iValue + "% "+lNiceString;
            }
            
            /**
             * WIRTS LEG
             */
            
            if(iProp == 356){
            	PropValue lValue1 = (PropValue) iProperties.get(FIRST);
            	switch ((int)lValue1.iValue){
            	
            	case 0:
            		
            		return "Found In Normal Difficulty";
            		
            	case 1:
            		
            		return "Found In Nightmare Difficulty";
            		
            	case 2:
            		
            		return "Found In Hell Difficulty";
            	
            	}
                

            }
            
            if(iProp == 1337){
            	/**
            	 * MY OWN PROPERTY FOR ALL RESISTANCES!
            	 * 
            	 */
            	PropValue lValue1 = (PropValue) iProperties.get(FIRST);
            	return "All Resistances +" + lValue1.iValue;
            }

            PropValue lValue1 = (PropValue) iProperties.get(FIRST);
//            PropValue lValue2 = (PropValue) iProperties.get(SECOND);
            String lNiceString = D2TblFile.getString(lValue1.iItemStatCost.get("descstrpos"));
            if (lNiceString == null)
            {
            }
            else if (lNiceString.indexOf("%d") != -1)
            {
            }
            else
            {
                String lGoMuleProp = D2TxtFile.GOMULE_PROPS.getProperty(lValue1.iItemStatCost.get("Stat"));
                if (lGoMuleProp != null)
                {
                    if ("true".equals(lGoMuleProp))
                    {
                        return Long.toString(lValue1.iValue) + "% " + lNiceString;
                    }
                    if ("false".equals(lGoMuleProp))
                    {
                        return Long.toString(lValue1.iValue) + " " + lNiceString;
                    }
                    if ("none".equals(lGoMuleProp))
                    {
                        return lNiceString;
                    }
                    if ("level_true".equals(lGoMuleProp))
                    {
                        return Long.toString(Math.round((lValue1.iValue * 0.125) * (iCharLvl))) + "% " + lNiceString+ " (Based on Char Lvl)";
                    }
                    if ("level_false".equals(lGoMuleProp))
                    {
                        return Long.toString((long)Math.floor((lValue1.iValue * 0.125) * iCharLvl)) + " " + lNiceString+ " (Based on Char Lvl)";
                    }
                    else
                    {
                        System.err.println("Item " + iItemName + " with wrongProp: " + lValue1.iItemStatCost.get("Stat") + " " + lGoMuleProp + ": " + new Long(lValue1.iValue) + " " + lNiceString);
                    }
                }
                else
                {
                    System.err.println("NotFound: " + iItemName + " - " + lValue1.iItemStatCost.get("Stat") + ": " + new Long(lValue1.iValue) + " " + lNiceString + " : " +this.iProp+ " : " +this.iProp);
                }
            }
        }
        catch (Exception pEx)
        {
            pEx.printStackTrace();
            return "Error with defining this property";
        }
        return null;
    }

    public boolean hasNoValue()
    {
        return iNoValue;
    }

    public String getValue()
    {
        if (iNoValue)
        {
            return null;
        }
        if (iValue == null)
        {
            iValue = getValueInternal();
                        
            if (iNoValue)
            {
                return null;
            }
            if (iValue == null)
            {
                iValue = "No Display for property: " + iProp;
            }
        }
        

        
        return iValue;
    }

	public int getBitSet() {
		// TODO Auto-generated method stub
		return iBitSet;
	}

	public D2TxtFileItemProperties getItemStatCost() {
		
		return iItemStatCost;
	}

    //    public String getValueInternal()
    //    {
    //        try
    //        {
    //            // the do not display properties
    //            if (iProp == 23 || iProp == 24 || iProp == 159 || iProp == 160)
    //            {
    //                iNoValue = true;
    //                return null;
    //            }
    //            else if (iProp == 57)
    //            {
    //// iProperties.put(new Long(pProps), new Long(pValue));
    //// if (pProps == 59)
    //// {
    //                PropValue l57 = (PropValue) iProperties.get(FIRST);
    //                PropValue l58 = (PropValue) iProperties.get(SECOND);
    //                PropValue l59 = (PropValue) iProperties.get(THIRD);
    //                
    //                    long lMin = l57.iValue;
    //                    long lMax = l57.iValue;
    //                    double lTime = ((Long) iProperties.get(new Long(59))).longValue() / 25.0;
    //                    if (lMin == lMax)
    //                    {
    //                        double lDam = (lMin * lTime) / 10.25;
    //                        iValue = "+" + Math.round(lDam) + " poison damage over " +
    // Math.round(lTime) + " secondiProp== 97s";
    //                    }
    //                    else
    //                    {
    //                        double lDam1 = (lMin * lTime) / 10.25;
    //                        double lDam2 = (lMax * lTime) / 10.25;
    //                        iValue = "+" + Math.round(lDam1) + "-" + Math.round(lDam2) + " poison
    // damage over " + Math.round(lTime) + " seconds";
    //                    }
    //// }
    //                return;
    //            }
    //            else if (iProp == 73)
    //            {
    //                iValue = "+" + new Long(pValue) + " Maximum Durability";
    //            }
    //            else if (iProp == 83)
    //            {
    //                if (pBitSet == 0)
    //                {
    //                    iProperties.put(new Long(pBitSet), new Long(pValue));
    //                }
    //                else
    //                {
    //                    int lValue = ((Long) iProperties.get(new Long(0))).intValue();
    //                    iValue = "+" + pValue + " to " + D2Character.getCharacterCode(lValue) + "
    // Skill Levels";
    //                }
    //                return;
    //            }
    //            else if (iProp == 92)
    //            {
    //                iValue = "Required Lvl +" + pValue;
    //                return;
    //            }
    //            else if (iProp == 107)
    //            {
    //                if (pBitSet == 0)
    //                {
    //                    iProperties.put(new Long(pBitSet), new Long(pValue));
    //                }
    //                else
    //                {
    //                    int lSkillNr = ((Long) iProperties.get(new Long(0))).intValue();
    //                    D2TxtFileItemProperties lSkill = D2TxtFile.SKILL_DESC.getRow(lSkillNr);
    //                    iValue = "+" + pValue + " to " + D2TblFile.getString(lSkill.get("str
    // name"));
    //                }
    //                return;
    //            }
    //            else if (iProp == 126)
    //            {
    //                if (pBitSet == 0)
    //                {
    //                    iProperties.put(new Long(pBitSet), new Long(pValue));
    //                }
    //                else
    //                {
    //                    int lSkillNr = ((Long) iProperties.get(new Long(0))).intValue();
    //                    int lSkillCount = (int) pValue;
    //                    String lSkillTree = null;
    //                    switch (lSkillNr)
    //                    {
    //                    case 1:
    //                        lSkillTree = "Fire";
    //                        break;
    //                    default:
    //                        iValue = "Item " + iItemName + " with unknown Tree Skill for property " +
    // iProp + ": " + lSkillNr;
    //                        System.err.println(iValue);
    //                        return;
    //                    }
    //                    iValue = "+" + lSkillCount + " to " + lSkillTree + " Skills";
    //                }
    //                return;
    //            }
    //            else if (iProp == 188)
    //            {
    //                if (pBitSet == 0)
    //                {
    //                    iProperties.put(new Long(pBitSet), new Long(pValue));
    //                    iProperties.put("String", pItemStatCost.get("descstrpos"));
    //                }
    //                else
    //                {
    //                    int lSkillNr = ((Long) iProperties.get(new Long(0))).intValue();
    //                    int lSkillCount = (int) pValue;
    //                    String lSkillTree = null;
    //                    switch (lSkillNr)
    //                    {
    //                    case 0:
    //                        lSkillTree = "Bow and Crossbow Skills (Amazon Only)";
    //                        break;
    //                    case 1:
    //                        lSkillTree = "Passive and Magic Skills (Amazon Only)";
    //                        break;
    //                    case 2:
    //                        lSkillTree = "Javelin and Spear Skills (Amazon Only)";
    //                        break;
    //                    case 8:
    //                        lSkillTree = "Fire Skills (Sorceress Only)";
    //                        break;
    //                    case 9:
    //                        lSkillTree = "Lightning Skills (Sorceress Only)";
    //                        break;
    //                    case 10:
    //                        lSkillTree = "Cold Skills (Sorceress Only)";
    //                        break;
    //                    case 16:
    //                        lSkillTree = "Curses (Necromancer only)";
    //                        break;
    //                    case 17:
    //                        lSkillTree = "Poison and Bone Skills (Necromancer Only)";
    //                        break;
    //                    case 18:
    //                        lSkillTree = "Summoning Skills (Necromancer Only)";
    //                        break;
    //                    case 24:
    //                        lSkillTree = "Combat Skills (Paladin Only)";
    //                        break;
    //                    case 25:
    //                        lSkillTree = "Offensive Aura Skills (Paladin Only)";
    //                        break;
    //                    case 26:
    //                        lSkillTree = "Defensive Aura Skills (Paladin Only)";
    //                        break;
    //                    case 32:
    //                        lSkillTree = "Combat Skills (Barbarian Only)";
    //                        break;
    //                    case 33:
    //                        lSkillTree = "Masteries Skills (Barbarian Only)";
    //                        break;
    //                    case 34:
    //                        lSkillTree = "Warcry Skills (Barbarian Only)";
    //                        break;
    //                    case 40:
    //                        lSkillTree = "Summoning Skills (Druid Only)";
    //                        break;
    //                    case 41:
    //                        lSkillTree = "Shape-Shifting Skills (Druid Only)";
    //                        break;
    //                    case 42:
    //                        lSkillTree = "Elemental Skills (Druid Only)";
    //                        break;
    //                    case 49:
    //                        lSkillTree = "Shadow Discipline Skills (Assassin Only)";
    //                        break;
    //                    case 50:
    //                        lSkillTree = "Martial Art Skills (Assassin Only)";
    //                        break;
    //                    default:
    //                        iValue = "Item " + iItemName + " with unknown Tree Skill for property " +
    // iProp + ": " + lSkillNr;
    //                        System.err.println(iValue);
    //                        return;
    //                    }
    //                    iValue = "+" + lSkillCount + " to " + lSkillTree;
    //                    // String lDescr = (String) iProperties.get("String");
    //                    // lDescr = lDescr.replaceFirst("1", ((Long)
    //                    // iProperties.get(new Long(0))).toString() );
    //                    // iValue = D2TblFile.getString(lDescr);
    //                    // iValue = iValue.replaceFirst("%d", Long.toString(pValue)
    //                    // );
    //                    // System.err.println("Value: " + iValue );
    //                }
    //                return;
    //            }
    //            else if (iProp == 201)
    //            {
    //                if (pBitSet != 2)
    //                {
    //                    iProperties.put(new Long(pBitSet), new Long(pValue));
    //                }
    //                else
    //                {
    //                    iValue = D2TblFile.getString(pItemStatCost.get("descstrpos"));
    //                    iValue = iValue.replaceFirst("%d%", Long.toString(pValue));
    //                    iValue = iValue.replaceFirst("%d", ((Long) iProperties.get(new
    // Long(0))).toString());
    //                    int lSkillNr = ((Long) iProperties.get(new Long(1))).intValue();
    //                    D2TxtFileItemProperties lSkills = D2TxtFile.SKILLS.getRow(lSkillNr);
    //                    String lDesc = lSkills.get("skilldesc");
    //                    D2TxtFileItemProperties lSkill =
    // D2TxtFile.SKILL_DESC.searchColumns("skilldesc", lDesc);
    //                    iValue = iValue.replaceFirst("%s", D2TblFile.getString(lSkill.get("str
    // name")));// lSkill.get("skilldesc")
    //                }
    //                return;
    //            }
    //            else if (iProp == 204)
    //            {
    //                // 0 - 6 bits -> level (3)
    //                // 1 - 10 bits -> ID (278)
    //                // 2 - 8 bits -> remain (9)
    //                // 3 - 8 bits -> max (11)
    //                if (pBitSet != 3)
    //                {
    //                    iProperties.put(new Long(pBitSet), new Long(pValue));
    //                }
    //                else
    //                {
    //                    String lNiceString =
    // D2TblFile.getString(pItemStatCost.get("descstrpos"));
    //                    long l1 = ((Long) iProperties.get(new Long(0))).longValue();
    //                    int l2 = ((Long) iProperties.get(new Long(1))).intValue();
    //                    String l3 = ((Long) iProperties.get(new Long(2))).toString();
    //                    String l4 = Long.toString(pValue);
    //
    //                    D2TxtFileItemProperties lSkills = D2TxtFile.SKILLS.getRow(l2);
    //                    String lDesc = lSkills.get("skilldesc");
    //                    D2TxtFileItemProperties lSkill =
    // D2TxtFile.SKILL_DESC.searchColumns("skilldesc", lDesc);
    //                    lNiceString = lNiceString.replaceFirst("%d", l3);
    //                    lNiceString = lNiceString.replaceFirst("%d", l4);
    //                    iValue = "Level " + l1 + " " + D2TblFile.getString(lSkill.get("str
    // name")) + " " + lNiceString;
    //                }
    //            }
    //            else if (iProp == 240)
    //            {
    //                String lNiceString =
    // D2TblFile.getString(pItemStatCost.get("descstrpos"));
    //                iValue = Long.toString(Math.round((pValue * 0.125) * (iCharLvl - 1))) +
    // "% " + lNiceString;
    //            }
    //            else if (iProp == 252)
    //            {
    //                String lNiceString =
    // D2TblFile.getString(pItemStatCost.get("descstrpos"));
    //                iValue = "Repair 1 Durability in " + Long.toString(Math.round(100.0 /
    // pValue)) + " Seconds";
    //            }
    //
    //            if (iValue == null)
    //            {
    //                String lNiceString =
    // D2TblFile.getString(pItemStatCost.get("descstrpos"));
    //                if (lNiceString == null)
    //                {
    //                    // System.err.println(" StatStr: " + iProp + " - " + pProps
    //                    // + " - " + pItemStatCost.get("Stat") + " - " +
    //                    // pItemStatCost.get("descstrpos") + " - " +
    //                    // pItemStatCost.get("descstrneg") + ", " + pValue);
    //                    // System.err.println(" StatStr: " +
    //                    // pItemStatCost.get("Stat") + " - " +
    //                    // pItemStatCost.get("descstrpos") + " - " +
    //                    // pItemStatCost.get("descstrneg") + ", " + pValue);
    //                }
    //                else if (lNiceString.indexOf("%d") != -1)
    //                {
    //                    // System.err.println(" StatStr: " + iProp + " - " + pProps
    //                    // + " - " + pItemStatCost.get("Stat") + " - " +
    //                    // pItemStatCost.get("descstrpos") + " - " +
    //                    // pItemStatCost.get("descstrneg") + ", " + pValue);
    //                    // iValue = lNiceString.replaceFirst("%d",
    //                    // Long.toString(pValue));
    //                    // System.err.println(" Test1: " + iProp + " - " + pProps +
    //                    // " - " + lNiceString);
    //                    // System.err.println(" test2: " + pItemStatCost.get("Stat")
    //                    // + " - " + pItemStatCost.get("descstrpos") + " - " +
    //                    // pItemStatCost.get("descstrneg") + ", " + pValue);
    //                    // pProperties.add(lNiceString);
    //                }
    //                else
    //                {
    //                    String lGoMuleProp =
    // D2TxtFile.GOMULE_PROPS.getProperty(pItemStatCost.get("Stat"));
    //                    if (lGoMuleProp != null)
    //                    {
    //                        if ("true".equals(lGoMuleProp))
    //                        {
    //                            iValue = Long.toString(pValue) + "% " + lNiceString;
    //                        }
    //                        else if ("false".equals(lGoMuleProp))
    //                        {
    //                            iValue = Long.toString(pValue) + " " + lNiceString;
    //                        }
    //                        else if ("none".equals(lGoMuleProp))
    //                        {
    //                            iValue = lNiceString;
    //                        }
    //                        else if ("level_true".equals(lGoMuleProp))
    //                        {
    //                            iValue = Long.toString(Math.round((pValue * 0.125) * (iCharLvl))) + "% "
    // + lNiceString;
    //                        }
    //                        else if ("level_false".equals(lGoMuleProp))
    //                        {
    //                            iValue = Long.toString(Math.round((pValue * 0.125) * iCharLvl)) + " " +
    // lNiceString;
    //                        }
    //                        else
    //                        {
    //                            // iValue = pItemStatCost.get("Stat") + ": " + new
    //                            // Long(pValue);
    //                            System.err.println("Item " + iItemName + " with wrongProp: " +
    // pItemStatCost.get("Stat") + " " + lGoMuleProp + ": " + new Long(pValue) +
    // " " + lNiceString);
    //                        }
    //                    }
    //                    else
    //                    {
    //                        // iValue = pItemStatCost.get("Stat") + ": " + new
    //                        // Long(pValue);
    //                        System.err.println("NotFound: " + iItemName + " - " +
    // pItemStatCost.get("Stat") + ": " + new Long(pValue) + " " + lNiceString);
    //                    }
    //                }
    //            }
    //        }
    //        catch (Exception pEx)
    //        {
    //            pEx.printStackTrace();
    //            iValue = "Error with defining this property";
    //        }
    //    }

}