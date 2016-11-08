package subaraki.fashion.handler;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.RenderPlayerFashion;

public class ClientEventHandler {

	private Field field;
	private Object object;
	private Map<String, RenderPlayer> fashionSkinMap = Maps.<String, RenderPlayer>newHashMap();

	public ClientEventHandler() {
		MinecraftForge.EVENT_BUS.register(this);

		fashionSkinMap.put("default", new RenderPlayerFashion(Minecraft.getMinecraft().getRenderManager()));
		fashionSkinMap.put("slim", new RenderPlayerFashion(Minecraft.getMinecraft().getRenderManager(), true));

		try {
			field = ReflectionHelper.findField(RenderManager.class, "skinMap");
			field.set(Minecraft.getMinecraft().getRenderManager(), fashionSkinMap);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			
			Fashion.log.error("**********************");
			Fashion.log.error("!*!*!*!*!");
			Fashion.log.error("Could not replace the player renderer with a fashion sensible renderer !");
			Fashion.log.error("!*!*!*!*!");
			Fashion.log.error("**********************");

			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void reloadTextures(TextureStitchEvent.Post event){
		Fashion.proxy.loadFashionPacks();
	}
}