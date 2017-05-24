package subaraki.fashion.handler;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.proxy.ClientProxy;

public class StitchHandler {

	public StitchHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void stitchEvent(TextureStitchEvent event){
		int size = ClientProxy.partsSize(EnumFashionSlot.WEAPON);
		
		if(size <= 1)//skip the blank sword
			return;
		
		for(int i = 1 ; i < size ; i++)
			event.getMap().registerSprite(ClientProxy.getTextureForStitcher(EnumFashionSlot.WEAPON, i));
	}

}
