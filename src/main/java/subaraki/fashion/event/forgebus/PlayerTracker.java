package subaraki.fashion.event.forgebus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSetWardrobeToTrackedClientPlayers;
import subaraki.fashion.network.client.PacketSyncFashionToClient;
import subaraki.fashion.network.client.PacketSyncFashionToTrackedPlayers;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerTracker {

    @SubscribeEvent
    public static void playerLogin(PlayerLoggedInEvent event) {

        if (!event.getPlayer().level.isClientSide) {
            toClient(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void playerDimensionChange(PlayerChangedDimensionEvent event) {

        if (!event.getPlayer().level.isClientSide) {
            toClient(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void playerTracking(PlayerEvent.StartTracking event) {

        if (event.getTarget() instanceof Player && event.getPlayer() != null)
            sync((Player) event.getTarget());
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {

        if (!event.isWasDeath())
            return;
        if (event.getPlayer() == null)
            return;
        if (event.getOriginal() == null)
            return;
        if (event.getPlayer().level.isClientSide || event.getOriginal().level.isClientSide)
            return;

        event.getOriginal().reviveCaps();
        FashionData.get(event.getOriginal()).ifPresent(dataOriginal -> {
            FashionData.get(event.getPlayer()).ifPresent(data -> {
                data.readData(dataOriginal.writeData());
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void join(PlayerEvent.PlayerRespawnEvent event) {

        if (event.getPlayer() == null)
            return;
        if (event.getPlayer().level.isClientSide)
            return;

        toClient(event.getPlayer());
    }

    private static void sync(Player player) {

        if (!player.level.isClientSide) {
            FashionData.get(player).ifPresent(data -> {

                NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new PacketSyncFashionToTrackedPlayers(data.getAllRenderedParts(),
                        data.shouldRenderFashion(), player.getUUID(), data.getKeepLayerNames()));

                NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player),
                        new PacketSetWardrobeToTrackedClientPlayers(player.getUUID(), data.isInWardrobe()));
            });
        }
    }

    private static void toClient(Player player) {

        FashionData.get(player).ifPresent(data -> {
            Fashion.log.debug(data.getKeepLayerNames());
            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PacketSyncFashionToClient(data.getAllRenderedParts(), data.getKeepLayerNames(), data.shouldRenderFashion()));
        });
    }
}
