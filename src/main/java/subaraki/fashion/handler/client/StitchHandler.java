package subaraki.fashion.handler.client;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;

@EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class StitchHandler {

    // texture stitch is for the mod registry !
    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre event) {

        Fashion.log.info("stitching weapon textures");

        stitch(ResourcePackReader.partsSize(EnumFashionSlot.WEAPON), EnumFashionSlot.WEAPON, event);
        stitch(ResourcePackReader.partsSize(EnumFashionSlot.SHIELD), EnumFashionSlot.SHIELD, event);

    }

    private void stitch(int size, EnumFashionSlot slot, TextureStitchEvent.Pre event) {

        if (size > 1) {
            for (int partIndex = 1; partIndex < size; partIndex++)
                if (ResourcePackReader.getResourceForPart(slot, partIndex) != null) {
                    ResourceLocation resLoc = ResourcePackReader.getTextureForStitcher(slot, partIndex);
                    if (resLoc != null) {
                        event.addSprite(resLoc);
                    } else {
                        Fashion.log.warn("tried loading a null resourcelocation for weapons.");
                        Fashion.log.warn(ResourcePackReader.getResourceForPart(slot, partIndex));
                    }
                }
        }
    }

}
