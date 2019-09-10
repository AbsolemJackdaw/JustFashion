package subaraki.fashion.render.layer;

import static subaraki.fashion.mod.EnumFashionSlot.BOOTS;
import static subaraki.fashion.mod.EnumFashionSlot.CHEST;
import static subaraki.fashion.mod.EnumFashionSlot.HEAD;
import static subaraki.fashion.mod.EnumFashionSlot.LEGS;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.handler.client.ResourcePackReader;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.model.ModelFashion;
import subaraki.fashion.model.ModelFashionBody;
import subaraki.fashion.model.ModelFashionBoots;
import subaraki.fashion.model.ModelFashionHead;
import subaraki.fashion.model.ModelFashionLegs;

public class LayerFashion extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private ModelFashionHead head = new ModelFashionHead(0.51f);
    private ModelFashionBody bodySmall = new ModelFashionBody(0.27f, true);
    private ModelFashionBody body = new ModelFashionBody(0.27f, false);
    private ModelFashionLegs legs = new ModelFashionLegs(0.26f);
    private ModelFashionBoots boots = new ModelFashionBoots(0.27f);

    public LayerFashion(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityrenderer) {

        super(entityrenderer);
    }

    @Override
    public void render(AbstractClientPlayerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        renderFashionPart(entityIn, HEAD, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        renderFashionPart(entityIn, CHEST, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        renderFashionPart(entityIn, LEGS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        renderFashionPart(entityIn, BOOTS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {

        return true;
    }

    private void renderFashionPart(AbstractClientPlayerEntity player, EnumFashionSlot slot, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        FashionData fashionData = FashionData.get(player);

        int id = fashionData.getPartIndex(slot);

        this.bindTexture(ResourcePackReader.getResourceForPart(slot, id));

        ModelFashion model = getModelFromSlot(slot, ((AbstractClientPlayerEntity) player).getSkinType().equals("slim"));

        model.setModelAttributes(this.getEntityModel());
        model.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);

        if (slot == EnumFashionSlot.HEAD) {
            model.bipedHead.isHidden = getEntityModel().bipedHead.isHidden;
            model.bipedHeadwear.isHidden = getEntityModel().bipedHeadwear.isHidden;
        }

        if (slot == EnumFashionSlot.LEGS || slot == EnumFashionSlot.BOOTS) {
            model.bipedLeftLeg.isHidden = getEntityModel().bipedLeftLeg.isHidden;
            model.bipedLeftLegwear.isHidden = getEntityModel().bipedLeftLegwear.isHidden;
            model.bipedRightLeg.isHidden = getEntityModel().bipedRightLeg.isHidden;
            model.bipedRightLegwear.isHidden = getEntityModel().bipedRightLegwear.isHidden;
        }

        if (slot == EnumFashionSlot.CHEST) {
            model.bipedBody.isHidden = getEntityModel().bipedBody.isHidden;
            model.bipedBodyWear.isHidden = getEntityModel().bipedBodyWear.isHidden;

            model.bipedLeftArm.isHidden = getEntityModel().bipedLeftArm.isHidden;
            model.bipedLeftArmwear.isHidden = getEntityModel().bipedLeftArmwear.isHidden;

            model.bipedRightArm.isHidden = getEntityModel().bipedRightArm.isHidden;
            model.bipedRightArmwear.isHidden = getEntityModel().bipedRightArmwear.isHidden;
        }

        model.isSneak = getEntityModel().isSneak;
        // model.isRiding = renderer.getEntityModel().isRiding; //no more riding
        // apparently
        model.isChild = getEntityModel().isChild;
        model.rightArmPose = getEntityModel().rightArmPose;
        model.leftArmPose = getEntityModel().leftArmPose;

        model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private ModelFashion getModelFromSlot(EnumFashionSlot slot, boolean smallArms) {

        switch (slot) {
        case HEAD:
            return head;
        case CHEST:
            return smallArms ? bodySmall : body;
        case LEGS:
            return legs;
        case BOOTS:
            return boots;
        default:
            return null;
        }
    }

}
