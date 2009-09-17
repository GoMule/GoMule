package gomule.item.filter;

import gomule.item.*;

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
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Normal";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.getItemQuality().equals("normal");
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Exceptional";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.getItemQuality().equals("exceptional");
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Elite";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.getItemQuality().equals("elite");
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Other";
				}

				public boolean isFilterItem( D2Item pItem )
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
		D2ItemFilter lFilter = new D2ItemFilter()
		{
			public String getDisplayString()
			{
				return "Ethereal";
			}

			public boolean isFilterItem( D2Item pItem )
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
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Unique";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isUnique();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Set";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isSet();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Runeword";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isRuneWord();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Rare";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isRare();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Magic";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isMagical();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Craft";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isCrafted();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Socketed";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isSocketed();
				}
			};
			lList.add(  lFilter );
		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Other";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return !(pItem.isUnique() || pItem.isSet() || pItem.isRuneWord() || pItem.isRare() 
							|| pItem.isMagical() || pItem.isCrafted() || pItem.isSocketed() );
				}
			};
			lList.add(  lFilter );
		}
		
		return lList;
	}

	public ArrayList getItemCategories()
	{
		ArrayList lList = new ArrayList();
		
//		{
//			D2ItemFilter lFilter = new D2ItemFilter()
//			{
//				public String getDisplayString()
//				{
//					return "All";
//				}
//
//				public boolean isFilterItem( D2Item pItem )
//				{
//					return true;
//				}
//			};
//			lList.add(  lFilter );
//		}
		
		{
			D2ItemFilter lFilter = new D2ItemFilter()
			{
				public String getDisplayString()
				{
					return "Armor";
				}

				public boolean isFilterItem( D2Item pItem )
				{
					return pItem.isTypeArmor();
				}
			};
			lList.add(  lFilter );
		}
		
		return lList;
	}

}
