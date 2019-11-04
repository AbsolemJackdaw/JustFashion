package subaraki.fashion.server.event.forge_bus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import subaraki.fashion.capability.CapabilityInventoryProvider;

public class AttachEventHandler {

    @SubscribeEvent
    public void onAttach(AttachCapabilitiesEvent<Entity> event) {

        final Object entity = event.getObject();

        if (entity instanceof PlayerEntity)
            event.addCapability(CapabilityInventoryProvider.KEY, new CapabilityInventoryProvider((PlayerEntity) entity));
    }
}
