package subaraki.fashion.capability;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.EnumFashionSlot;
import subaraki.fashion.render.layer.LayerWardrobe;
import subaraki.fashion.util.ResourcePackReader;

import java.util.List;

public class FashionData {

    public static final int MOD = 1;
    public static final int VANILLA = 0;
    private static final ResourceLocation MISSING_FASHION = new ResourceLocation(Fashion.MODID, "/textures/fashion/missing_fasion.png");
    /**
     * Cache of original list with layers attached to the player
     */
    public List<RenderLayer<?, ?>> cachedOriginalRenderList = null;
    /**
     * List of all fashion layers rendered, independent of original list
     */
    public List<RenderLayer<?, ?>> fashionLayers = Lists.newArrayList();
    /**
     * Layers that ought to be kept rendered independent from Fashion Layers
     */
    private List<RenderLayer<?, ?>> keepLayers = Lists.newArrayList();
    private List<String> keepLayersNames = Lists.newArrayList();
    private Player player;
    private boolean renderFashion;
    private boolean inWardrobe;
    private ResourceLocation hatIndex, bodyIndex, legsIndex, bootsIndex, weaponIndex, shieldIndex;
    /**
     * all layers, both vanilla and external mod
     */
    private List<List<RenderLayer<?, ?>>> savedLayers = Lists.newArrayList();

    /**
     * since  1.17, the player gets a new renderer and new layers on resource reload.
     * this Field allows to check if the previous player renderer is the same as the current.
     * If tis not the case, clear all cache so the new player renderer layers can be cached correctly.
     */
    private PlayerRenderer cachedPreviousPlayerRenderer = null;

    public FashionData() {

    }

    public static LazyOptional<FashionData> get(Player player) {

        return player.getCapability(FashionCapability.CAPABILITY, null);
    }

