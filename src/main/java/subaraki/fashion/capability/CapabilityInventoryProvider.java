package subaraki.fashion.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import subaraki.fashion.mod.Fashion;

public class CapabilityInventoryProvider implements ICapabilitySerializable<CompoundTag> {

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
    public CapabilityInventoryProvider(Player player) {

        slots.setPlayer(player);
    }

    @Override
    public CompoundTag serializeNBT() {

        return (CompoundTag) slots.writeData();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        slots.readData(nbt);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {

        if (cap == FashionCapability.CAPABILITY)
            return (LazyOptional<T>) LazyOptional.of(this::getImpl);

        return LazyOptional.empty();
    }

    private FashionData getImpl() {

        return slots;
    }

}
