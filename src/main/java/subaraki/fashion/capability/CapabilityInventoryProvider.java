package subaraki.fashion.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import subaraki.fashion.mod.Fashion;

public class CapabilityInventoryProvider implements ICapabilitySerializable<CompoundNBT> {

    /**
     * Unique key to identify the attached provider from others
     */
    public static final ResourceLocation KEY = new ResourceLocation(Fashion.MODID, "fashion_cap");

    /**
     * The instance that we are providing
     */
    final FashionData slots = new FashionData();

    /**
     * Gets called before world is initiated. player.worldObj will return null here
     * !
     */
    public CapabilityInventoryProvider(PlayerEntity player) {

        slots.setPlayer(player);
    }

    @Override
    public CompoundNBT serializeNBT() {

        return (CompoundNBT) FashionCapability.CAPABILITY.writeNBT(slots, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

        FashionCapability.CAPABILITY.readNBT(slots, null, nbt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {

        if (cap == FashionCapability.CAPABILITY)
            return (LazyOptional<T>) LazyOptional.of(this::getImpl);

        return LazyOptional.empty();
    }

    private FashionData getImpl() {

        if (slots != null) {
            return slots;
        }
        return new FashionData();
    }

}
