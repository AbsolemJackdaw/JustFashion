package subaraki.fashion.client.event.forge_bus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.model.ModelFashionBody;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HandRenderEvent {

    private static ModelFashionBody body;
    private static ResourceLocation resLoc;

    @SubscribeEvent
    public static void renderHandEvent(RenderHandEvent event) {

        PlayerEntity player = Minecraft.getInstance().player;

        if (!event.getItemStack().isEmpty())
            return;

        FashionData.get(player).ifPresent(fashionData -> {

            if (body == null) {
                body = new ModelFashionBody<AbstractClientPlayerEntity>(0.27f, false);
                body.setAllVisible(false);
                body.rightArm.visible = true;
                body.bipedRightArmwear.visible = true;
            }

            ResourceLocation resLoc = fashionData.getRenderingPart(EnumFashionSlot.CHEST);

            if (!fashionData.shouldRenderFashion() || resLoc == null || resLoc.toString().contains("missing"))
                return;

            event.getMatrixStack().pushPose();

            boolean handFlag = event.getHand() == Hand.MAIN_HAND;
            if (!handFlag)
                return;
            HandSide handside = handFlag ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean flag = handside != HandSide.LEFT;
            float f = flag ? 1.0F : -1.0F;
            float f1 = MathHelper.sqrt(event.getSwingProgress());
            float f2 = -0.3F * MathHelper.sin(f1 * 3.1415927F);
            float f3 = 0.4F * MathHelper.sin(f1 * 6.2831855F);
            float f4 = -0.4F * MathHelper.sin(event.getSwingProgress() * 3.1415927F);
            event.getMatrixStack().translate((double) (f * (f2 + 0.64000005F)), (double) (f3 + -0.6F + event.getEquipProgress() * -0.6F), (double) (f4 + -0.71999997F));
            event.getMatrixStack().mulPose(Vector3f.YP.rotationDegrees(f * 45.0F));
            float f5 = MathHelper.sin(event.getSwingProgress() * event.getSwingProgress() * 3.1415927F);
            float f6 = MathHelper.sin(f1 * 3.1415927F);
            event.getMatrixStack().mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
            event.getMatrixStack().mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
            event.getMatrixStack().translate((double) (f * -1.0F), 3.5999999046325684D, 3.5D);
            event.getMatrixStack().mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
            event.getMatrixStack().mulPose(Vector3f.XP.rotationDegrees(200.0F));
            event.getMatrixStack().mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
            event.getMatrixStack().translate((double) (f * 5.6F), 0.0D, 0.0D);

            if (flag) {
                event.getMatrixStack().translate(0.0625f * 13.5f, -0.0625 * (16f + 11f), 0.0f);
                event.getMatrixStack().mulPose(Vector3f.ZP.rotationDegrees(6.25f));

            } else {
                event.getMatrixStack().translate(-0.0625f, -0.0625 * (16f + 12.5f), -0.0625f / 2f);
                event.getMatrixStack().mulPose(Vector3f.ZP.rotationDegrees(-6.25f));

            }

            float s = 0.0625f * (16f + 16f);
            event.getMatrixStack().scale(s, s, s);

            if (flag)
            {
                body.rightArm.translateAndRotate(event.getMatrixStack());
                body.bipedRightArmwear.copyFrom(body.rightArm);
            }
            else
            {
                body.leftArm.translateAndRotate(event.getMatrixStack());
                body.bipedLeftArmwear.copyFrom(body.leftArm);
            }

            body.renderToBuffer(event.getMatrixStack(), event.getBuffers().getBuffer(RenderType.entityCutoutNoCull(resLoc)), event.getLight(), OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

            event.getMatrixStack().popPose();


        });
    }

}
