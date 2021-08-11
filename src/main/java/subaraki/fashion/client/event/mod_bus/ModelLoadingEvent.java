package subaraki.fashion.client.event.mod_bus;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class ModelLoadingEvent {

    @SubscribeEvent
    public static void registerItemModel(ModelRegistryEvent event)
    {

        new ResourcePackReader();

        ResourceLocation wardrobe = new ResourceLocation("fashion:wardrobe");
        ModelLoader.addSpecialModel(wardrobe);

        Fashion.log.debug("Firing Model Registry Event");
        int size_weapons = ResourcePackReader.getWeaponSize();
        int size_shields = ResourcePackReader.getShieldSize();

        Fashion.log.debug("Weapons to load : " + size_weapons);
        Fashion.log.debug("Shields to load : " + size_shields);

        List<ResourceLocation> theList = Lists.newArrayList();

        theList = ResourcePackReader.getListForSlot(EnumFashionSlot.WEAPON);
        for (ResourceLocation resLoc : theList)
        {

            Fashion.log.debug(String.format("Weapon Registry %s", resLoc));
            ModelLoader.addSpecialModel(resLoc);
        }

        theList = ResourcePackReader.getListForSlot(EnumFashionSlot.SHIELD);
        for (ResourceLocation resLoc : theList)
        {
            Fashion.log.debug(String.format("ShieldRegistry %s", resLoc));

            for(int i = 0; i < 2; i++)
            {
             boolean blocking = i == 0;
                ModelLoader.addSpecialModel(ResourcePackReader.getAestheticShield(resLoc, blocking));
            }
        }
    }
}
