package subaraki.fashion.render.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import static net.minecraft.client.renderer.GlStateManager.*;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.model.ModelFashion;
import subaraki.fashion.model.ModelFashionBody;
import subaraki.fashion.model.ModelFashionBoots;
import subaraki.fashion.model.ModelFashionHead;
import subaraki.fashion.model.ModelFashionLegs;
import subaraki.fashion.proxy.ClientProxy;

public class LayerFashion implements LayerRenderer<AbstractClientPlayer>{

	private ModelFashionHead head = new ModelFashionHead(0.51f);
	private ModelFashionBody bodySmall = new ModelFashionBody(0.27f, true);
	private ModelFashionBody body = new ModelFashionBody(0.27f, false);
	private ModelFashionLegs legs = new ModelFashionLegs(0.26f);
	private ModelFashionBoots boots = new ModelFashionBoots(0.27f);

	private RenderPlayer renderer;

	public LayerFashion(RenderPlayer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		renderFashionPart(entitylivingbaseIn, 0, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		renderFashionPart(entitylivingbaseIn, 1, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		renderFashionPart(entitylivingbaseIn, 2, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		renderFashionPart(entitylivingbaseIn, 3, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	private void renderFashionPart(AbstractClientPlayer player, int slot, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale){
		
		FashionData fashionData = player.getCapability(FashionCapability.CAPABILITY,null);

		int id = fashionData.getPartIndex(slot);
		this.renderer.bindTexture(ClientProxy.getResourceForPart(slot, id));

		ModelFashion model = getModelFromSlot(slot, ((AbstractClientPlayer)player).getSkinType().equals("slim"));

		model.setModelAttributes(this.renderer.getMainModel());
		model.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
		
		model.isSneak = renderer.getMainModel().isSneak;
		model.isRiding = renderer.getMainModel().isRiding;
		model.isChild = renderer.getMainModel().isChild;
		model.rightArmPose = renderer.getMainModel().rightArmPose;
		model.leftArmPose = renderer.getMainModel().leftArmPose;

		model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		
	}

	private ModelFashion getModelFromSlot(int slot, boolean smallArms){
		if(slot == 0)
			return head;
		else if(slot == 1)
			return smallArms ? bodySmall : body;
		else if(slot == 2)
			return legs;
		else if(slot == 3)
			return boots;
		return null;
	}
}
