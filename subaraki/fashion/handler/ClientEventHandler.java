package subaraki.fashion.handler;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.layer.LayerFashion;

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

		FashionData data = FashionData.get(event.getEntityPlayer());

		try {
			if(field == null)
				field = ReflectionHelper.findField(RenderLivingBase.class, "layerRenderers","field_177097_h");
			if(object == null)
				object = field.get(event.getRenderer());

			if(data.shouldRenderFashion()){
				if(data.cachedOriginalRenderList == null){
					data.cachedOriginalRenderList = (List<LayerRenderer>) object;
					if(this.fashionLayers.isEmpty()){
						this.fashionLayers.add(new LayerHeldItem(event.getRenderer()));
						this.fashionLayers.add(new LayerArrow(event.getRenderer()));
						this.fashionLayers.add(new LayerDeadmau5Head(event.getRenderer()));
						this.fashionLayers.add(new LayerCape(event.getRenderer()));
						this.fashionLayers.add(new LayerCustomHead(event.getRenderer().getMainModel().bipedHead));
						this.fashionLayers.add(new LayerElytra(event.getRenderer()));
						this.fashionLayers.add(new LayerFashion(event.getRenderer()));
					}
					field.set(event.getRenderer(), fashionLayers);
				}
			}
			else{
				if(data.cachedOriginalRenderList != null){
					field.set(event.getRenderer(), data.cachedOriginalRenderList);
					data.cachedOriginalRenderList = null;
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void renderPlayerPost(RenderPlayerEvent.Post event){
		FashionData data = FashionData.get(event.getEntityPlayer());
		try {
			if(field == null)
				field = ReflectionHelper.findField(RenderLivingBase.class, "layerRenderers","field_177097_h");
			if(object == null)
				object = field.get(event.getRenderer());

			if(data.cachedOriginalRenderList != null){
				field.set(event.getRenderer(), data.cachedOriginalRenderList);
				data.cachedOriginalRenderList = null;
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}