    public void checkResourceReloadAndReset(PlayerRenderer playerRenderer) {
        if (cachedPreviousPlayerRenderer != playerRenderer) {
            Fashion.log.info("Fashion has reset layers to accommodate reloaded player renderer");
            cachedPreviousPlayerRenderer = playerRenderer;
            resetLayerCache();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void resetLayerCache() {
        savedLayers.clear();
        keepLayers.clear();
        fashionLayers.clear();
        cachedOriginalRenderList = null;
    }

    public void addLayerToKeep(String name) {
        if (!keepLayersNames.contains(name))
            keepLayersNames.add(name);
    }

    public void removeLayerFromKeep(String name) {
        if (keepLayersNames.contains(name))
            keepLayersNames.remove(name);
    }

    public void addLayersToKeep(List<String> addAll) {
        for (String name : addAll)
            addLayerToKeep(name);
    }

    //clear cache of keepLayers for packets and syncing purposes.
    public void resetKeepLayerForDistantPlayer() {
        keepLayers.clear();
    }

    public boolean hasOtherModLayersToRender() {
        return keepLayersNames.isEmpty();
    }

    public List<String> getKeepLayerNames() {
        return keepLayersNames;
    }

    public List<RenderLayer<?, ?>> getLayersToKeep() {
        if (keepLayers.size() != keepLayersNames.size())
            keepLayers.clear();

        if (keepLayers.isEmpty()) {
            for (String name : keepLayersNames)
                for (RenderLayer<?, ?> layer : getOtherModLayersList()) {
                    if (layer.getClass().getSimpleName().equals(name)) {
                        keepLayers.add(layer);
                    }
                }
        }
        return keepLayers;
    }

    /**
     * list of all mod layers, cached
     */
    public List<RenderLayer<?, ?>> getOtherModLayersList() {

        if (!savedLayers.isEmpty())
            return ImmutableList.copyOf(savedLayers.get(MOD));
        return Lists.newArrayList();
    }

    /**
     * list of all vanilla layers, cached
     */
    public List<RenderLayer<?, ?>> getVanillaLayersList() {

        if (!savedLayers.isEmpty())
            return ImmutableList.copyOf(savedLayers.get(VANILLA));
        return Lists.newArrayList();

    }

    public List<List<RenderLayer<?, ?>>> getSavedLayers() {

        return savedLayers;
    }

    /**
     * Copy over all layers that aren't vanilla layers to the savedOriginalList,
     * from the original cached list. This will be used to enable a toggle in the
     * gui.
     * <p>
     * This is called only once
     * <p>
     * This is basically the anti fashion layers. armor vs fashion. under armor is
     * understood any biped body armor. anything that can be put on the player's
     * head (pumpkins etc), the deadmouse special layer, held item , the arrows
     * stuck in the player the vanilla cape and the elytra as well as the wardrobe
     * that can not be toggled
     */
    public void saveOriginalList(List<RenderLayer<?, ?>> fromPlayerRenderer) {

        List<RenderLayer<?, ?>> copyModLayers = Lists.newArrayList();
        List<RenderLayer<?, ?>> copyVanillaLayers = Lists.newArrayList();

        // seperate vanilla layers from mod layers, so mod layers can be toggled
        for (RenderLayer<?, ?> layer : fromPlayerRenderer) {
            if ((layer instanceof HumanoidArmorLayer) || (layer instanceof ItemInHandLayer) || (layer instanceof LayerWardrobe) || (layer instanceof CustomHeadLayer)
                    || (layer instanceof Deadmau5EarsLayer) || (layer instanceof ArrowLayer) || (layer instanceof CapeLayer) || (layer instanceof ElytraLayer)
                    || (layer instanceof SpinAttackEffectLayer)) {
                copyVanillaLayers.add(layer);
                continue;
            }

            copyModLayers.add(layer);
        }

        savedLayers.clear();
        for (int i = 0; i < 2; i++)
            savedLayers.add(Lists.newArrayList());
        savedLayers.get(VANILLA).addAll(copyVanillaLayers);
        savedLayers.get(MOD).addAll(copyModLayers);
    }

    public Player getPlayer() {

        return player;
    }

    public void setPlayer(Player newPlayer) {

        this.player = newPlayer;
    }

    public Tag writeData() {

        CompoundTag tag = new CompoundTag();
        tag.putBoolean("renderFashion", renderFashion);

        if (hatIndex == null)
            hatIndex = getRenderingPart(EnumFashionSlot.HEAD);
        tag.putString("hat", hatIndex.toString());

        if (bodyIndex == null)
            bodyIndex = getRenderingPart(EnumFashionSlot.CHEST);
        tag.putString("body", bodyIndex.toString());

        if (legsIndex == null)
            legsIndex = getRenderingPart(EnumFashionSlot.LEGS);
        tag.putString("legs", legsIndex.toString());

        if (bootsIndex == null)
            bootsIndex = getRenderingPart(EnumFashionSlot.BOOTS);
        tag.putString("boots", bootsIndex.toString());

        if (weaponIndex == null)
            weaponIndex = getRenderingPart(EnumFashionSlot.WEAPON);
        tag.putString("weapon", weaponIndex.toString());

        if (shieldIndex == null)
            shieldIndex = getRenderingPart(EnumFashionSlot.SHIELD);
        tag.putString("shield", shieldIndex.toString());

        if (!keepLayersNames.isEmpty()) {
            tag.putInt("size", keepLayersNames.size());
            for (int i = 0; i < keepLayersNames.size(); i++) {
                tag.putString("keep_" + i, keepLayersNames.get(i));
                Fashion.log.debug("added a layer to save : " + keepLayersNames.get(i) + " " + i);
            }
        }

        return tag;
    }

    public void readData(Tag nbt) {

        CompoundTag tag = ((CompoundTag) nbt);

        renderFashion = tag.getBoolean("renderFashion");
        hatIndex = new ResourceLocation(tag.getString("hat"));
        bodyIndex = new ResourceLocation(tag.getString("body"));
        legsIndex = new ResourceLocation(tag.getString("legs"));
        bootsIndex = new ResourceLocation(tag.getString("boots"));
        weaponIndex = new ResourceLocation(tag.getString("weapon"));
        shieldIndex = new ResourceLocation(tag.getString("shield"));

        keepLayersNames.clear();

        if (tag.contains("size")) {
            int size = tag.getInt("size");
            for (int i = 0; i < size; i++) {
                String name = tag.getString("keep_" + i);

                keepLayersNames.add(name);
                Fashion.log.debug(name + " got loaded as active");
            }
        }
    }

    /**
     * Switch on wether or not to render fashion
     */
    public boolean shouldRenderFashion() {

        return renderFashion;
    }

    public void setRenderFashion(boolean renderFashion) {

        this.renderFashion = renderFashion;
    }

    /**
     * Inverts the shouldRenderFashion boolean
     */
    public void toggleRenderFashion() {

        this.renderFashion = !renderFashion;
    }

    /**
     * Returns the index at which the player renders fashion at the given moment for
     * the given fashion slot enum
     */
    public ResourceLocation getRenderingPart(EnumFashionSlot slot) {

        // try and get the first value of the needed list.
        // when using missing fashion as a default, it will not find it on first
        // itteration when opening a new world,
        // and you need to press twice to start cycling the fashion
        ResourceLocation DEFAULT = MISSING_FASHION;

        if (!ResourcePackReader.getListForSlot(slot).isEmpty())
            if (ResourcePackReader.getListForSlot(slot).get(0) != null)
                DEFAULT = ResourcePackReader.getListForSlot(slot).get(0);

        // when a resource location is null, it has it's name set to minecraft:missing in
        // packets. We resolve that here, and transform null / missing to default
        boolean flag = getAllRenderedParts()[slot.ordinal()] != null && getAllRenderedParts()[slot.ordinal()].toString().contains("missing");


        return switch (slot) {
            case HEAD -> hatIndex != null && !flag ? hatIndex : DEFAULT;
            case CHEST -> bodyIndex != null && !flag ? bodyIndex : DEFAULT;
            case LEGS -> legsIndex != null && !flag ? legsIndex : DEFAULT;
            case BOOTS -> bootsIndex != null && !flag ? bootsIndex : DEFAULT;
            case WEAPON -> weaponIndex != null && !flag ? weaponIndex : DEFAULT;
            case SHIELD -> shieldIndex != null && !flag ? shieldIndex : DEFAULT;
        };
    }

    /**
     * Returns the index for all fashion parts currently rendered on the player.
     * This is mainly used in saving data and passing data trough packets
     */
    public ResourceLocation[] getAllRenderedParts() {

        return new ResourceLocation[]{hatIndex, bodyIndex, legsIndex, bootsIndex, weaponIndex, shieldIndex};
    }

    /**
     * Change the index for the given fashion slot to a new index
     */
    public void updateFashionSlot(ResourceLocation partname, EnumFashionSlot slot) {

        switch (slot) {
            case HEAD -> {
                hatIndex = partname;
            }
            case CHEST -> {
                bodyIndex = partname;
            }
            case LEGS -> {
                legsIndex = partname;
            }
            case BOOTS -> {
                bootsIndex = partname;
            }
            case WEAPON -> {
                weaponIndex = partname;
            }
            case SHIELD -> {
                shieldIndex = partname;
            }
        }
    }

    /**
     * Wether or not the player should be rendered with the wardrobe layer or not
     */
    public boolean isInWardrobe() {

        return inWardrobe;
    }

    /**
     * Set wether or not the player should be rendered in the wardrobe
     */
    public void setInWardrobe(boolean inWardrobe) {

        this.inWardrobe = inWardrobe;
    }
}
