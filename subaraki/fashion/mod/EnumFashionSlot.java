package subaraki.fashion.mod;

public enum EnumFashionSlot {

	HEAD,
	CHEST,
	LEGS,
	BOOTS,
	WEAPON,
	SHIELD;
	
	public static EnumFashionSlot fromInt(int id){
		switch (id) {
		case 0:
			return EnumFashionSlot.HEAD;
		case 1:
			return EnumFashionSlot.CHEST;
		case 2 : 
			return EnumFashionSlot.LEGS;
		case 3 : 
			return EnumFashionSlot.BOOTS;
		case 4 : 
			return EnumFashionSlot.WEAPON;
		case 5 :
			return EnumFashionSlot.SHIELD;
		}
		return HEAD;
	}
}
