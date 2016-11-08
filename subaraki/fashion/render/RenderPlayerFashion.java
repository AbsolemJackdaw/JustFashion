package subaraki.fashion.render;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.render.layer.LayerFashion;

public class RenderPlayerFashion extends RenderPlayer{

	protected List<LayerRenderer> fashionLayers = Lists.<LayerRenderer>newArrayList();
	
	public RenderPlayerFashion(RenderManager renderManager) {
		this(renderManager, false);
	}

	public RenderPlayerFashion(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
		this.fashionLayers.add(new LayerHeldItem(this));
		this.fashionLayers.add(new LayerArrow(this));
		this.fashionLayers.add(new LayerDeadmau5Head(this));
		this.fashionLayers.add(new LayerCape(this));
		this.fashionLayers.add(new LayerCustomHead(this.getMainModel().bipedHead));
		this.fashionLayers.add(new LayerElytra(this));
		this.fashionLayers.add(new LayerFashion(this));
	}

	@Override
	protected void renderLayers(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
		FashionData fashion = FashionData.get(entitylivingbaseIn);

		if(fashion.shouldRenderFashion()){
			for (LayerRenderer layerrenderer : this.fashionLayers)
			{
				boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
				layerrenderer.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);

				if (flag)
				{
					this.unsetBrightness();
				}
			}
		}else
			super.renderLayers(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch,
					scaleIn);
	}
}
