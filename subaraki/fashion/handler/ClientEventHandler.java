package subaraki.fashion.handler;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.PacketOpenWardrobe;
import subaraki.fashion.proxy.ClientProxy;
import subaraki.fashion.render.layer.LayerAestheticHeldItem;
import subaraki.fashion.render.layer.LayerFashion;
import subaraki.fashion.render.layer.LayerWardrobe;

public class ClientEventHandler {

	public ClientEventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void stitchTextures(TextureStitchEvent.Pre event){
		Fashion.log.info("stitching weapon textures");

		stitch(ClientProxy.partsSize(EnumFashionSlot.WEAPON), EnumFashionSlot.WEAPON, event);
		stitch(ClientProxy.partsSize(EnumFashionSlot.SHIELD), EnumFashionSlot.SHIELD, event);

	}

	private void stitch(int size, EnumFashionSlot slot, TextureStitchEvent event){
		if(size > 1)
		{
			for(int partIndex = 1 ; partIndex < size; partIndex++)
				if(ClientProxy.getResourceForPart(slot, partIndex) != null)
				{
					ResourceLocation resLoc = ClientProxy.getTextureForStitcher(slot, partIndex);
					if(resLoc != null)
					{
						event.getMap().registerSprite(resLoc);
					}
					else
					{
						Fashion.log.warn("tried loading a null resourcelocation for weapons.");
						Fashion.log.warn(ClientProxy.getResourceForPart(slot, partIndex));
					}
				}
		}
	}

	private Field swap_field_layerrenders;
	private Object swap_list_layerrenders;

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
			
			//reflection 'swap' fields are volatile and will be set to null after rendering is done
			if(swap_field_layerrenders == null)
			{
				swap_field_layerrenders = ReflectionHelper.findField(RenderLivingBase.class, "layerRenderers","field_177097_h");
			}
			
			if(swap_list_layerrenders == null)
			{
				swap_list_layerrenders = swap_field_layerrenders.get(renderer);
			}

			//save original list. not volatile
			if(data.getSavedOriginalList().isEmpty())
				data.saveVanillaList((List<LayerRenderer>) swap_list_layerrenders);
			
			if(data.shouldRenderFashion()){
				if(data.cachedOriginalRenderList == null){
					data.cachedOriginalRenderList = (List<LayerRenderer>) swap_list_layerrenders;
					if(data.fashionLayers.isEmpty()){
						data.fashionLayers.clear();

						data.fashionLayers.add(new LayerFashion(renderer));
						data.fashionLayers.add(new LayerWardrobe(renderer));

						if(!data.keepLayers.isEmpty())
						{
							for(LayerRenderer layer : data.keepLayers)
							{
								data.fashionLayers.add(layer);
								Fashion.log.debug("Fashion had a render layer Injected.");
								Fashion.log.debug(layer.getClass().getName() + " got added.");
							}
						}

						data.fashionLayers.add(new LayerAestheticHeldItem(renderer));
						data.fashionLayers.add(new LayerArrow(renderer));
						data.fashionLayers.add(new LayerDeadmau5Head(renderer));
						data.fashionLayers.add(new LayerCape(renderer));
						data.fashionLayers.add(new LayerCustomHead(renderer.getMainModel().bipedHead));
						data.fashionLayers.add(new LayerElytra(renderer));

					}
					swap_field_layerrenders.set(renderer, data.fashionLayers);
				}
			}
			else{
				if(data.cachedOriginalRenderList != null){
					swap_field_layerrenders.set(renderer, data.cachedOriginalRenderList);
					data.cachedOriginalRenderList = null;
					data.resetSavedOriginalList();
				}
			}

			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		private void resetRenders(EntityPlayer player, RenderPlayer renderer){
			FashionData data = FashionData.get(player);
			try {
				if(swap_field_layerrenders == null)
					swap_field_layerrenders = ReflectionHelper.findField(RenderLivingBase.class, "layerRenderers","field_177097_h");
				if(swap_list_layerrenders == null)
					swap_list_layerrenders = swap_field_layerrenders.get(renderer);

				if(data.cachedOriginalRenderList != null){
					swap_field_layerrenders.set(renderer, data.cachedOriginalRenderList);
					data.cachedOriginalRenderList = null;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}