package subaraki.fashion.handler.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.fashion.capability.CapabilityInventoryProvider;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.PacketSyncFashionToClient;
import subaraki.fashion.network.PacketSyncFashionToTrackedPlayers;
import subaraki.fashion.render.layer.LayerWardrobe;

public class PlayerTracker {

    public PlayerTracker() {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void playerLogin(PlayerLoggedInEvent event) {

        if (!event.getPlayer().world.isRemote) {
            FashionData fashion = FashionData.get(event.getPlayer());

            if (event.getPlayer() instanceof ServerPlayerEntity)
                NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
                        new PacketSyncFashionToClient(fashion.getAllParts(), fashion.shouldRenderFashion()));

        } else {
            String types[] = new String[] { "default", "slim" };

            for (String type : types) {
                PlayerRenderer renderer = ((PlayerRenderer) Minecraft.getInstance().getRenderManager().getSkinMap().get(type));
                renderer.addLayer(new LayerWardrobe(renderer));
            }
        }
    }

    @SubscribeEvent
    public void playerDimensionChange(PlayerChangedDimensionEvent event) {

        if (!event.getPlayer().world.isRemote) {
            FashionData fashion = FashionData.get(event.getPlayer());
            if (event.getPlayer() instanceof ServerPlayerEntity)
                NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
                        new PacketSyncFashionToClient(fashion.getAllParts(), fashion.shouldRenderFashion()));
        }
    }

    @SubscribeEvent
    public void playerTracking(PlayerEvent.StartTracking event) {

        if (event.getTarget() instanceof PlayerEntity && event.getPlayer() != null)
            this.sync((PlayerEntity) event.getTarget());
    }

    private void sync(PlayerEntity player) {

        if (!player.world.isRemote) {
            FashionData fashion = FashionData.get(player);

            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player),
                    new PacketSyncFashionToTrackedPlayers(fashion.getAllParts(), fashion.shouldRenderFashion(), player.getUniqueID(), fashion.getSimpleNamesForToggledFashionLayers()));

        }
    }

    @SubscribeEvent
    public void onAttach(AttachCapabilitiesEvent<Entity> event) {

        final Object entity = event.getObject();

        if (entity instanceof PlayerEntity)
            event.addCapability(CapabilityInventoryProvider.KEY, new CapabilityInventoryProvider((PlayerEntity) entity));
    }
}
