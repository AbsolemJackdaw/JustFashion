package subaraki.fashion.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ModelFashionBoots<T extends LivingEntity> extends ModelFashion<T> {

    public ModelFashionBoots(float modelSize) {

        super(modelSize, 32, 32, false);

        this.rightLeg = new ModelRenderer(this, 0, 0);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);

        this.leftLeg = new ModelRenderer(this, 16, 0);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);

        this.bipedLeftLegwear = new ModelRenderer(this, 16, 16);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftLegwear.setPos(1.9F, 12.0F, 0.0F);

        this.bipedRightLegwear = new ModelRenderer(this, 0, 16);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightLegwear.setPos(-1.9F, 12.0F, 0.0F);

        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.rightArm.visible = false;
        this.leftArm.visible = false;

        this.bipedBodyWear.visible = false;
        this.bipedRightArmwear.visible = false;
        this.bipedLeftArmwear.visible = false;
    }
}