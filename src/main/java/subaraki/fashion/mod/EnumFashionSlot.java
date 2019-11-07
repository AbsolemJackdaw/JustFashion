package subaraki.fashion.mod;

public enum EnumFashionSlot {

    HEAD, CHEST, LEGS, BOOTS, WEAPON, SHIELD;

    public static enum EnumFashionSlotTypes {
        CLOTHES(new EnumFashionSlot[] { EnumFashionSlot.HEAD, EnumFashionSlot.CHEST, EnumFashionSlot.LEGS, EnumFashionSlot.BOOTS }),
        INHANDS(new EnumFashionSlot[] { EnumFashionSlot.WEAPON, EnumFashionSlot.SHIELD});

        private EnumFashionSlot[] list;
        private EnumFashionSlotTypes(EnumFashionSlot[] slots) {
            this.list = slots;
        }
        
        public EnumFashionSlot[] get()
        {
            return list;
        }
    }
}
