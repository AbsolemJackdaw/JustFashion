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

             model = getModelFromSlot(slot, ((AbstractClientPlayerEntity) player).getSkinType().equals("slim"));

             if(model == null)
                 return;
             
            this.getEntityModel().copyModelAttributesTo(model);
            model.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            model.setRotationAngles(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            model.setVisible(false);
            
            if (slot == HEAD)
            {
                model.bipedHead.showModel = !player.isInvisible();
                model.bipedHeadwear.showModel = !player.isInvisible();
            }

            if (slot == LEGS )
            {
                model.bipedLeftLeg.showModel = !player.isInvisible();
                model.bipedLeftLegwear.showModel = !player.isInvisible();
                model.bipedRightLeg.showModel = !player.isInvisible();
                model.bipedRightLegwear.showModel = !player.isInvisible();
                model.bipedBody.showModel = !player.isInvisible();

            }
            
            if(slot == BOOTS)
            {
                model.bipedLeftLeg.showModel = !player.isInvisible();
                model.bipedLeftLegwear.showModel = !player.isInvisible();
                model.bipedRightLeg.showModel = !player.isInvisible();
                model.bipedRightLegwear.showModel = !player.isInvisible();
            }

            if (slot == CHEST)
            {
                model.bipedBody.showModel = !player.isInvisible();
                model.bipedBodyWear.showModel = !player.isInvisible();

                model.bipedLeftArm.showModel = !player.isInvisible();
                model.bipedLeftArmwear.showModel = !player.isInvisible();

                model.bipedRightArm.showModel = !player.isInvisible();
                model.bipedRightArmwear.showModel = !player.isInvisible();
            }

            model.isSneak = getEntityModel().isSneak;
            model.isChild = getEntityModel().isChild;
            model.rightArmPose = getEntityModel().rightArmPose;
            model.leftArmPose = getEntityModel().leftArmPose;
            model.isSitting = getEntityModel().isSitting;

            IVertexBuilder bb = bufferIn.getBuffer(RenderType.getEntityTranslucent(resLoc));
            model.render(matrixStackIn, bb, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

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
