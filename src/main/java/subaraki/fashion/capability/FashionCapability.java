package subaraki.fashion.capability;

import java.util.concurrent.Callable;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class FashionCapability {

    /*
     * This field will contain the forge-allocated Capability class. This instance
     * will be initialized internally by Forge, upon calling register.
     */
    @CapabilityInject(FashionData.class)
    public static Capability<FashionData> CAPABILITY;

    /*
     * This registers our capability to the manager
     */
    public void register() {

        CapabilityManager.INSTANCE.register(

                // This is the class the capability works with
                FashionData.class,

                // This is a helper for users to save and load
                new StorageHelper(),

                // This is a factory for default instances
                new DefaultInstanceFactory());
    }

    /*
     * This class handles saving and loading the data.
     */
    public static class StorageHelper implements Capability.IStorage<FashionData> {

        @Override
        public INBT writeNBT(Capability<FashionData> capability, FashionData instance, Direction side) {

            return instance.writeData();
        }

        @Override
        public void readNBT(Capability<FashionData> capability, FashionData instance, Direction side, INBT nbt) {

            instance.readData(nbt);
        }
    }

    /*
     * This class handles constructing new instances for this capability
     */
    public static class DefaultInstanceFactory implements Callable<FashionData> {

        @Override
        public FashionData call() throws Exception {

            return new FashionData();
        }
    }
}
