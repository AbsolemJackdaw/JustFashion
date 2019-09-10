package subaraki.fashion.mod;

public enum EnumFashionSlot {

    HEAD, CHEST, LEGS, BOOTS, WEAPON, SHIELD;

    public static EnumFashionSlot fromInt(int id) {

        for (EnumFashionSlot SLOT : EnumFashionSlot.values()) {
            if (SLOT.ordinal() == id)
                return SLOT;
        }

        Fashion.log.error("Resorted to Default HEAD for fashion slot lookup. This is an error and should not happen. ");

        return HEAD;
    }
}
