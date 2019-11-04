package subaraki.fashion.server.event.forge_bus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSyncFashionToClient;
import subaraki.fashion.network.client.PacketSyncFashionToTrackedPlayers;

public class PlayerTracker {

    @SubscribeEvent
    public void playerLogin(PlayerLoggedInEvent event) {

        if (!event.getPlayer().world.isRemote) {
            toClient(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void playerDimensionChange(PlayerChangedDimensionEvent event) {

        if (!event.getPlayer().world.isRemote) {
            toClient(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void playerTracking(PlayerEvent.StartTracking event) {

        if (event.getTarget() instanceof PlayerEntity && event.getPlayer() != null)
            this.sync((PlayerEntity) event.getTarget());
    }

    private void sync(PlayerEntity player) {

        if (!player.world.isRemote) {
            FashionData.get(player).ifPresent(data -> {
                NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new PacketSyncFashionToTrackedPlayers(data.getAllParts(),
                        data.shouldRenderFashion(), player.getUniqueID(), data.getSimpleNamesForToggledFashionLayers()));

            });

        }
    }

    private void toClient(PlayerEntity player) {

        FashionData.get(player).ifPresent(data -> {
            Fashion.log.debug(data.keepLayersNamesForServer);
            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                    new PacketSyncFashionToClient(data.getAllParts(), data.keepLayersNamesForServer, data.shouldRenderFashion()));
        });
    }
}
