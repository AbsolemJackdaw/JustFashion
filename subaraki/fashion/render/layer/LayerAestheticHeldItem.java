package subaraki.fashion.render.layer;

import org.lwjgl.opengl.GL11;

import lib.modelloader.ModelHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.proxy.ClientProxy;

public class LayerAestheticHeldItem implements LayerRenderer<AbstractClientPlayer>{

	private RenderPlayer renderer;
	private TransformType cam_right = ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
	private TransformType cam_left= ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;

	public LayerAestheticHeldItem(RenderPlayer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

		FashionData fashionData = player.getCapability(FashionCapability.CAPABILITY,null);

		ItemStack stackHeldItem = player.getHeldItemMainhand();
		ItemStack stackOffHand = player.getHeldItemOffhand();

		if(fashionData.getPartIndex(EnumFashionSlot.WEAPON) == 0)
		{
			renderHeldItem(player, stackHeldItem, cam_right, EnumHandSide.RIGHT);
			renderHeldItem(player, stackOffHand, cam_left, EnumHandSide.LEFT);
			return;
		}

		if(stackHeldItem.getItem() instanceof ItemSword)
		{
			renderAesthetic(player, EnumHandSide.RIGHT, fashionData, cam_right);
		}
		else
		{
			renderHeldItem(player, stackHeldItem, cam_right, EnumHandSide.RIGHT);
		}

		if(stackOffHand.getItem() instanceof ItemSword)
		{
			renderAesthetic(player, EnumHandSide.LEFT, fashionData, cam_left);
		}
		else
		{
			renderHeldItem(player, stackOffHand, cam_left, EnumHandSide.LEFT);
		}

	}

	private void render(IBakedModel model){
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldrenderer = tessellator.getBuffer();
		worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		for (BakedQuad bakedquad : model.getQuads(null, null, 0))
		{
			worldrenderer.addVertexData(bakedquad.getVertexData());
		}
		tessellator.draw();
	}

	private void renderAesthetic(AbstractClientPlayer player, EnumHandSide handSide, FashionData data, TransformType cam){
		GlStateManager.pushMatrix();
		
		if(player.isSneaking())
		{
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
		}

		this.translateToHand(handSide);

		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		boolean flag = handSide == EnumHandSide.LEFT;
		GlStateManager.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
		
		this.renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		IBakedModel handleModel = ModelHandle.of(ClientProxy.getResourceForPart(EnumFashionSlot.WEAPON, data.getPartIndex(EnumFashionSlot.WEAPON))).get();
		IBakedModel model = ForgeHooksClient.handleCameraTransforms(handleModel, cam, flag);
		GlStateManager.translate((float)((flag ? -1 : 1) / 16.0F)+(flag ? - 0.0625f*7 : - 0.0625f*9) , -0.0625f*8 , -0.0625f*8);

		render(model);

		GlStateManager.popMatrix();
	}

	//vanilla rendering
	private void renderHeldItem(AbstractClientPlayer player, ItemStack stack, ItemCameraTransforms.TransformType cam, EnumHandSide handSide)
	{
		if (!stack.isEmpty())
		{
			GlStateManager.pushMatrix();

			if (player.isSneaking())
			{
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			// Forge: moved this call down, fixes incorrect offset while sneaking.
			this.translateToHand(handSide);
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			boolean flag = handSide == EnumHandSide.LEFT;
			GlStateManager.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, cam, flag);
			GlStateManager.popMatrix();
		}
	}

	protected void translateToHand(EnumHandSide p_191361_1_)
	{
		((ModelBiped)this.renderer.getMainModel()).postRenderArm(0.0625F, p_191361_1_);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}