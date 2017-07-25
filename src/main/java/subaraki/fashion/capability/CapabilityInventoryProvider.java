package subaraki.fashion.capability;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import subaraki.fashion.mod.Fashion;

public class CapabilityInventoryProvider implements ICapabilitySerializable<NBTTagCompound>
{
    /**
     * Unique key to identify the attached provider from others
     */
    public static final ResourceLocation KEY = new ResourceLocation(Fashion.MODID, "fashion");

    /**
     * The instance that we are providing
     */
    final FashionData slots = new FashionData();

    /**gets called before world is initiated. player.worldObj will return null here !*/
    public CapabilityInventoryProvider(EntityPlayer player){
        slots.setPlayer(player);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == FashionCapability.CAPABILITY)
            return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
        if (capability == FashionCapability.CAPABILITY)
            return (T)slots;
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT(){
        return (NBTTagCompound) FashionCapability.CAPABILITY.writeNBT(slots, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt){
    	FashionCapability.CAPABILITY.readNBT(slots, null, nbt);
    }
}
