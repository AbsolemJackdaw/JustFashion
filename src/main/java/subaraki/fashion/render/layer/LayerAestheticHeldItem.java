package subaraki.fashion.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;
import net.minecraftforge.client.ForgeHooksClient;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.render.EnumFashionSlot;
import subaraki.fashion.util.ResourcePackReader;

public class LayerAestheticHeldItem extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final TransformType cam_right = ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    private final TransformType cam_left = ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND;

    public LayerAestheticHeldItem(PlayerRenderer renderer) {

        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        boolean pres = FashionData.get(player).isPresent();
        if (pres) {
            FashionData fashionData = FashionData.get(player).orElseThrow(NullPointerException::new);
            boolean isMainHand = player.getMainArm() == HumanoidArm.RIGHT;

            ItemStack stackHeldItem = isMainHand ? player.getMainHandItem() : player.getOffhandItem();
            ItemStack stackOffHand = isMainHand ? player.getOffhandItem() : player.getMainHandItem();

            boolean renderedOffHand = false;
            boolean renderedHand = false;

            if (!stackHeldItem.isEmpty() || !stackOffHand.isEmpty()) {
                if (!fashionData.getRenderingPart(EnumFashionSlot.WEAPON).toString().contains("missing")) {
                    if (stackHeldItem.getItem() instanceof SwordItem) {
                        renderAesthetic(EnumFashionSlot.WEAPON, player, stackHeldItem, cam_right, HumanoidArm.RIGHT, poseStack, buffer, packedLightIn);
                        renderedHand = true;
                    }

                    if (stackOffHand.getItem() instanceof SwordItem) {
                        renderAesthetic(EnumFashionSlot.WEAPON, player, stackOffHand, cam_left, HumanoidArm.LEFT, poseStack, buffer, packedLightIn);
                        renderedOffHand = true;
                    }
                }

                if (!fashionData.getRenderingPart(EnumFashionSlot.SHIELD).toString().contains("missing")) {
                    if (stackHeldItem.getItem() instanceof ShieldItem || stackHeldItem.getItem().getUseAnimation(stackHeldItem) == UseAnim.BLOCK) {
                        renderAesthetic(EnumFashionSlot.SHIELD, player, stackHeldItem, cam_right, HumanoidArm.RIGHT, poseStack, buffer, packedLightIn);
                        renderedHand = true;
                    }

                    if (stackOffHand.getItem() instanceof ShieldItem || stackOffHand.getItem().getUseAnimation(stackOffHand) == UseAnim.BLOCK) {
                        renderAesthetic(EnumFashionSlot.SHIELD, player, stackOffHand, cam_left, HumanoidArm.LEFT, poseStack, buffer, packedLightIn);
                        renderedOffHand = true;
                    }
                }

                if (!renderedHand)
                    if (stackHeldItem.is(Items.SPYGLASS) && player.getUseItem() == stackHeldItem && player.swingTime == 0) {
                        renderArmWithSpyglass(player, stackHeldItem, HumanoidArm.RIGHT, poseStack, buffer, packedLightIn);
                    } else {
                        renderHeldItem(player, stackHeldItem, cam_right, HumanoidArm.RIGHT, poseStack, buffer, packedLightIn);
                    }
                if (!renderedOffHand)
                    if (stackOffHand.is(Items.SPYGLASS) && player.getUseItem() == stackOffHand && player.swingTime == 0) {
                        renderArmWithSpyglass(player, stackOffHand, HumanoidArm.LEFT, poseStack, buffer, packedLightIn);
                    } else {
                        renderHeldItem(player, stackOffHand, cam_left, HumanoidArm.LEFT, poseStack, buffer, packedLightIn);
                    }
            }
        }

//        FashionData.get(player).ifPresent(fashionData -> {
//
//        });
    }

    private void renderAesthetic(EnumFashionSlot slot, AbstractClientPlayer player, ItemStack stack, ItemTransforms.TransformType cam, HumanoidArm hand, PoseStack mat, MultiBufferSource buffer, int packedLightIn) {

//        FashionData.get(player).ifPresent(data -> {
        boolean pres = FashionData.get(player).isPresent();
        if (pres) {
            FashionData data = FashionData.get(player).orElseThrow(NullPointerException::new);
            if (stack.isEmpty())
                return;

            mat.pushPose();
            ResourceLocation resLoc = data.getRenderingPart(slot);
            boolean flag = hand == HumanoidArm.LEFT;
            this.getParentModel().translateToHand(hand, mat);
            switch (slot) {
                case WEAPON:
                    mat.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    mat.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    mat.translate((float) (flag ? -1 : 1) / 16.0F, 0.125D, -0.625D);
                    if (ResourcePackReader.isItem(resLoc))
                        // correct small offset for items
                        mat.translate(0.425, -0.592, 0.102);
                    else
                        // correct small offset for custom models
                        mat.translate(-0.5, -0.5, -0.5);
                    break;

                case SHIELD:
                    if (stack.getUseAnimation().equals(UseAnim.BLOCK) || stack.getItem() instanceof ShieldItem) {
                        boolean isBlocking = player.isUsingItem() && player.getUseItem().equals(stack);
                        mat.mulPose(Vector3f.XP.rotationDegrees(isBlocking ? -135F : -90.0F));
                        if (!flag)
                            mat.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                        if (isBlocking)
                            mat.mulPose(Vector3f.YP.rotationDegrees(-45.0F * (flag ? 1 : -1)));

                        mat.translate((float) (flag ? -1 : 1) / 16.0F, 0.125D, -0.625D);
                        if (flag) {
                            if (isBlocking)
                                mat.translate(0.0625f * -5.5f, 0.0625f * -16.5f, 0.0625f * 5f);
                            else
                                mat.translate(0.0625f * -9.5f, 0.0625f * -9.5f, 0.0625f * 8f);
                        } else if (isBlocking)
                            mat.translate(0.0625f * -7.5f, 0.0625f * -16.5f, 0.0625f * -0.5f);
                        else
                            mat.translate(0.0625f * -11.5f, 0.0625f * -9.5f, 0.0625f * -4f);
                    }

                    break;
                default:
                    break;
            }

            if (stack.getItem() instanceof ShieldItem || stack.getItem().getUseAnimation(stack) == UseAnim.BLOCK) {
                boolean isBlocking = player.isUsingItem() && player.getUseItem() == stack;
                resLoc = ResourcePackReader.getAestheticShield(data.getRenderingPart(slot), isBlocking);
            }

            BakedModel model_buffer = Minecraft.getInstance().getModelManager().getModel(resLoc);
            BakedModel rotatedModel = ForgeHooksClient.handleCameraTransforms(mat, model_buffer, cam, flag);

            renderModel(rotatedModel, buffer, getRenderType(), mat, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

            mat.popPose();
        }
//        });

    }

    private RenderType getRenderType() {

        return RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
    }

    // vanilla rendering
    private void renderHeldItem(LivingEntity ent, ItemStack stack, ItemTransforms.TransformType cam, HumanoidArm hand, PoseStack mat, MultiBufferSource buffer, int packedLightIn) {

        if (!stack.isEmpty()) {
            mat.pushPose();
            ((ArmedModel) this.getParentModel()).translateToHand(hand, mat);
            mat.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            mat.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            boolean flag = hand == HumanoidArm.LEFT;
            mat.translate((float) (flag ? -1 : 1) / 16.0F, 0.125D, -0.625D);
            Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(ent, stack, cam, flag, mat, buffer, packedLightIn);
            mat.popPose();
        }
    }

    public void renderModel(BakedModel model, MultiBufferSource bufferIn, RenderType rt, PoseStack matrixStackIn, int packedLightIn, int overlay, int color) {

        RandomSource rand = RandomSource.create(42L);

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = ((color) & 0xFF) / 255.0f;

        VertexConsumer bb = bufferIn.getBuffer(rt);
        for (BakedQuad quad : model.getQuads(null, null, rand)) {
            bb.putBulkData(matrixStackIn.last(), quad, r, g, b, a, packedLightIn, overlay, true);
        }
    }

    private void renderArmWithSpyglass(LivingEntity player, ItemStack stack, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, (-(float) Math.PI / 6F), ((float) Math.PI / 2F));
        modelpart.translateAndRotate(poseStack);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(poseStack, false);
        boolean flag = arm == HumanoidArm.LEFT;
        poseStack.translate((flag ? -2.5F : 2.5F) / 16.0F, -0.0625D, 0.0D);
        Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(player, stack, ItemTransforms.TransformType.HEAD, false, poseStack, buffer, light);
        poseStack.popPose();
    }
}