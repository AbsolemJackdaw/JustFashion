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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import subaraki.fashion.capability.FashionData;

public class LayerWardrobe extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private static ResourceLocation modelLocation = new ResourceLocation("fashion:wardrobe");

    public LayerWardrobe(PlayerRenderer renderer) {

        super(renderer);
    }

    @Override
    public void render(MatrixStack mat, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {

        FashionData.get((PlayerEntity) entitylivingbaseIn).ifPresent(fashion -> {

            if (fashion.isInWardrobe())
            {
                mat.push();
                // pixel scale * pixel offset = 16 pixels is one block
                float scale = 0.0625f * (16 + 4);
                mat.scale(scale, scale, scale);

                mat.rotate(Vector3f.XP.rotationDegrees(180));
                mat.translate(-(0.0625f * 8), -(0.0625f * (16 + 4)), -(0.0625f * 8));
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);
                render(model, bufferIn, getRenderType(), mat, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                mat.pop();
            }
        });
    }

    private RenderType getRenderType()
    {

        return RenderType.getEntitySolid(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    }

    public void render(IBakedModel model, IRenderTypeBuffer bufferIn, RenderType rt, MatrixStack matrixStackIn, int packedLightIn, int overlay, int color)
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