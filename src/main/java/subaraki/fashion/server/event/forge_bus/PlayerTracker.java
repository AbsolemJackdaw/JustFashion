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
import subaraki.fashion.network.client.PacketSetWardrobeToTrackedClientPlayers;
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

    @SubscribeEvent
    public void clone(PlayerEvent.Clone event) {

        if (!event.isWasDeath())
            return;
        if (event.getPlayer() == null)
            return;
        if (event.getOriginal() == null)
            return;
        if (event.getPlayer().world.isRemote || event.getOriginal().world.isRemote)
            return;

        FashionData.get(event.getOriginal()).ifPresent(dataOriginal -> {
            FashionData.get(event.getPlayer()).ifPresent(data -> {
                data.readData(dataOriginal.writeData());
            });
        });
    }

    @SubscribeEvent
    public void join(PlayerEvent.PlayerRespawnEvent event) {

        if (event.getPlayer() == null)
            return;
        if (event.getPlayer().world.isRemote)
            return;

        toClient(event.getPlayer());
    }

    private void sync(PlayerEntity player) {

        if (!player.world.isRemote) {
            FashionData.get(player).ifPresent(data -> {
                
                NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new PacketSyncFashionToTrackedPlayers(data.getAllRenderedParts(),
                        data.shouldRenderFashion(), player.getUniqueID(), data.getSimpleNamesForToggledFashionLayers()));
                
                NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player),
                        new PacketSetWardrobeToTrackedClientPlayers(player.getUniqueID(), data.isInWardrobe()));
            });
        }
    }

    private void toClient(PlayerEntity player) {

        FashionData.get(player).ifPresent(data -> {
            Fashion.log.debug(data.keepLayersNamesForServer);
            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                    new PacketSyncFashionToClient(data.getAllRenderedParts(), data.keepLayersNamesForServer, data.shouldRenderFashion()));
        });
    }
}
