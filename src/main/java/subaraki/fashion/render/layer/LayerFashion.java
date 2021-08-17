package subaraki.fashion.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.render.EnumFashionSlot;
import subaraki.fashion.render.FashionModels;

import static subaraki.fashion.render.EnumFashionSlot.*;

public class LayerFashion extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerModel<AbstractClientPlayer> head = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.HEAD_MODEL_LOCATION), false);
    public PlayerModel<AbstractClientPlayer> bodySmall = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.SMALL_MODEL_LOCATION), false);
    public PlayerModel<AbstractClientPlayer> body = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.NORML_MODEL_LOCATION), false);
    public PlayerModel<AbstractClientPlayer> legs = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.LEGS_MODEL_LOCATION), false);
    public PlayerModel<AbstractClientPlayer> boots = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.BOOTS_MODEL_LOCATION), false);
    private PlayerModel<AbstractClientPlayer> model = null;

    {
        head.setAllVisible(false);
        bodySmall.setAllVisible(false);
        body.setAllVisible(false);
        legs.setAllVisible(false);
        boots.setAllVisible(false);

        head.head.visible = head.hat.visible =
                bodySmall.body.visible = bodySmall.jacket.visible = bodySmall.leftSleeve.visible = bodySmall.rightSleeve.visible = bodySmall.rightArm.visible = bodySmall.leftArm.visible =
                        body.body.visible = body.jacket.visible = body.leftSleeve.visible = body.rightSleeve.visible = body.rightArm.visible = body.leftArm.visible =
                                boots.leftLeg.visible = boots.rightLeg.visible = boots.leftPants.visible = boots.rightPants.visible =
                                        legs.leftLeg.visible = legs.rightLeg.visible = legs.rightPants.visible = legs.leftPants.visible = legs.body.visible = true;
    }

    public LayerFashion(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> entityrenderer) {

        super(entityrenderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, HEAD, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, CHEST, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, LEGS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, BOOTS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    private void renderFashionPart(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, EnumFashionSlot slot, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        FashionData.get(player).ifPresent(fashionData -> {

            ResourceLocation resLoc = fashionData.getRenderingPart(slot);

            if (resLoc == null || resLoc.toString().contains("missing"))
                return;

            model = getModelFromSlot(slot, player.getModelName().equals("slim"));

            if (model == null)
                return;

            this.getParentModel().copyPropertiesTo(model);

            //possible fix for mr crayfish's gun mod, where sleeves dont follow arm movement
            if (model.rightArm.visible) {
                model.rightArm.copyFrom(this.getParentModel().rightArm);
                model.rightSleeve.copyFrom(this.getParentModel().rightArm);
            }
            if (model.leftArm.visible) {
                model.leftArm.copyFrom(this.getParentModel().leftArm);
                model.leftSleeve.copyFrom(this.getParentModel().leftArm);
            }

            model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            model.setAllVisible(false);

            if (slot == HEAD) {
                model.head.visible = !player.isInvisible();
                model.hat.visible = !player.isInvisible();
            }

            if (slot == LEGS) {
                model.leftLeg.visible = !player.isInvisible();
                model.leftPants.visible = !player.isInvisible();
                model.rightLeg.visible = !player.isInvisible();
                model.rightPants.visible = !player.isInvisible();
                model.body.visible = !player.isInvisible();

            }

            if (slot == BOOTS) {
                model.leftLeg.visible = !player.isInvisible();
                model.leftPants.visible = !player.isInvisible();
                model.rightLeg.visible = !player.isInvisible();
                model.rightPants.visible = !player.isInvisible();
            }

            if (slot == CHEST) {
                model.body.visible = !player.isInvisible();
                model.jacket.visible = !player.isInvisible();

                model.leftArm.visible = !player.isInvisible();
                model.leftSleeve.visible = !player.isInvisible();

                model.rightArm.visible = !player.isInvisible();
                model.rightSleeve.visible = !player.isInvisible();
            }

            model.crouching = getParentModel().crouching;
            model.young = getParentModel().young;
            model.rightArmPose = getParentModel().rightArmPose;
            model.leftArmPose = getParentModel().leftArmPose;
            model.riding = getParentModel().riding;

            VertexConsumer bb = bufferIn.getBuffer(RenderType.entityTranslucent(resLoc));
            model.renderToBuffer(matrixStackIn, bb, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        });
    }

    private PlayerModel<AbstractClientPlayer> getModelFromSlot(EnumFashionSlot slot, boolean smallArms) {

        return switch (slot) {
            case HEAD -> head;
            case CHEST -> smallArms ? bodySmall : body;
            case LEGS -> legs;
            case BOOTS -> boots;
            default -> null;
        };
    }

}
