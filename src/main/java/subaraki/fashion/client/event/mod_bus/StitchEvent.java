package subaraki.fashion.client.event.mod_bus;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import subaraki.fashion.client.ClientReferences;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;

@EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT)
public class StitchEvent {

    @SubscribeEvent
    public static void stitchTextures(TextureStitchEvent.Pre event) {

        if (ClientReferences.isBlockTextureMap(event))
            return;

        Fashion.log.debug("stitching weapon textures");

        stitch(EnumFashionSlot.WEAPON, event);
        stitch(EnumFashionSlot.SHIELD, event);

    }

    private static void stitch(EnumFashionSlot slot, TextureStitchEvent.Pre event) {

        for (ResourceLocation resLoc : ResourcePackReader.getTexturesForStitcher(slot))
            if (resLoc != null) {
                event.addSprite(resLoc);
                Fashion.log.info("stitched " + resLoc.toString());
            } else {
                Fashion.log.warn(String.format(
                        "%n SKIPPED STITCHING A NULL %s RESOURCE %n This is normal only once, and for shields. %n Check your fashion resourcepack json for any path errors if it happens more then once ! ",
                        slot.name()));
            }
    }

}
