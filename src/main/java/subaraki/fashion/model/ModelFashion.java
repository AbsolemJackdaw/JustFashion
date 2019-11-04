package subaraki.fashion.model;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.HandSide;

public class ModelFashion extends BipedModel<LivingEntity> {

    public RendererModel bipedLeftArmwear;
    public RendererModel bipedRightArmwear;
    public RendererModel bipedLeftLegwear;
    public RendererModel bipedRightLegwear;
    public RendererModel bipedBodyWear;

    private final boolean smallArms;

    public ModelFashion(float modelSize, int textureWidthIn, int textureHeightIn, boolean smallArms) {

        super(modelSize, 0.0F, textureWidthIn, textureHeightIn);
        this.smallArms = smallArms;

        this.bipedLeftArmwear = new RendererModel(this, 0, 0);
        this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);

        this.bipedRightArmwear = new RendererModel(this, 0, 0);
        this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);

        this.bipedLeftLegwear = new RendererModel(this, 0, 0);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);

        this.bipedRightLegwear = new RendererModel(this, 0, 0);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);

        this.bipedBodyWear = new RendererModel(this, 0, 0);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public static void copyModelAngles(RendererModel source, RendererModel dest) {

        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    public void setModelAttributes(BipedModel<AbstractClientPlayerEntity> bipedModel) {

        this.swingProgress = bipedModel.swingProgress;
        this.isChild = bipedModel.isChild;
        // no more riding field ? used to be here ...
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */

    @Override
    public void render(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GlStateManager.pushMatrix();
        if (entityIn.getPose().equals(Pose.SNEAKING)) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
        }

        if (this.bipedLeftLegwear != null) {
            this.bipedLeftLegwear.render(scale);
            this.bipedRightLegwear.render(scale);
        }
        if (this.bipedLeftArmwear != null) {
            this.bipedLeftArmwear.render(scale);
            this.bipedRightArmwear.render(scale);
            this.bipedBodyWear.render(scale);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used
     * for animating the movement of arms and legs, where par1 represents the
     * time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */

    @Override
    public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {

        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
        copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
        copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
        copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyModelAngles(this.bipedBody, this.bipedBodyWear);
    }

    @Override
    public void setVisible(boolean visible) {

        super.setVisible(visible);
        this.bipedLeftArmwear.showModel = visible;
        this.bipedRightArmwear.showModel = visible;
        this.bipedLeftLegwear.showModel = visible;
        this.bipedRightLegwear.showModel = visible;
        this.bipedBodyWear.showModel = visible;
    }

    public void postRenderArm(float scale, HandSide side) {

        RendererModel renderer = this.getArmForSide(side);

        if (this.smallArms) {
            float f = 0.5F * (float) (side == HandSide.RIGHT ? 1 : -1);
            renderer.rotationPointX += f;
            renderer.postRender(scale);
            renderer.rotationPointX -= f;
        } else {
            renderer.postRender(scale);
        }
    }
}