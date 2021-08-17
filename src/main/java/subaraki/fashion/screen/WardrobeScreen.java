package subaraki.fashion.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.client.event.forge_bus.KeyRegistry;
import subaraki.fashion.mod.ConfigData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.server.PacketSyncPlayerFashionToServer;

import java.util.Arrays;

public class WardrobeScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Fashion.MODID, "textures/gui/wardrobe.png");
    private float oldMouseX;
    private float oldMouseY;

    protected int xSize = 176;
    protected int ySize = 166;

    protected int guiLeft;
    protected int guiTop;

    private PlayerEntity player;

    public WardrobeScreen() {

        super(new StringTextComponent("fashion.wardrobe"));
        player = Minecraft.getInstance().player;
    }

    @Override
    protected void init() {

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        Arrays.stream(EnumFashionSlot.values()).forEach(t -> {
            addSlotButton(t);
            addSlotButton(t);
        });

        id = 0; // reset id to 0 in case of resizing ! else it will add up and up and offset y
        // to infinity

        InventoryScreen.renderEntityInInventory(guiLeft + 33, guiTop + 65, 25, -(guiLeft - 70 - oldMouseX) / 2.5f, guiTop + 40 - oldMouseY,
                player);
        // TODO hacky way of letting vanilla layers show up on first opening of gui
        // FIXME

        FashionData.get(player).ifPresent(fashion -> {

            // toggle buttons for each render layer that exists in the game for players,
            // both from mods and vanilla
            if (!fashion.getModLayersList().isEmpty())
                for (int i = 0; i < fashion.getModLayersList().size(); i++) {
                    final int index = i;
                    LayerRenderer<?, ?> layer = fashion.getModLayersList().get(index);

                    this.addButton(new FancyButton(guiLeft - 12 - (i % 5) * 10, guiTop + 6 + (i / 5) * 10, layer.getClass().getSimpleName(),
                            b -> toggleLayer((FancyButton) b, layer)).setActive(fashion.keepLayers.contains(layer)));
                }

            // toggle button, with the explicit press ID of 12 (could be anything at this
            // point, it's an artifact for pre 1.14
            this.addButton(new FancyButton(guiLeft + 8, guiTop + ySize / 2 + 14, c -> pressToggle((FancyButton) c)).setActive(fashion.shouldRenderFashion()));

        });

    }

    private int id = 0;

    private void addSlotButton(EnumFashionSlot slot) {

        int offset = 0;
        if (slot.ordinal() > 3)
            offset = 10;

        boolean back = id % 2 == 0;
        Button b = new Button(guiLeft + 105 + (back ? 0 : 55), guiTop + 10 + offset + (id / 2 * 15), 10, 10, new StringTextComponent(back ? "<" : ">"),
                c -> cycle(back, slot));
        this.addButton(b);
        id++;
    }

    @Override
    public void render(MatrixStack mat, int mouseX, int mouseY, float partialTicks) {

        this.drawGuiContainerBackgroundLayer(mat, partialTicks, mouseX, mouseY);

        RenderSystem.disableRescaleNormal();
        RenderHelper.turnOff();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        super.render(mat, mouseX, mouseY, partialTicks);

        RenderHelper.turnBackOn();

        mat.pushPose();
        // RenderSystem.translatef((float) i, (float) j, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        // GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.turnOff();
        this.drawGuiContainerForegroundLayer(mat, mouseX, mouseY);
        RenderHelper.turnBackOn();

        mat.popPose();
        RenderHelper.turnBackOn();

        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        RenderHelper.turnBackOn();

    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack mat, float partialTicks, int mouseX, int mouseY) {

        this.getMinecraft().getTextureManager().bind(BACKGROUND);

        blit(mat, guiLeft + 14, guiTop + 7, xSize, 0, 38, 62);

        FashionData.get(player).ifPresent(fashion -> {
            fashion.setInWardrobe(false); // disable for in gui rendering
        });
        mat.pushPose();

        float rotationMirror_back = ConfigData.face_mirror ? 0.0F : -150F;
        float rotationMirror_front = ConfigData.face_mirror ? 180.0F : 0.0F;

        float flip_mouse = ConfigData.face_mirror ? -1f : 1f;

        float scale = ConfigData.bigger_model ? 1.3f : 1.1f;

        int offset_big_model = (ConfigData.bigger_model ? 5 : 0);

        renderEntityInInventory(guiLeft + 33, guiTop + 65 + offset_big_model, (int) (25f * scale),
                ((guiLeft + 70 - oldMouseX) / 2.5f) * -flip_mouse, guiTop + 40 - oldMouseY, player, rotationMirror_back);

        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(mat, guiLeft, guiTop, 0, 0, xSize, ySize);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();

        renderEntityInInventory((int) ((guiLeft + 68f)), (int) ((guiTop + 82f)), (int) (30f * scale),
                ((guiLeft + 70 - oldMouseX) / 2.5f) * flip_mouse, guiTop + 40 - oldMouseY, player, rotationMirror_front);

        mat.popPose();

        FashionData.get(player).ifPresent(fashion -> {
            fashion.setInWardrobe(true);// set back after drawn for in world rendering
        });
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack mat, int mouseX, int mouseY) {

        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        FashionData.get(player).ifPresent(fashion -> {

            int id = 0;
            for (EnumFashionSlot slot : EnumFashionSlot.values()) {
                ResourceLocation resLoc = fashion.getRenderingPart(slot);
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

                int offset = 0;
                if (id > 3)
                    offset = 10;
                minecraft.font.draw(mat, name, guiLeft + 138 - minecraft.font.width(name) / 2,
                        guiTop + ((id++ + 1) * 15) - 3 + offset, 0xffffff);
            }

            String toggled = fashion.shouldRenderFashion() ? "Showing Fashion" : "Showing Armor";
            minecraft.font.draw(mat, toggled, guiLeft + xSize / 2 - 68, guiTop + ySize / 2 + 14, 0xffffff);

            // tracking player view !!
            this.oldMouseX = (float) mouseX;
            this.oldMouseY = Math.min((float) mouseY, guiTop + 50);

            for (Widget guiButton : buttons) {
                if (guiButton instanceof FancyButton) {
                    FancyButton gfb = (FancyButton) guiButton;
                    if (gfb.isMouseOver(mouseX, mouseY) && !gfb.isSwitch()) {
                        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(gfb.name));
                        Style style = Style.EMPTY.withHoverEvent(hover);
                        this.renderComponentHoverEffect(mat, style, mouseX, mouseY);
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

            NetworkHandler.NETWORK.sendToServer(new PacketSyncPlayerFashionToServer(fashion.getAllRenderedParts(), fashion.shouldRenderFashion(),
                    fashion.getSimpleNamesForToggledFashionLayers()));

            fashion.setInWardrobe(false);
        });

        // no need for super. super only calls to throw out item stacks

    }

    @Override
    public boolean keyPressed(int keysym, int scancode, int p_keyPressed_3_) {

        if (KeyRegistry.keyWardrobe.getKeyBinding().matches(keysym, scancode)) {
            this.minecraft.player.closeContainer();
            return true;
        }

        return super.keyPressed(keysym, scancode, p_keyPressed_3_);
    }

    ////////////////
    ////////////
    ///////////////
    ////////////////

    private void cycle(boolean back, EnumFashionSlot slot) {

        FashionData.get(player).ifPresent(fashion -> {
            if (fashion.shouldRenderFashion()) {

                ResourceLocation name = fashion.getRenderingPart(slot);
                ResourceLocation newPart = back ? ResourcePackReader.getPreviousClothes(slot, name) : ResourcePackReader.getNextClothes(slot, name);

                fashion.updateFashionSlot(newPart, slot);
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

    public static void renderEntityInInventory(int x, int y, int scale, float mouseX, float mouseY, LivingEntity player, float rotateY) {


        float lookX = (float) Math.atan((double) (mouseX / 40.0F));
        float lookY = (float) Math.atan((double) (mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) x, (float) y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack stack = new MatrixStack();
        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale((float) scale, (float) scale, (float) scale);
        Quaternion rotateZ = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion rotateX = Vector3f.XP.rotationDegrees(lookY * 20.0F);
        //rotateZ.mul(rotateX);
        stack.mulPose(rotateZ);
        float lvt_11_1_ = player.yBodyRot;
        float lvt_12_1_ = player.yRot;
        float lvt_13_1_ = player.xRot;
        float lvt_14_1_ = player.yHeadRotO;
        float lvt_15_1_ = player.yHeadRot;
        player.yBodyRot = 180.0F + lookX * 20.0F;
        player.yRot = 180.0F + lookX * 40.0F;
        player.xRot = -lookY * 20.0F;
        player.yHeadRot = player.yRot;
        player.yHeadRotO = player.yRot;
        EntityRendererManager lvt_16_1_ = Minecraft.getInstance().getEntityRenderDispatcher();
        rotateX.conj();
        lvt_16_1_.overrideCameraOrientation(rotateX);
        lvt_16_1_.setRenderShadow(false);
        IRenderTypeBuffer.Impl lvt_17_1_ = Minecraft.getInstance().renderBuffers().bufferSource();
        stack.mulPose(new Quaternion(0, rotateY, 0, true));
        RenderSystem.runAsFancy(() -> {
            lvt_16_1_.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, lvt_17_1_, 15728880);
        });
        lvt_17_1_.endBatch();
        lvt_16_1_.setRenderShadow(true);
        player.yBodyRot = lvt_11_1_;
        player.yRot = lvt_12_1_;
        player.xRot = lvt_13_1_;
        player.yHeadRotO = lvt_14_1_;
        player.yHeadRot = lvt_15_1_;
        RenderSystem.popMatrix();
    }
}
