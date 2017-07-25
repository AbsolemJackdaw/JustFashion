package subaraki.fashion.model;

import net.minecraft.client.model.ModelRenderer;

public class ModelFashionBody extends ModelFashion{
	public ModelFashionBody(float modelSize, boolean smallArms) {
		super(modelSize, 56, 32, smallArms);
		float offset = 0.0f;

		this.bipedBody = new ModelRenderer(this, 0, 0);
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
		this.bipedBody.setRotationPoint(0.0F, 0.0F + offset, 0.0F);

		this.bipedBodyWear = new ModelRenderer(this, 0, 16);
		this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
		this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);

		if (smallArms)
		{
			this.bipedLeftArm = new ModelRenderer(this, 24, 0);
			this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);

			this.bipedRightArm = new ModelRenderer(this, 40, 0);
			this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);

			this.bipedLeftArmwear = new ModelRenderer(this, 24, 16);
			this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
			this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);

			this.bipedRightArmwear = new ModelRenderer(this, 40, 16);
			this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
			this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
		}
		else
		{
			this.bipedRightArm = new ModelRenderer(this, 24, 0);
			this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + offset, 0.0F);

			this.bipedLeftArm = new ModelRenderer(this, 40, 0);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + offset, 0.0F);

			this.bipedLeftArmwear = new ModelRenderer(this, 40, 16);
			this.bipedLeftArmwear.mirror = true;
			this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
			this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);

			this.bipedRightArmwear = new ModelRenderer(this, 24, 16);
			this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
			this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
		}

		this.bipedHead.showModel = false;
		this.bipedHeadwear.showModel = false;
		this.bipedRightLeg.showModel = false;
		this.bipedLeftLeg.showModel = false;
		
		this.bipedRightLegwear.showModel = false;
		this.bipedLeftLegwear.showModel = false;
	}
}