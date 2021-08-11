package subaraki.fashion.client.render.layer;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.mod.EnumFashionSlot;

public class LayerAestheticHeldItem extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private TransformType cam_right = ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    private TransformType cam_left = ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;

    public LayerAestheticHeldItem(PlayerRenderer renderer) {

        super(renderer);
    }

    @Override
    public void render(MatrixStack mat, IRenderTypeBuffer buffer, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {

        FashionData.get(player).ifPresent(fashionData -> {

            ItemStack stackHeldItem = player.getHeldItemMainhand();
            ItemStack stackOffHand = player.getHeldItemOffhand();

            boolean renderedOffHand = false;
            boolean renderedHand = false;

            if (!fashionData.getRenderingPart(EnumFashionSlot.WEAPON).toString().contains("missing"))
            {
                if (stackHeldItem.getItem() instanceof SwordItem)
                {
                    renderAesthetic(EnumFashionSlot.WEAPON, player, stackHeldItem, cam_right, HandSide.RIGHT, mat, buffer, packedLightIn);
                    renderedHand = true;
                }

                if (stackOffHand.getItem() instanceof SwordItem)
                {
                    renderAesthetic(EnumFashionSlot.WEAPON, player, stackOffHand, cam_left, HandSide.LEFT, mat, buffer, packedLightIn);
                    renderedOffHand = true;
                }
            }

            if (!fashionData.getRenderingPart(EnumFashionSlot.SHIELD).toString().contains("missing"))
            {
                if (stackHeldItem.getItem() instanceof ShieldItem || stackHeldItem.getItem().getUseAction(stackHeldItem) == UseAction.BLOCK)
                {
                    renderAesthetic(EnumFashionSlot.SHIELD, player, stackHeldItem, cam_right, HandSide.RIGHT, mat, buffer, packedLightIn);
                    renderedHand = true;
                }

                if (stackOffHand.getItem() instanceof ShieldItem || stackOffHand.getItem().getUseAction(stackOffHand) == UseAction.BLOCK)
                {
                    renderAesthetic(EnumFashionSlot.SHIELD, player, stackOffHand, cam_left, HandSide.LEFT, mat, buffer, packedLightIn);
                    renderedOffHand = true;
                }
            }

            if (!renderedHand)
                renderHeldItem(player, stackHeldItem, cam_right, HandSide.RIGHT, mat, buffer, packedLightIn);
            if (!renderedOffHand)
                renderHeldItem(player, stackOffHand, cam_left, HandSide.LEFT, mat, buffer, packedLightIn);

        });
    }

    private void renderAesthetic(EnumFashionSlot slot, AbstractClientPlayerEntity player, ItemStack stack, ItemCameraTransforms.TransformType cam, HandSide hand, MatrixStack mat, IRenderTypeBuffer buffer, int packedLightIn)
    {

        FashionData.get(player).ifPresent(data -> {

            if (stack.isEmpty())
                return;

            mat.push();
            ResourceLocation resLoc = data.getRenderingPart(slot);
            boolean flag = hand == HandSide.LEFT;

            this.getEntityModel().translateHand(hand, mat);
            mat.rotate(Vector3f.XP.rotationDegrees(-90.0F));
            mat.rotate(Vector3f.YP.rotationDegrees(180.0F));
            mat.translate((double) ((float) (flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);

            switch (slot)
            {
            case WEAPON:
                if (ResourcePackReader.isItem(resLoc))
                    // correct small offset for items
                    mat.translate(0.425, -0.592, 0.102);
                else
                    // correct small offset for custom models
                    mat.translate(-0.5, -0.5, -0.5);
                break;

            case SHIELD:
                if (stack.getUseAction().equals(UseAction.BLOCK) || stack.getItem() instanceof ShieldItem)
                {
                    boolean isBlocking = player.isHandActive() && player.getActiveItemStack().equals(stack);

                    if (flag)
                    {
                        if (isBlocking)
                            mat.translate(0.0625f * -10, 0.0625f * -3, 0.0625f * -9);
                        else
                            mat.translate(0.0625f * -8, -0.0625f * 8, -0.0625f * -10);
                    }
                    else
                        if (isBlocking)
                            mat.translate(0.0625f * -5, 0.0, 0.0625f * -13);
                        else
                            mat.translate(0.0625f * 8, -0.0625f * 8, -0.0625f * 6);

                }

                break;
            default:
                break;
            }

            if (stack.getItem() instanceof ShieldItem || stack.getItem().getUseAction(stack) == UseAction.BLOCK)
            {
                boolean isBlocking = player.isHandActive() && player.getActiveItemStack() == stack;
                resLoc = ResourcePackReader.getAestheticShield(data.getRenderingPart(slot), isBlocking);
            }

            IBakedModel model_buffer = Minecraft.getInstance().getModelManager().getModel(resLoc);
            IBakedModel rotatedModel = ForgeHooksClient.handleCameraTransforms(mat, model_buffer, cam, flag);

            renderModel(rotatedModel, buffer, getRenderType(), mat, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

            mat.pop();

        });

    }

    private RenderType getRenderType()
    {

        return RenderType.getEntityTranslucent(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    }

    // vanilla rendering
    private void renderHeldItem(LivingEntity ent, ItemStack stack, ItemCameraTransforms.TransformType cam, HandSide hand, MatrixStack mat, IRenderTypeBuffer buffer, int packedLightIn)
    {

        if (!stack.isEmpty())
        {
            mat.push();
            this.getEntityModel().translateHand(hand, mat);
            mat.rotate(Vector3f.XP.rotationDegrees(-90.0F));
            mat.rotate(Vector3f.YP.rotationDegrees(180.0F));
            boolean flag = hand == HandSide.LEFT;
            mat.translate((double) ((float) (flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(ent, stack, cam, flag, mat, buffer, packedLightIn);
            mat.pop();
        }
    }

    public void renderModel(IBakedModel model, IRenderTypeBuffer bufferIn, RenderType rt, MatrixStack matrixStackIn, int packedLightIn, int overlay, int color)
    {

        Random rand = new Random(42);

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = ((color >> 0) & 0xFF) / 255.0f;

        IVertexBuilder bb = bufferIn.getBuffer(rt);
        for (BakedQuad quad : model.getQuads(null, null, rand, EmptyModelData.INSTANCE))
        {
            bb.addVertexData(matrixStackIn.getLast(), quad, r, g, b, a, packedLightIn, overlay, true);
        }
    }

}