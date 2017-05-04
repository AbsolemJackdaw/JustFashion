package subaraki.fashion.handler;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import lib.fashion.LayerInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.PacketOpenWardrobe;
import subaraki.fashion.proxy.ClientProxy;
import subaraki.fashion.render.layer.LayerFashion;
import subaraki.fashion.render.layer.LayerWardrobe;

public class ClientEventHandler {

	private List<LayerRenderer> fashionLayers = Lists.<LayerRenderer>newArrayList();

	public ClientEventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reloadTextures(TextureStitchEvent.Post event){
		Fashion.proxy.loadFashionPacks();
	}

	private Field field;
	private Object object;

	@SubscribeEvent
	public void renderPlayerPre(RenderPlayerEvent.Pre event){
		swapRenders(event.getEntityPlayer(), event.getRenderer());
	}

	@SubscribeEvent
	public void renderPlayerPost(RenderPlayerEvent.Post event){
		resetRenders(event.getEntityPlayer(), event.getRenderer());
	}
	
	@SubscribeEvent
	public void keyPressed(KeyInputEvent event){
		if(ClientProxy.keyWardrobe.isPressed()){
			NetworkHandler.NETWORK.sendToServer(new PacketOpenWardrobe());
			FashionData.get(Fashion.proxy.getClientPlayer()).setInWardrobe(true);
		}
	}
	
	/*=============================================================================================*/
	/*=============================================================================================*/
	/*=============================================================================================*/

	private void swapRenders(EntityPlayer player, RenderPlayer renderer){
		FashionData data = FashionData.get(player);

		try {
			if(field == null)
				field = ReflectionHelper.findField(RenderLivingBase.class, "layerRenderers","field_177097_h");
			if(object == null)
				object = field.get(renderer);

			if(data.shouldRenderFashion()){
				if(data.cachedOriginalRenderList == null){
					data.cachedOriginalRenderList = (List<LayerRenderer>) object;
					if(this.fashionLayers.isEmpty()){
						this.fashionLayers.add(new LayerHeldItem(renderer));
						this.fashionLayers.add(new LayerArrow(renderer));
						this.fashionLayers.add(new LayerDeadmau5Head(renderer));
						this.fashionLayers.add(new LayerCape(renderer));
						this.fashionLayers.add(new LayerCustomHead(renderer.getMainModel().bipedHead));
						this.fashionLayers.add(new LayerElytra(renderer));
						this.fashionLayers.add(new LayerFashion(renderer));
						this.fashionLayers.add(new LayerWardrobe(renderer));

						if(!LayerInjector.getExtraLayers().isEmpty())
						{
							for(LayerRenderer layer : LayerInjector.getExtraLayers())
							{
								this.fashionLayers.add(layer);
								Fashion.log.debug("Fashion had a render layer Injected.");
								Fashion.log.debug(layer.getClass().getName() + " got added.");
							}
						}
					}
					field.set(renderer, fashionLayers);
				}
			}
			else{
				if(data.cachedOriginalRenderList != null){
					field.set(renderer, data.cachedOriginalRenderList);
					data.cachedOriginalRenderList = null;
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void resetRenders(EntityPlayer player, RenderPlayer renderer){
		FashionData data = FashionData.get(player);
		try {
			if(field == null)
				field = ReflectionHelper.findField(RenderLivingBase.class, "layerRenderers","field_177097_h");
			if(object == null)
				object = field.get(renderer);

			if(data.cachedOriginalRenderList != null){
				field.set(renderer, data.cachedOriginalRenderList);
				data.cachedOriginalRenderList = null;
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}