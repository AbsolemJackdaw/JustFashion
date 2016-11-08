package subaraki.fashion.model;

import net.minecraft.client.model.ModelRenderer;

public class ModelFashionBody extends ModelFashion{
	public ModelFashionBody(float modelSize) {
		super(modelSize, 0.0F, 40, 16);
		float offset = 0.0f;

		this.bipedBody = new ModelRenderer(this, 0, 0);
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
		this.bipedBody.setRotationPoint(0.0F, 0.0F + offset, 0.0F);

		this.bipedRightArm = new ModelRenderer(this, 24, 0);
		this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + offset, 0.0F);

		this.bipedLeftArm = new ModelRenderer(this, 24, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + offset, 0.0F);

		this.bipedHead.showModel = false;
		this.bipedHeadwear.showModel = false;
		this.bipedRightLeg.showModel = false;
		this.bipedLeftLeg.showModel = false;
	}
}