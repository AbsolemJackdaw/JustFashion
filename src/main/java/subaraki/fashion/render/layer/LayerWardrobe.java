package subaraki.fashion.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.model.data.EmptyModelData;
import subaraki.fashion.capability.FashionData;

import java.util.Random;

public class LayerWardrobe extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation modelLocation = new ResourceLocation("fashion:wardrobe");

    public LayerWardrobe(PlayerRenderer renderer) {

        super(renderer);
    }

    @Override
    public void render(PoseStack mat, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        FashionData.get(entitylivingbaseIn).ifPresent(fashion -> {

            if (fashion.isInWardrobe()) {
                mat.pushPose();
                // pixel scale * pixel offset = 16 pixels is one block
                float scale = 0.0625f * (16 + 4);
                mat.scale(scale, scale, scale);

                mat.mulPose(Vector3f.XP.rotationDegrees(180));
                mat.translate(-(0.0625f * 8), -(0.0625f * (16 + 4)), -(0.0625f * 8));
                BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);
                render(model, bufferIn, getRenderType(), mat, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                mat.popPose();
            }
        });
    }

    private RenderType getRenderType() {

        return RenderType.entitySolid(InventoryMenu.BLOCK_ATLAS);
    }

    public void render(BakedModel model, MultiBufferSource bufferIn, RenderType rt, PoseStack matrixStackIn, int packedLightIn, int overlay, int color) {

        Random rand = new Random(42);

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = ((color >> 0) & 0xFF) / 255.0f;

        VertexConsumer bb = bufferIn.getBuffer(rt);
        for (BakedQuad quad : model.getQuads(null, null, rand, EmptyModelData.INSTANCE)) {
            bb.putBulkData(matrixStackIn.last(), quad, r, g, b, a, packedLightIn, overlay, true);
        }
    }
}