package subaraki.fashion.model;

import net.minecraft.client.model.ModelRenderer;

public class ModelFashionLegs extends ModelFashion{

	public ModelFashionLegs(float modelSize) {
		super(modelSize, 0.0F, 40, 16);
		float offset = 0.0f;

		this.bipedBody = new ModelRenderer(this, 16, 0);
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
		this.bipedBody.setRotationPoint(0.0F, 0.0F + offset, 0.0F);

		this.bipedRightLeg = new ModelRenderer(this, 0, 0);
		this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + offset, 0.0F);

		this.bipedLeftLeg = new ModelRenderer(this, 0, 0);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + offset, 0.0F);

		this.bipedHead.showModel = false;
		this.bipedHeadwear.showModel = false;
		this.bipedRightArm.showModel = false;
		this.bipedLeftArm.showModel = false;
	}
}