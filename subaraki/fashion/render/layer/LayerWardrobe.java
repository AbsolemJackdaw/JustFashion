package subaraki.fashion.render.layer;

import lib.modelloader.ModelHandle;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;

public class LayerWardrobe implements LayerRenderer<AbstractClientPlayer>{

	private ModelHandle modelHandle = ModelHandle.of(new ResourceLocation(Fashion.MODID,"wardrobe"));
	private ModelHandle modelHandleTiny = ModelHandle.of(new ResourceLocation(Fashion.MODID,"tinywardrobe"));

	private RenderPlayer renderer;

	public LayerWardrobe(RenderPlayer renderer){
		this.renderer = renderer;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

		FashionData fashion = FashionData.get(entitylivingbaseIn);
		
		if(fashion.isInWardrobe()){
			GlStateManager.pushMatrix();
			scale = 1.2f;
			renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.translate(-0.6, 1.5, 0.5);
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.scale(scale, scale, scale);
			modelHandle.render();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}