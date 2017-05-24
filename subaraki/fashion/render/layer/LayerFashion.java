package subaraki.fashion.render.layer;

import static subaraki.fashion.mod.EnumFashionSlot.BOOTS;
import static subaraki.fashion.mod.EnumFashionSlot.CHEST;
import static subaraki.fashion.mod.EnumFashionSlot.HEAD;
import static subaraki.fashion.mod.EnumFashionSlot.LEGS;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
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
		renderFashionPart(entitylivingbaseIn, HEAD, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		renderFashionPart(entitylivingbaseIn, CHEST, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		renderFashionPart(entitylivingbaseIn, LEGS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		renderFashionPart(entitylivingbaseIn, BOOTS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	private void renderFashionPart(AbstractClientPlayer player, EnumFashionSlot slot, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale){

		FashionData fashionData = player.getCapability(FashionCapability.CAPABILITY,null);

		int id = fashionData.getPartIndex(slot);
		this.renderer.bindTexture(ClientProxy.getResourceForPart(slot, id));

		ModelFashion model = getModelFromSlot(slot, ((AbstractClientPlayer)player).getSkinType().equals("slim"));

		model.setModelAttributes(this.renderer.getMainModel());
		model.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);

		if(slot == EnumFashionSlot.HEAD)
		{
			model.bipedHead.isHidden = renderer.getMainModel().bipedHead.isHidden;
			model.bipedHeadwear.showModel = renderer.getMainModel().bipedHeadwear.showModel;
		}
		
		if(slot == EnumFashionSlot.LEGS || slot == EnumFashionSlot.BOOTS)
		{
			model.bipedLeftLeg.isHidden = renderer.getMainModel().bipedLeftLeg.isHidden;
			model.bipedLeftLegwear.isHidden = renderer.getMainModel().bipedLeftLegwear.isHidden;
			model.bipedRightLeg.isHidden = renderer.getMainModel().bipedRightLeg.isHidden;
			model.bipedRightLegwear.isHidden = renderer.getMainModel().bipedRightLegwear.isHidden;
		}
		
		if(slot == EnumFashionSlot.CHEST)
		{
			model.bipedBody.isHidden = renderer.getMainModel().bipedBody.isHidden;
			model.bipedBodyWear.isHidden = renderer.getMainModel().bipedBodyWear.isHidden;
			
			model.bipedLeftArm.isHidden = renderer.getMainModel().bipedLeftArm.isHidden;
			model.bipedLeftArmwear.isHidden = renderer.getMainModel().bipedLeftArmwear.isHidden;

			model.bipedRightArm.isHidden = renderer.getMainModel().bipedRightArm.isHidden;
			model.bipedRightArmwear.isHidden = renderer.getMainModel().bipedRightArmwear.isHidden;
		}

		model.isSneak = renderer.getMainModel().isSneak;
		model.isRiding = renderer.getMainModel().isRiding;
		model.isChild = renderer.getMainModel().isChild;
		model.rightArmPose = renderer.getMainModel().rightArmPose;
		model.leftArmPose = renderer.getMainModel().leftArmPose;

		model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

	}

	private ModelFashion getModelFromSlot(EnumFashionSlot slot, boolean smallArms){
		switch (slot) 
		{
		case HEAD:
			return head;
		case CHEST :
			return smallArms ? bodySmall : body;
		case LEGS : 
			return legs;
		case BOOTS :
			return boots;
		default :
			return null;
		}
	}
}
