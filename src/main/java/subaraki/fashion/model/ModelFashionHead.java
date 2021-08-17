package subaraki.fashion.model;

import net.minecraft.entity.LivingEntity;

public class ModelFashionHead<T extends LivingEntity> extends ModelFashion<T> {

    public ModelFashionHead(float modelSize) {

        super(modelSize, 64, 16, false);

        this.body.visible = false;
        this.rightArm.visible = false;
        this.leftArm.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;

        this.bipedBodyWear.visible = false;
        this.bipedRightArmwear.visible = false;
        this.bipedLeftArmwear.visible = false;
        this.bipedRightLegwear.visible = false;
        this.bipedLeftLegwear.visible = false;
    }
}