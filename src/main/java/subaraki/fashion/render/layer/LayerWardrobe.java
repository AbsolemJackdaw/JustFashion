package subaraki.fashion.render.layer;

import com.mojang.blaze3d.platform.GlStateManager;

import lib.modelloader.ModelHandle;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;

public class LayerWardrobe extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private ModelHandle modelHandle = ModelHandle.of(new ResourceLocation(Fashion.MODID, "wardrobe"));

    private PlayerRenderer renderer;

    public LayerWardrobe(PlayerRenderer renderer) {

        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public boolean shouldCombineTextures() {

        return false;
    }

    @Override
    public void render(AbstractClientPlayerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        FashionData fashion = FashionData.get((PlayerEntity) entityIn);

        if (fashion.isInWardrobe()) {
            GlStateManager.pushMatrix();
            scale = 1.2f;
            renderer.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.translatef(-0.6f, 1.5f, 0.5f);
            GlStateManager.rotatef(180, 1, 0, 0);
            GlStateManager.scalef(scale, scale, scale);
            modelHandle.render();
            GlStateManager.popMatrix();
        }
    }
}