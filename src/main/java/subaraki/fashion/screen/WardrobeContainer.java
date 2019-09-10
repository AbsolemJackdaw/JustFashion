package subaraki.fashion.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class WardrobeContainer extends Container {

    public WardrobeContainer(ContainerType<?> type, int id) {

        super(type, id);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {

        return true;
    }
}
