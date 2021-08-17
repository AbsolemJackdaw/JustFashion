package subaraki.fashion.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import subaraki.fashion.mod.Fashion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientReferences {

    public static boolean isBlockTextureMap(TextureStitchEvent.Pre event) {

        return event.getMap().location().equals(InventoryMenu.BLOCK_ATLAS);
    }

    /**
     * Used in the packet when logging in to get the list of all layers before the
     * player is actually rendered
     */
    public static List<RenderLayer<?, ?>> tryList() throws IllegalArgumentException, IllegalAccessException {

        PlayerRenderer renderer = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().get("default"));
        Field swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingEntityRenderer.class, Fashion.obfLayerName);
        Object swap_list_layerrenders = swap_field_layerrenders.get(renderer);
        List<RenderLayer<?, ?>> list = new ArrayList<>();
        list = ((List<RenderLayer<?, ?>>) swap_list_layerrenders);

        return list;
    }

    public static Player getClientPlayerByUUID(UUID uuid) {
        return Minecraft.getInstance().level.getPlayerByUUID(uuid);
    }

}
