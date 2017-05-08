package subaraki.fashion.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiFancyButton extends GuiButton {

	private boolean isActive;
	public GuiFancyButton(int buttonId, int x, int y) {
		super(buttonId, x, y, 15, 15, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			this.drawTexturedModalRect((this.xPosition)*2, (this.yPosition)*2, isActive ? 208 : 224, 0, this.width, this.height);
			GlStateManager.popMatrix();

			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;

			if (packedFGColour != 0)
			{
				j = packedFGColour;
			}
			else
				if (!this.enabled)
				{
					j = 10526880;
				}
				else if (this.hovered)
				{
					j = 16777120;
				}

			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
		}
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public GuiFancyButton setActive(boolean flag) {
		isActive = flag;
		return this;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean clicked = super.mousePressed(mc, mouseX, mouseY);
		if(clicked)
			this.isActive = !isActive;
		
		return clicked;
	}
}
