package gomule.item.filter;

import gomule.item.*;
import gomule.item.filter.free.*;

import java.util.*;

/**
 * default item filter factory (works for all version)
 * @author Marco
 *
 */
public class D2ItemFilterFactoryDefault implements D2ItemFilterFactory
{
	
	public ArrayList getItemQualities()
	{
		ArrayList lList = new ArrayList();
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Normal")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.getItemQuality().equals("normal");
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Exceptional")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.getItemQuality().equals("exceptional");
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Elite")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.getItemQuality().equals("elite");
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Other")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return !( pItem.getItemQuality().equals("normal")
						|| pItem.getItemQuality().equals("exceptional")
						|| pItem.getItemQuality().equals("elite")
						);
				}
			};
			lList.add(  lFilter );
		}
		
		return lList;
	}
	
	public D2ItemFilter getEthereal()
	{
		D2ItemFilter lFilter = new D2ItemFilterDefault("Ethereal")
		{
			public boolean isFilterItem( D2FilterableItem pItem )
			{
				return pItem.isEthereal();
			}
		};
		
		return lFilter;
	}

	public ArrayList getItemTypes()
	{
		ArrayList lList = new ArrayList();
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Unique")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.isUnique();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Set")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.isSet();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Runeword")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.isRuneWord();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Rare")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.isRare();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Magic")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.isMagical();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Craft")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.isCrafted();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Socketed")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return pItem.isSocketed();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilterDefault("Other")
			{
				public boolean isFilterItem( D2FilterableItem pItem )
				{
					return !(pItem.isUnique() || pItem.isSet() || pItem.isRuneWord() || pItem.isRare() 
							|| pItem.isMagical() || pItem.isCrafted() || pItem.isSocketed() );
				}
			};
			lList.add(  lFilter );
		}
		
		return lList;
	}
	
	private static final String ARMOR = "Armor";
	private static final String WEAPON = "Weapon";
	private static final String SOCKET_FILLER = "Socket Filler";
	private static final String CHARM = "Charm";
	private static final String MISC = "Misc";

	// special socket fillers
	private static final String GEM = "Gem";
	private static final String RUNE = "Rune";
	
	public ArrayList getItemCategories(int pLevel, D2ItemFilter pFilter)
	{
		if ( pLevel == 1 )
		{
			ArrayList lList = new ArrayList();
			
			{
				D2ItemFilter lFilter = new D2ItemFilterDefault(ARMOR)
				{
					public boolean isFilterItem( D2FilterableItem pItem )
					{
						return pItem.isTypeArmor();
					}
				};
				lList.add(  lFilter );
			}
			
			{
				D2ItemFilter lFilter = new D2ItemFilterDefault(WEAPON)
				{
					public boolean isFilterItem( D2FilterableItem pItem )
					{
						return pItem.isTypeWeapon();
					}
				};
				lList.add(  lFilter );
			}
			
			{
				D2ItemFilter lFilter = new D2ItemFilterDefault(SOCKET_FILLER)
				{
					public boolean isFilterItem( D2FilterableItem pItem )
					{
						return pItem.isJewel() || pItem.isGem() || pItem.isRune();
					}
				};
				lList.add(  lFilter );
			}
			
			{
				D2ItemFilter lFilter = new D2ItemFilterDefault(CHARM)
				{
					public boolean isFilterItem( D2FilterableItem pItem )
					{
						return pItem.isCharmGrand() || pItem.isCharmLarge() || pItem.isCharmSmall();
					}
				};
				lList.add(  lFilter );
			}
			
			{
				D2ItemFilter lFilter = new D2ItemFilterDefault(MISC)
				{
					public boolean isFilterItem( D2FilterableItem pItem )
					{
						return pItem.isTypeMisc() 
						&& !(pItem.isJewel() || pItem.isGem() || pItem.isRune()) 
						&& !(pItem.isCharmGrand() || pItem.isCharmLarge() || pItem.isCharmSmall());
					}
				};
				lList.add(  lFilter );
			}
			
			return lList;
		}
		else if ( pLevel == 2 )
		{
			if ( pFilter == null )
			{
				return null;
			}
			if ( pFilter.getDisplayString().equals( ARMOR ) )
			{
				ArrayList lList = new ArrayList();
				
		        ArrayList lArmorFilterList = D2BodyLocations.getArmorFilterList();

		        for ( int i = 0 ; i < lArmorFilterList.size() ; i++ )
				{
		        	// TODO remove All from body locations and remove this compare one the 
		        	// old filter is out of order
		        	D2BodyLocations lLocation = (D2BodyLocations) lArmorFilterList.get( i );
		        	if ( !lLocation.toString().equalsIgnoreCase( "all" ) )
		        	{
						D2ItemFilter lFilter = new D2FilterBodyLocation( lLocation );
						lList.add(  lFilter );
		        	}
				}
		        
		        return lList;
			}
			else if ( pFilter.getDisplayString().equals( WEAPON ) )
			{
				ArrayList lList = new ArrayList();
				
		        ArrayList lWeaponFilterList = D2WeaponTypes.getWeaponTypeList();
		        
		        for ( int i = 0 ; i < lWeaponFilterList.size() ; i++ )
				{
		        	// TODO remove "Object" from weapon locations and remove this compare one the 
		        	// old filter is out of order
		        	if ( lWeaponFilterList.get( i ) instanceof D2WeaponTypes )
		        	{
			        	D2WeaponTypes lLocation = (D2WeaponTypes) lWeaponFilterList.get( i );
			        	if ( !lLocation.toString().equalsIgnoreCase( "all" ) )
			        	{
							D2ItemFilter lFilter = new D2FilterWeaponTypes( lLocation );
							lList.add(  lFilter );
			        	}
		        	}
				}
		        
		        return lList;
			}
			else if ( pFilter.getDisplayString().equals( SOCKET_FILLER ) )
			{
				ArrayList lList = new ArrayList();
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault("Jewel")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isJewel();
						}
					};
					lList.add(  lFilter );
				}
		        
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault(GEM)
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isGem();
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault(RUNE)
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isRune();
						}
					};
					lList.add(  lFilter );
				}
				
				return lList;
			}
			else if ( pFilter.getDisplayString().equals( CHARM ) )
			{
				ArrayList lList = new ArrayList();
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Small" )
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isCharmSmall();
						}
					};
					lList.add(  lFilter );
				}
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Large" )
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isCharmLarge();
						}
					};
					lList.add(  lFilter );
				}
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Grand" )
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isCharmGrand();
						}
					};
					lList.add(  lFilter );
				}
				
				return lList;
			}
			else if ( pFilter.getDisplayString().equals( MISC ) )
			{
				ArrayList lList = new ArrayList();
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Amulet" )
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isBodyLocation(D2BodyLocations.BODY_NECK);
						}
					};
					lList.add(  lFilter );
				}
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Ring" )
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isBodyLocation(D2BodyLocations.BODY_LRIN);
						}
					};
					lList.add(  lFilter );
				}
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Other" )
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return !pItem.isBodyLocation(D2BodyLocations.BODY_NECK) && !pItem.isBodyLocation(D2BodyLocations.BODY_LRIN);
						}
					};
					lList.add(  lFilter );
				}
				
				return lList;
			}
		}
		else if ( pLevel == 3 )
		{
			if ( pFilter == null )
			{
				return null;
			}
			if ( pFilter.getDisplayString().equals( GEM ) )
			{
				ArrayList lList = new ArrayList();
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Perfect")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isGemPerfect();
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Flawless")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isGemFlawless();
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Normal")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isGemNormal();
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Flawed")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isGemFlawed();
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Chipped")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.isGemChipped();
						}
					};
					lList.add(  lFilter );
				}

				return lList;
			}
			else if ( pFilter.getDisplayString().equals( RUNE ) )
			{
				ArrayList lList = new ArrayList();
				
				{
					D2ItemFilter lFilter = new D2FilterRunes( "Vex, Ohm, Lo, Sur, Ber, Jah, Cham, Zod", "r26", "r33");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "Um, Mal, Ist, Gul", "r22", "r25");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "Fal, Lem, Pul", "r19", "r21");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "Io, Lum, Ko", "r16", "r18");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "Dol, Hel, Sheal", "r13", "r15");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "Thul, Amn, Sol", "r10", "r12");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "Tal, Ral, Ort", "r07", "r09");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "Nef, Eth, Ith", "r04", "r06");
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2FilterRunes( "El, Eld, Tir", "r01", "r03");
					lList.add(  lFilter );
				}

				return lList;
			}
		}
		else if ( pLevel == 4 )
		{
			if ( pFilter == null )
			{
				return null;
			}
			if ( pFilter.getDisplayString().equals( GEM ) )
			{
				ArrayList lList = new ArrayList();
				
				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Diamond")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.getItemName().indexOf( "Diamond" ) != -1;
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Emerald")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.getItemName().indexOf( "Emerald" ) != -1;
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Ruby")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.getItemName().indexOf( "Ruby" ) != -1;
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Sapphire")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.getItemName().indexOf( "Sapphire" ) != -1;
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Skull")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.getItemName().indexOf( "Skull" ) != -1;
						}
					};
					lList.add(  lFilter );
				}

				{
					D2ItemFilter lFilter = new D2ItemFilterDefault( "Topaz")
					{
						public boolean isFilterItem( D2FilterableItem pItem )
						{
							return pItem.getItemName().indexOf( "Topaz" ) != -1;
						}
					};
					lList.add(  lFilter );
				}

				return lList;
			}
			else if ( pFilter.getDisplayString().equals( RUNE ) )
			{
				ArrayList lList = new ArrayList();
				
				{
					D2ItemFilter lFilter = new D2FilterRunes( "Ral", "r08");
					lList.add(  lFilter );
				}

				return lList;
			}
		}
		return null;
	}
	
	class D2FilterBodyLocation implements D2ItemFilter
	{
		private D2BodyLocations	iBodyLocation;
		
		public D2FilterBodyLocation(D2BodyLocations pBodyLocation)
		{
			iBodyLocation = pBodyLocation;
		}

		public String getDisplayString()
		{
			return iBodyLocation.toString();
		}

		public boolean isFilterItem( D2FilterableItem pItem )
		{
			return pItem.isBodyLocation( iBodyLocation );
		}
		
		public String toString()
		{
			return getDisplayString();
		}
	}

	class D2FilterWeaponTypes implements D2ItemFilter
	{
		private D2WeaponTypes	iWeaponType;
		
		public D2FilterWeaponTypes(D2WeaponTypes pWeaponType)
		{
			iWeaponType = pWeaponType;
		}

		public String getDisplayString()
		{
			return iWeaponType.toString();
		}

		public boolean isFilterItem( D2FilterableItem pItem )
		{
			return pItem.isWeaponType( iWeaponType );
		}
		
		public String toString()
		{
			return getDisplayString();
		}
	}

	class D2FilterRunes extends D2ItemFilterDefault
	{
		private String	iLower;
		private String	iHigher;
		
		public D2FilterRunes(String pDisplay, String pLower, String pHigher)
		{
			super(pDisplay);
			iLower = pLower;
			iHigher = pHigher;
		}

		public D2FilterRunes(String pDisplay, String pSingle)
		{
			this(pDisplay, pSingle, pSingle);
		}

		public boolean isFilterItem( D2FilterableItem pItem )
		{
			return pItem.getRuneCode().compareTo( iLower ) >= 0 && pItem.getRuneCode().compareTo( iHigher ) <= 0;
		}
		
	}

	public ArrayList getItemQueryFactory()
	{
		ArrayList lReturn = new ArrayList();

		{
			lReturn.add( new D2ItemFilterQueryFactoryInteger( new D2ItemFilterQueryFactoryProperty("Requerement Lvl")
			{
				public Object getValue( D2FilterableItem pItem )
				{
					return new Integer( pItem.getReqLvl() );
				}
			}));
		}
		
		{
			lReturn.add( new D2ItemFilterQueryFactoryInteger( new D2ItemFilterQueryFactoryProperty("Requerement Str")
			{
				public Object getValue( D2FilterableItem pItem )
				{
					return new Integer( pItem.getReqStr() );
				}
			}));
		}
		
		{
			lReturn.add( new D2ItemFilterQueryFactoryInteger( new D2ItemFilterQueryFactoryProperty("Requerement Dex")
			{
				public Object getValue( D2FilterableItem pItem )
				{
					return new Integer( pItem.getReqDex() );
				}
			}));
		}
		
		{
			lReturn.add( new D2ItemFilterQueryFactoryInteger( new D2ItemPropFilter("To Life", 7)));
		}
		
		return lReturn;
	}
	
	class D2ItemPropFilter extends D2ItemFilterQueryFactoryProperty
	{
		private final int		iPropNr;
		
		public D2ItemPropFilter( String pName, int pPropNr )
		{
			super( pName );
			iPropNr = pPropNr;
		}
		
		public Object getValue( D2FilterableItem pItem )
		{
			D2PropCollection lPropCollection = pItem.getPropCollection();
			
			for ( int i = 0 ; i < lPropCollection.size() ; i++ )
			{
				D2Prop lProp = (D2Prop) lPropCollection.get( i );
				if ( lProp.getPNum() == iPropNr )
				{
					return new Integer( lProp.getPVals()[0] );
				}
			}
			
			return null;
		}
	}

}
