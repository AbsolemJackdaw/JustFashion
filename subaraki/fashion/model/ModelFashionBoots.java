package subaraki.fashion.model;

import net.minecraft.client.model.ModelRenderer;

public class ModelFashionBoots extends ModelFashion{

	public ModelFashionBoots(float modelSize) {
		super(modelSize, 0.0F, 16, 16);
		float offset = 0.0F;

		this.bipedRightLeg = new ModelRenderer(this, 0, 0);
		this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + offset, 0.0F);

		this.bipedLeftLeg = new ModelRenderer(this, 0, 0);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + offset, 0.0F);

		this.bipedHead.showModel = false;
		this.bipedHeadwear.showModel = false;
		this.bipedBody.showModel = false;
		this.bipedRightArm.showModel = false;
		this.bipedLeftArm.showModel = false;
	}
}