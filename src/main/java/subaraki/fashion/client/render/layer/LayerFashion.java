package subaraki.fashion.client.render.layer;

import static subaraki.fashion.mod.EnumFashionSlot.BOOTS;
import static subaraki.fashion.mod.EnumFashionSlot.CHEST;
import static subaraki.fashion.mod.EnumFashionSlot.HEAD;
import static subaraki.fashion.mod.EnumFashionSlot.LEGS;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.model.ModelFashion;
import subaraki.fashion.model.ModelFashionBody;
import subaraki.fashion.model.ModelFashionBoots;
import subaraki.fashion.model.ModelFashionHead;
import subaraki.fashion.model.ModelFashionLegs;

public class LayerFashion extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private ModelFashionHead<AbstractClientPlayerEntity> head = new ModelFashionHead<AbstractClientPlayerEntity>(0.51f);
    private ModelFashionBody<AbstractClientPlayerEntity> bodySmall = new ModelFashionBody<AbstractClientPlayerEntity>(0.27f, true);
    private ModelFashionBody<AbstractClientPlayerEntity> body = new ModelFashionBody<AbstractClientPlayerEntity>(0.27f, false);
    private ModelFashionLegs<AbstractClientPlayerEntity> legs = new ModelFashionLegs<AbstractClientPlayerEntity>(0.26f);
    private ModelFashionBoots<AbstractClientPlayerEntity> boots = new ModelFashionBoots<AbstractClientPlayerEntity>(0.27f);

    ModelFashion<AbstractClientPlayerEntity> model = null;
    
    public LayerFashion(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityrenderer) {

        super(entityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {

        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, HEAD, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, CHEST, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, LEGS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        renderFashionPart(matrixStackIn, bufferIn, packedLightIn, entityIn, BOOTS, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    private void renderFashionPart(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, EnumFashionSlot slot, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {

        FashionData.get(player).ifPresent(fashionData -> {

            ResourceLocation resLoc = fashionData.getRenderingPart(slot);

            if (resLoc == null || resLoc.toString().contains("missing"))
                return;

             model = getModelFromSlot(slot, ((AbstractClientPlayerEntity) player).getModelName().equals("slim"));

             if(model == null)
                 return;
             
            this.getParentModel().copyPropertiesTo(model);

            model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            model.setAllVisible(false);
            
            if (slot == HEAD)
            {
                model.head.visible = !player.isInvisible();
                model.hat.visible = !player.isInvisible();
            }

            if (slot == LEGS )
            {
                model.leftLeg.visible = !player.isInvisible();
                model.bipedLeftLegwear.visible = !player.isInvisible();
                model.rightLeg.visible = !player.isInvisible();
                model.bipedRightLegwear.visible = !player.isInvisible();
                model.body.visible = !player.isInvisible();

            }
            
            if(slot == BOOTS)
            {
                model.leftLeg.visible = !player.isInvisible();
                model.bipedLeftLegwear.visible = !player.isInvisible();
                model.rightLeg.visible = !player.isInvisible();
                model.bipedRightLegwear.visible = !player.isInvisible();
            }

            if (slot == CHEST)
            {
                model.body.visible = !player.isInvisible();
                model.bipedBodyWear.visible = !player.isInvisible();

                model.leftArm.visible = !player.isInvisible();
                model.bipedLeftArmwear.visible = !player.isInvisible();

                model.rightArm.visible = !player.isInvisible();
                model.bipedRightArmwear.visible = !player.isInvisible();
            }

            model.crouching = getParentModel().crouching;
            model.young = getParentModel().young;
            model.rightArmPose = getParentModel().rightArmPose;
            model.leftArmPose = getParentModel().leftArmPose;
            model.riding = getParentModel().riding;

            IVertexBuilder bb = bufferIn.getBuffer(RenderType.entityTranslucent(resLoc));
            model.renderToBuffer(matrixStackIn, bb, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        });
    }

    private ModelFashion<AbstractClientPlayerEntity> getModelFromSlot(EnumFashionSlot slot, boolean smallArms)
    {

        switch (slot)
        {
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
