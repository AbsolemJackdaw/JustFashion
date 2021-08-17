package subaraki.fashion.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ModelFashionBody<T extends LivingEntity> extends ModelFashion<T> {

    public ModelFashionBody(float modelSize, boolean smallArms) {

        super(modelSize, 56, 32, smallArms);
        float offset = 0.0f;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
        this.body.setPos(0.0F, 0.0F + offset, 0.0F);

        this.bipedBodyWear = new ModelRenderer(this, 0, 16);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
        this.bipedBodyWear.setPos(0.0F, 0.0F, 0.0F);

        if (smallArms) {
            this.leftArm = new ModelRenderer(this, 24, 0);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.leftArm.setPos(5.0F, 2.5F, 0.0F);

            this.rightArm = new ModelRenderer(this, 40, 0);
            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.rightArm.setPos(-5.0F, 2.5F, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 24, 16);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
            this.bipedLeftArmwear.setPos(5.0F, 2.5F, 0.0F);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 16);
            this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
            this.bipedRightArmwear.setPos(-5.0F, 2.5F, 10.0F);
        } else {
            this.rightArm = new ModelRenderer(this, 24, 0);
            this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.rightArm.setPos(-5.0F, 2.0F + offset, 0.0F);

            this.leftArm = new ModelRenderer(this, 40, 0);
            this.leftArm.mirror = true;
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.leftArm.setPos(5.0F, 2.0F + offset, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 40, 16);
            this.bipedLeftArmwear.mirror = true;
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
            this.bipedLeftArmwear.setPos(5.0F, 2.0F, 0.0F);

            this.bipedRightArmwear = new ModelRenderer(this, 24, 16);
            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
            this.bipedRightArmwear.setPos(-5.0F, 2.0F, 10.0F);
        }

        this.head.visible = false;
        this.hat.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;

        this.bipedRightLegwear.visible = false;
        this.bipedLeftLegwear.visible = false;
    }
}