package subaraki.fashion.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ModelFashion<T extends LivingEntity> extends BipedModel<T> {

    public ModelRenderer bipedLeftArmwear;
    public ModelRenderer bipedRightArmwear;
    public ModelRenderer bipedLeftLegwear;
    public ModelRenderer bipedRightLegwear;
    public ModelRenderer bipedBodyWear;

    private final boolean smallArms;

    public ModelFashion(float modelSize, int textureWidthIn, int textureHeightIn, boolean smallArms) {

        super(modelSize, 0.0F, textureWidthIn, textureHeightIn);
        this.smallArms = smallArms;

        this.bipedLeftArmwear = new ModelRenderer(this, 0, 0);
        this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftArmwear.setPos(5.0F, 2.0F, 0.0F);

        this.bipedRightArmwear = new ModelRenderer(this, 0, 0);
        this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightArmwear.setPos(-5.0F, 2.0F, 10.0F);

        this.bipedLeftLegwear = new ModelRenderer(this, 0, 0);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftLegwear.setPos(1.9F, 12.0F, 0.0F);

        this.bipedRightLegwear = new ModelRenderer(this, 0, 0);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightLegwear.setPos(-1.9F, 12.0F, 0.0F);

        this.bipedBodyWear = new ModelRenderer(this, 0, 0);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
        this.bipedBodyWear.setPos(0.0F, 0.0F, 0.0F);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used
     * for animating the movement of arms and legs, where par1 represents the
     * time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.bipedLeftLegwear.copyFrom(leftLeg);
        this.bipedRightLegwear.copyFrom(rightLeg);
        this.bipedLeftArmwear.copyFrom(leftArm);
        this.bipedRightArmwear.copyFrom(rightArm);
        this.bipedBodyWear.copyFrom(body);
    }

    @Override
    public void setAllVisible(boolean visible) {

        super.setAllVisible(visible);
        this.bipedLeftArmwear.visible = visible;
        this.bipedRightArmwear.visible = visible;
        this.bipedLeftLegwear.visible = visible;
        this.bipedRightLegwear.visible = visible;
        this.bipedBodyWear.visible = visible;
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.bipedLeftLegwear, this.bipedRightLegwear, this.bipedLeftArmwear, this.bipedRightArmwear, this.bipedBodyWear));
    }
}