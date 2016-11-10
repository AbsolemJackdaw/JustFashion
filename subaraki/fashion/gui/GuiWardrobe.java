package subaraki.fashion.gui;

import java.io.IOException;

import lib.util.DrawEntityOnScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.PacketSyncPlayerFashionToServer;
import subaraki.fashion.proxy.ClientProxy;

public class GuiWardrobe extends GuiContainer{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Fashion.MODID, "textures/gui/wardrobe.png");
	private float oldMouseX;
	private float oldMouseY;
	private FashionData fashion;

	private String onScreenButtonText[] = new String[]{
			"hats",
			"body",
			"pants",
			"boots"
	};
	public GuiWardrobe(FashionContainer inventorySlotsIn) {
		super(inventorySlotsIn);
		fashion = FashionData.get(inventorySlotsIn.player);
	}

	@Override
	public void initGui() {
		buttonList.clear();
		super.initGui();

		for(int i = 0; i < 8; i++)
			buttonList.add(new GuiButton(i, guiLeft + 10 + (i%2 == 0 ? 0 : 40), guiTop + 100 + (i/2* 15), 10, 10, i%2 == 0 ? "<" : ">"));

		buttonList.add(new GuiFancyButton(20, guiLeft + xSize - 12, guiTop + ySize / 2 + 12).setActive(fashion.shouldRenderFashion()));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button instanceof GuiFancyButton)
			fashion.setRenderFashion( ((GuiFancyButton)button).isActive() );

		else if(fashion.shouldRenderFashion())
		{
			int slot = (button.id)/2;
			int id = fashion.getPartIndex(slot);

			if(button.id%2 == 0)
				id--;
			else
				id++;

			if(id >= ClientProxy.partsSize(slot))
				id = 0;
			if(id < 0)
				id = ClientProxy.partsSize(slot)-1;

			fashion.updatePartIndex(id, slot);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		this.mc.renderEngine.bindTexture(BACKGROUND);

		this.zLevel = 0;
		drawTexturedModalRect(guiLeft+14, guiTop+7, xSize, 0, 38, 62);

		GlStateManager.pushMatrix();
		DrawEntityOnScreen.drawEntityOnScreen(guiLeft+33, guiTop+65, 25, -(guiLeft-70-oldMouseX)/2.5f, guiTop + 40 - oldMouseY, this.mc.thePlayer, 135.0F, 25.0f, true); 
		DrawEntityOnScreen.drawEntityOnScreen(guiLeft+68, guiTop+82, 30, -(guiLeft+70-oldMouseX)/2.5f, guiTop + 40 - oldMouseY, this.mc.thePlayer, -45.0f , 150.0f, false); 

		GlStateManager.popMatrix();

		this.zLevel = 90;
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 0.8f);
		this.mc.renderEngine.bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();

		this.zLevel = 0;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		for(int i = 0; i < this.onScreenButtonText.length; i++)
			mc.fontRendererObj.drawString(this.onScreenButtonText[i], 
					35 - mc.fontRendererObj.getStringWidth("hats")/2,
					86 + ((i+1)* 15),
					0xffffff, true);

		String toggled = fashion.shouldRenderFashion() ? "Showing Fashion" : "Showing Armor";
		mc.fontRendererObj.drawString(toggled, 
				xSize - 14 - mc.fontRendererObj.getStringWidth(toggled),
				ySize / 2 + 11,
				0xffffff, true);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.oldMouseX = (float)mouseX;
		this.oldMouseY = Math.min((float)mouseY, guiTop+50);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		NetworkHandler.NETWORK.sendToServer(new PacketSyncPlayerFashionToServer(fashion.getAllParts(), fashion.shouldRenderFashion()));
	}
}
