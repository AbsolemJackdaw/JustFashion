package subaraki.fashion.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiFancyButton extends GuiButton {

	private boolean isActive;

	public  String name = "unknown layer";
	
	public GuiFancyButton(int buttonId, int x, int y) {
		super(buttonId, x, y, 15, 15, "");
	}

	public GuiFancyButton(int buttonId, int x, int y, String name) {
		this(buttonId, x, y);
		this.name = name;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float particleticks)
	{
		if (this.visible)
		{
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width/2 && mouseY < this.y + this.height/2;
			int hoverState = this.getHoverState(this.hovered);
            
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5f,0.5f,0.5f);
			this.drawTexturedModalRect((this.x)*2, (this.y)*2, isActive ? 208 : 224, 0, this.width, this.height);
			GlStateManager.popMatrix();
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
		boolean clicked = this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width/2 && mouseY < this.y + this.height/2;
		if(clicked)
			this.isActive = !isActive;

		return clicked;
	}

	protected void renderToolTip(int x, int y)
	{
	}

	private void drawHoveringText(String name2, int x, int y, FontRenderer font) {
	}
}
