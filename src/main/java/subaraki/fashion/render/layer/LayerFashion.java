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

    private final PlayerModel<AbstractClientPlayer> head = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.HEAD_MODEL_LOCATION), false);
    private final PlayerModel<AbstractClientPlayer> bodySmall = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.SMALL_MODEL_LOCATION), true);
    private final PlayerModel<AbstractClientPlayer> body = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.NORML_MODEL_LOCATION), false);
    private final PlayerModel<AbstractClientPlayer> legs = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.LEGS_MODEL_LOCATION), false);
    private final PlayerModel<AbstractClientPlayer> boots = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FashionModels.BOOTS_MODEL_LOCATION), false);

    {
        setVisibility(head, HEAD, true);
        setVisibility(body, CHEST, true);
        setVisibility(bodySmall, CHEST, true);
        setVisibility(legs, LEGS, true);
        setVisibility(boots, BOOTS, true);
    }

    public LayerFashion(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> entityrenderer) {

        super(entityrenderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        for (EnumFashionSlot slot : EnumFashionSlot.values())
            if (slot != SHIELD && slot != WEAPON) //ensure the only slots accessed are the clothing slots
                renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, slot, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    private void renderFashionPart(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, EnumFashionSlot slot, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        FashionData.get(player).ifPresent(fashionData -> {
            ResourceLocation resLoc = fashionData.getRenderingPart(slot);
            PlayerModel<AbstractClientPlayer> model = getModelFromSlot(slot, player.getModelName().equals("slim"));
            this.getParentModel().copyPropertiesTo(model);
            setVisibility(model, slot, !player.isInvisible());
            model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityTranslucent(resLoc));
            model.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
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

    private void setVisibility(PlayerModel<AbstractClientPlayer> model, EnumFashionSlot slot, boolean invisible) {
        model.setAllVisible(false);

        switch (slot) {
            case HEAD -> {
                model.head.visible = invisible;
                model.hat.visible = invisible;
            }
            case CHEST -> {
                model.body.visible = invisible;
                model.jacket.visible = invisible;
                model.leftArm.visible = invisible;
                model.leftSleeve.visible = invisible;
                model.rightArm.visible = invisible;
                model.rightSleeve.visible = invisible;
            }
            case LEGS -> {
                model.leftLeg.visible = invisible;
                model.leftPants.visible = invisible;
                model.rightLeg.visible = invisible;
                model.rightPants.visible = invisible;
                model.body.visible = invisible;
            }
            case BOOTS -> {
                model.leftLeg.visible = invisible;
                model.leftPants.visible = invisible;
                model.rightLeg.visible = invisible;
                model.rightPants.visible = invisible;
            }
        }
    }
}
