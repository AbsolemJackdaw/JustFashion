package subaraki.fashion.screen;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import lib.util.ClientReferences;
import lib.util.DrawEntityOnScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.client.event.forge_bus.KeyRegistry;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.server.PacketSyncPlayerFashionToServer;

public class WardrobeScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Fashion.MODID, "textures/gui/wardrobe.png");
    private float oldMouseX;
    private float oldMouseY;

    protected int xSize = 176;
    protected int ySize = 166;

    protected int guiLeft;
    protected int guiTop;

    private PlayerEntity player = ClientReferences.getClientPlayer();

    private String onScreenButtonText[] = new String[] { "hats", "body", "pants", "boots", "weapon", "shield" };

    public WardrobeScreen() {

        super(new StringTextComponent("fashion.wardrobe"));
    }

    @Override
    protected void init() {

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        DrawEntityOnScreen.drawEntityOnScreen(guiLeft + 33, guiTop + 65, 25, -(guiLeft - 70 - oldMouseX) / 2.5f, guiTop + 40 - oldMouseY, this.minecraft.player,
                135.0F, 25.0f, true); // TODO hacky way of letting vanilla layers show up on first opening of gui
        // FIXME

        super.init();

        // list of button id's for fashion hat to feet
        // ids 0-8
        for (int i = 0; i < 8; i++) {
            int x = guiLeft + 105 + (i % 2 == 0 ? 0 : 55);
            int y = guiTop + 10 + (i / 2 * 15);
            final int id = i;

            this.addButton(new Button(x, y, 10, 10, i % 2 == 0 ? "<" : ">", c -> cycle(id)));
        }

        // list of button id's for shield and sword
        // ids 8-12
        for (int i = 8; i < 12; i++) {
            int x = guiLeft + 105 + (i % 2 == 0 ? 0 : 55);
            int y = guiTop + 45 + ((i - 4) / 2 * 15);
            final int id = i;

            this.addButton(new Button(x, y, 10, 10, (i % 2 == 0 ? "<" : ">"), c -> cycle(id)));
        }

        FashionData.get(player).ifPresent(fashion -> {

            // toggle buttons for each render layer that exists in the game for players,
            // both from mods and vanilla
            if (!fashion.getSavedOriginalList().isEmpty())
                for (int i = 0; i < fashion.getSavedOriginalList().size(); i++) {
                    final int index = i;
                    LayerRenderer<?, ?> layer = fashion.getSavedOriginalList().get(index);

                    this.addButton(new FancyButton(guiLeft - 12 - (i % 5) * 10, guiTop + 6 + (i / 5) * 10, layer.getClass().getSimpleName(),
                            b -> toggleLayer((FancyButton) b, layer)).setActive(fashion.keepLayers.contains(layer)));
                }

            // toggle button, with the explicit press ID of 12 (could be anything at this
            // point, it's an artifact for pre 1.14
            this.addButton(new FancyButton(guiLeft + 8, guiTop + ySize / 2 + 14, c -> pressToggle((FancyButton) c)).setActive(fashion.shouldRenderFashion()));
        });

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        super.render(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) i, (float) j, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        int k = 240;
        int l = 240;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        RenderHelper.enableStandardItemLighting();

    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

        this.getMinecraft().getTextureManager().bindTexture(BACKGROUND);

        // this.zLevel = 0;
        blit(guiLeft + 14, guiTop + 7, xSize, 0, 38, 62);

        GlStateManager.pushMatrix();
        FashionData.get(player).ifPresent(fashion -> {
            fashion.setInWardrobe(false); // disable for in gui rendering
        });
        DrawEntityOnScreen.drawEntityOnScreen(guiLeft + 33, guiTop + 65, 25, -(guiLeft - 70 - oldMouseX) / 2.5f, guiTop + 40 - oldMouseY, this.minecraft.player,
                135.0F, 25.0f, true);
        DrawEntityOnScreen.drawEntityOnScreen(guiLeft + 68, guiTop + 82, 30, -(guiLeft + 70 - oldMouseX) / 2.5f, guiTop + 40 - oldMouseY, this.minecraft.player,
                -45.0f, 150.0f, false);
        FashionData.get(player).ifPresent(fashion -> {
            fashion.setInWardrobe(true);// set back after drawn for in world rendering
        });
        GlStateManager.popMatrix();

        // this.zLevel = 90;
        GlStateManager.enableBlend();
        GlStateManager.enableAlphaTest();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();

        // this.zLevel = 0;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        FashionData.get(player).ifPresent(fashion -> {
            for (int i = 0; i < this.onScreenButtonText.length - 2; i++) {
                EnumFashionSlot slot = EnumFashionSlot.fromInt(i);

                ResourceLocation resLoc = ResourcePackReader.getResourceForPart(slot, fashion.getPartIndex(slot));
                String[] s = null;
                String name = null;

                try {
                    s = resLoc.getPath().split("/");
                    name = s[s.length - 1].split("\\.")[0];
                } catch (NullPointerException e) {

                }

                if (name == null)
                    name = "errored";

                if (name.contains("blank") || name.contains("missing"))
                    name = "N/A";

                minecraft.fontRenderer.drawString(name, 138 - minecraft.fontRenderer.getStringWidth(name) / 2, ((i + 1) * 15) - 3, 0xffffff);

            }

            for (int i = 4; i < 6; i++) {
                EnumFashionSlot slot = EnumFashionSlot.fromInt(i);
                ResourceLocation resLoc = ResourcePackReader.getResourceForPart(slot, fashion.getPartIndex(slot));
                String[] s = null;
                String name = null;

                try {
                    s = resLoc.getPath().split("/");
                    name = s[s.length - 1].split("\\.")[0];
                } catch (NullPointerException e) {

                }

                if (name == null)
                    name = "no model";

                if (name.contains("blank") || name.contains("missing"))
                    name = "N/A";

                minecraft.fontRenderer.drawString(name, 138 - minecraft.fontRenderer.getStringWidth(name) / 2, 31 + ((i + 1) * 15) - 29, 0xffffff);
            }

            String toggled = fashion.shouldRenderFashion() ? "Showing Fashion" : "Showing Armor";
            minecraft.fontRenderer.drawString(toggled, xSize / 2 - 68, ySize / 2 + 14, 0xffffff); // TODO used to be
                                                                                                  // a
            // boolean here for
            // shadow drawing

            // tracking player view !!
            this.oldMouseX = (float) mouseX;
            this.oldMouseY = Math.min((float) mouseY, guiTop + 50);

            for (Widget guiButton : buttons) {
                if (guiButton instanceof FancyButton) {
                    FancyButton gfb = (FancyButton) guiButton;
                    if (gfb.isMouseOver(mouseX, mouseY) && !gfb.isSwitch()) {
                        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(gfb.name));
                        Style style = new Style().setHoverEvent(hover);
                        this.renderComponentHoverEffect(hover.getValue().setStyle(style), mouseX - guiLeft, mouseY - guiTop);
                    }
                }
            }

        });
    }

    @Override
    public boolean shouldCloseOnEsc() {

        return true; // needed to trigger onClose and packets on pressing escape button
    }

    // called whenever a screen is closed, by player or by opening another screen or
    // trough force (to far away)
    @Override
    public void removed() {

        FashionData.get(player).ifPresent(fashion -> {

            NetworkHandler.NETWORK.sendToServer(
                    new PacketSyncPlayerFashionToServer(fashion.getAllParts(), fashion.shouldRenderFashion(), fashion.getSimpleNamesForToggledFashionLayers()));

            fashion.setInWardrobe(false);
        });

        // no need for super. super only calls to throw out item stacks

    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {

        if (KeyRegistry.keyWardrobe.getKeyBinding().getLocalizedName().equals(Character.toString(typedChar))) {
            this.minecraft.player.closeScreen();
            return true;
        }

        return super.charTyped(typedChar, keyCode);
    }

    ////////////////
    ////////////
    ///////////////
    ////////////////

    private void cycle(int Id) {

        FashionData.get(player).ifPresent(fashion -> {
            if (fashion.shouldRenderFashion() && Id < 12) {
                int slot = (Id) / 2;
                int id = fashion.getPartIndex(EnumFashionSlot.fromInt(slot));

                if (Id % 2 == 0)
                    id--;
                else
                    id++;
                if (id >= ResourcePackReader.partsSize(EnumFashionSlot.fromInt(slot)))
                    id = 0;
                if (id < 0)
                    id = ResourcePackReader.partsSize(EnumFashionSlot.fromInt(slot)) - 1;

                // small fix for when no parts are present or loaded to prevent complaining
                // about -1
                if (id < 0)
                    id = 0;

                fashion.updatePartIndex(id, EnumFashionSlot.fromInt(slot));
            }
        });

    }

    private void pressToggle(FancyButton button) {

        button.toggle();

        FashionData.get(player).ifPresent(fashion -> {
            fashion.setRenderFashion(button.isActive());
        });
    }

    private void toggleLayer(FancyButton button, LayerRenderer<?, ?> layer) {

        button.toggle(); // set opposite of current state

        FashionData.get(player).ifPresent(fashion -> {

            if (button.isActive())// if set to active
                fashion.keepLayers.add(layer);
            else
                fashion.keepLayers.remove(layer);

            fashion.fashionLayers.clear();
        });

    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }
}
