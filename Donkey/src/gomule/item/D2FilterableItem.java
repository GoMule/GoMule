package gomule.item;

public interface D2FilterableItem
{
	public String getItemName();
	public String getItemQuality();
	
	public boolean isUnique();
	public boolean isSet();
	public boolean isRuneWord();
	public boolean isCrafted();
	public boolean isRare();
	public boolean isMagical();
	public boolean isSocketed();
	
	public boolean isEthereal();
	
	public boolean isTypeArmor();
	public boolean isBodyLocation(D2BodyLocations pLocation);
	public boolean isTypeWeapon();
	public boolean isWeaponType(D2WeaponTypes pType);
	public boolean isTypeMisc();
	
	public boolean isGem();
	public boolean isGemPerfect();
	public boolean isGemFlawless();
	public boolean isGemNormal();
	public boolean isGemFlawed();
	public boolean isGemChipped();
	
	public boolean isRune();
	public String getRuneCode();
	
	public boolean isJewel();
	
	public boolean isCharmSmall();
	public boolean isCharmLarge();
	public boolean isCharmGrand();
	
	public int getReqLvl();
	public int getReqDex();
	public int getReqStr();
	
	public D2PropCollection getPropCollection();
}
