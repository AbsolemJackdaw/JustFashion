package subaraki.fashion.gui;

import java.io.IOException;

import lib.util.DrawEntityOnScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
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
			"boots",
			"weapon",
			"shield"
	};
	public GuiWardrobe(FashionContainer inventorySlotsIn) {
		super(inventorySlotsIn);
		fashion = FashionData.get(inventorySlotsIn.player);

	}

	@Override
	public void initGui() {
		buttonList.clear();

		super.initGui();

		//ids 0-8
		for(int i = 0; i < 8; i++)
		{
			int x = guiLeft + 10 + (i%2 == 0 ? 0 : 45);
			int y = guiTop + 100 + (i/2* 15);

			buttonList.add(new GuiButton(i, x, y, 10, 10, i%2 == 0 ? "<" : ">"));
		}

		//ids 8-12
		for(int i = 4; i < 8; i++)
		{
			int x = guiLeft + 75 + (i%2 == 0 ? 0 : 50);
			int y = guiTop + 100 + (i/2* 15);

			buttonList.add(new GuiButton(i+4, x, y, 10, 10, i%2 == 0 ? "<" : ">"));
		}

		buttonList.add(new GuiFancyButton(12, guiLeft + xSize - 12, guiTop + ySize / 2 + 12).setActive(fashion.shouldRenderFashion()));

		if(!fashion.getSavedOriginalList().isEmpty())
			for(int i = 0; i < fashion.getSavedOriginalList().size(); i++)
			{
				final int index = i;
				LayerRenderer layer = fashion.getSavedOriginalList().get(index);
				
				buttonList.add(new GuiFancyButton(13+i, (guiLeft - 12) - (i%5)*10, (guiTop ) + (i/5)*10, layer.getClass().getSimpleName()){
					@Override
					public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
						boolean clicked = this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width/2 && mouseY < this.y + this.height/2;
						if(clicked)
						{
							setActive(!isActive()); //set opposite of current state
							
							if(isActive())//if set to active
								fashion.keepLayers.add(layer);
							else
								fashion.keepLayers.remove(layer);
							
							fashion.fashionLayers.clear();
						}
						return clicked;
					};

				}.setActive(fashion.keepLayers.contains(layer)));
			}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button instanceof GuiFancyButton && button.id == 12)
		{
			fashion.setRenderFashion( ((GuiFancyButton)button).isActive() );
			initGui();
		}

		else if(fashion.shouldRenderFashion() && button.id < 12)
		{
			int slot = (button.id)/2;
			int id = fashion.getPartIndex(EnumFashionSlot.fromInt(slot));

			if(button.id%2 == 0)
				id--;
			else
				id++;
			if(id >= ClientProxy.partsSize(EnumFashionSlot.fromInt(slot)))
				id = 0;
			if(id < 0)
				id = ClientProxy.partsSize(EnumFashionSlot.fromInt(slot))-1;

			fashion.updatePartIndex(id, EnumFashionSlot.fromInt(slot));
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		this.mc.renderEngine.bindTexture(BACKGROUND);

		this.zLevel = 0;
		drawTexturedModalRect(guiLeft+14, guiTop+7, xSize, 0, 38, 62);

		GlStateManager.pushMatrix();
		FashionData.get(this.mc.player).setInWardrobe(false); //disable for in gui rendering
		DrawEntityOnScreen.drawEntityOnScreen(guiLeft+33, guiTop+65, 25, -(guiLeft-70-oldMouseX)/2.5f, guiTop + 40 - oldMouseY, this.mc.player, 135.0F, 25.0f, true); 
		DrawEntityOnScreen.drawEntityOnScreen(guiLeft+68, guiTop+82, 30, -(guiLeft+70-oldMouseX)/2.5f, guiTop + 40 - oldMouseY, this.mc.player, -45.0f , 150.0f, false); 
		FashionData.get(this.mc.player).setInWardrobe(true); // set back after drawn for in world rendering
		GlStateManager.popMatrix();

		this.zLevel = 90;
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		this.mc.renderEngine.bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();

		this.zLevel = 0;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		for(int i = 0; i < this.onScreenButtonText.length-2; i++)
			mc.fontRenderer.drawString(this.onScreenButtonText[i], 
					35 - mc.fontRenderer.getStringWidth("hats")/2,
					86 + ((i+1)* 15),
					0xffffff, true);

		for(int i = 2; i < 4; i++)
			mc.fontRenderer.drawString(this.onScreenButtonText[i+2], 
					99 - mc.fontRenderer.getStringWidth("hats")/2,
					86 + ((i+1)* 15),
					0xffffff, true);

		String toggled = fashion.shouldRenderFashion() ? "Showing Fashion" : "Showing Armor";
		mc.fontRenderer.drawString(toggled, 
				xSize - 14 - mc.fontRenderer.getStringWidth(toggled),
				ySize / 2 + 11,
				0xffffff, true);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.oldMouseX = (float)mouseX;
		this.oldMouseY = Math.min((float)mouseY, guiTop+50);
		
		for (GuiButton guiButton : buttonList) {
			if(guiButton instanceof GuiFancyButton)
			{
				GuiFancyButton gfb = (GuiFancyButton)guiButton;
				if(gfb.isMouseOver() && gfb.id != 12)
					this.drawHoveringText(gfb.name, mouseX, mouseY);
			}
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		NetworkHandler.NETWORK.sendToServer(new PacketSyncPlayerFashionToServer(fashion.getAllParts(), fashion.shouldRenderFashion()));
		FashionData.get(this.mc.player).setInWardrobe(false);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if(keyCode == ClientProxy.keyWardrobe.getKeyCode())
		{
			this.mc.player.closeScreen();
		}
	}
}
