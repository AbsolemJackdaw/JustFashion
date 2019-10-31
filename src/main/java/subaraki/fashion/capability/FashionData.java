package subaraki.fashion.capability;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import subaraki.fashion.client.render.layer.LayerWardrobe;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;

public class FashionData {

    private PlayerEntity player;

    private boolean renderFashion;
    private boolean inWardrobe;

    private int hatIndex;
    private int bodyIndex;
    private int legsIndex;
    private int bootsIndex;
    private int weaponIndex;
    private int shieldIndex;

    /** Cache of original list with layers attached to the player */
    public List<LayerRenderer<?, ?>> cachedOriginalRenderList = null;
    /** List of all fashion layers rendered, independant of original list */
    public List<LayerRenderer<?, ?>> fashionLayers = Lists.newArrayList();

    /** List saved with all layers to be rendered */
    private List<LayerRenderer<?, ?>> savedOriginalList = Lists.newArrayList();
    /** Layers that ought to be kept rendered independant from Fashion Layers */
    public List<LayerRenderer<?, ?>> keepLayers = Lists.newArrayList();

    /** Get the list of items that need to be rendered to the player */
    public List<LayerRenderer<?, ?>> getSavedOriginalList() {

        List<LayerRenderer<?, ?>> copy = new ArrayList<>();
        copy = savedOriginalList;
        return copy;
    }

    public void resetSavedOriginalList() {

        savedOriginalList.clear();
    }

    /**
     * Copy over all layers that aren't vanilla layers to the savedOriginalList,
     * from the original cached list. This will be used to enable a toggle in the
     * gui.
     * 
     * This is called only once
     * 
     * This is basically the anti fashion layers. armor vs fashion. under armor is
     * understood any biped body armor, anything that can be put on the player's
     * head (pumpkins etc), the deadmouse special layer, held item , the arrows
     * stuck in the player the vanilla cape and the elytra as well as the wardrobe
     * that can not be toggled
     */
    public void saveVanillaList(List<LayerRenderer<?, ?>> original) {

        List<LayerRenderer<?, ?>> copy = Lists.newArrayList();

        // Remove unneeded layers
        for (LayerRenderer<?, ?> layer : original) {
            if ((layer instanceof BipedArmorLayer) || (layer instanceof LayerWardrobe) || (layer instanceof HeadLayer) || (layer instanceof Deadmau5HeadLayer)
                    || (layer instanceof HeldItemLayer) || (layer instanceof ArrowLayer) || (layer instanceof CapeLayer) || (layer instanceof ElytraLayer)
                    || (layer instanceof SpinAttackEffectLayer))
                continue;

            copy.add(layer);
        }

        savedOriginalList.clear();

        for (LayerRenderer<?, ?> layer : copy)
        {
            savedOriginalList.add(layer);
        }
        
        
    }

    public FashionData() {

    }

    public PlayerEntity getPlayer() {

        return player;
    }

    public void setPlayer(PlayerEntity newPlayer) {

        this.player = newPlayer;
    }

    public static FashionData get(PlayerEntity player) {

        return player.getCapability(FashionCapability.CAPABILITY).orElse(null);
    }

    public INBT writeData() {

        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("renderFashion", renderFashion);
        tag.putInt("hat", hatIndex);
        tag.putInt("body", bodyIndex);
        tag.putInt("legs", legsIndex);
        tag.putInt("boots", bootsIndex);
        tag.putInt("weapon", weaponIndex);
        tag.putInt("shield", shieldIndex);
       
        Fashion.log.debug(keepLayers.isEmpty());
        
        if (!keepLayers.isEmpty()) {
            tag.putInt("size", keepLayers.size());
            for (int i = 0; i < keepLayers.size(); i++) {
                tag.putString("keep_" + i, keepLayers.get(i).getClass().getSimpleName());
                Fashion.log.debug("added a layer to save : " + keepLayers.get(i).getClass().getSimpleName());
            }
        }

        return tag;
    }

    public void readData(INBT nbt) {

        CompoundNBT tag = ((CompoundNBT) nbt);

        renderFashion = tag.getBoolean("renderFashion");
        hatIndex = tag.getInt("hat");
        bodyIndex = tag.getInt("body");
        legsIndex = tag.getInt("legs");
        bootsIndex = tag.getInt("boots");
        weaponIndex = tag.getInt("weapon");
        shieldIndex = tag.getInt("shield");

        if (tag.contains("size")) {
            int size = tag.getInt("size");
            for (int i = 0; i < size; i++) {
                String name = tag.getString("keep_" + i);

                for (LayerRenderer<?, ?> layer : getSavedOriginalList()) {
                    if (layer.getClass().getSimpleName().equals(name)) {
                        keepLayers.add(layer);
                        Fashion.log.debug(layer.getClass().getSimpleName() + " got loaded as active");
                    }
                }
            }
        }
    }

    /** Switch on wether or not to render fashion */
    public boolean shouldRenderFashion() {

        return renderFashion;
    }

    public List<String> getSimpleNamesForToggledFashionLayers() {

        if (keepLayers != null && !keepLayers.isEmpty()) {
            List<String> layers = new ArrayList<String>();
            for (LayerRenderer<?, ?> layer : keepLayers)
                layers.add(layer.getClass().getSimpleName());
            return layers;
        }
        return new ArrayList<String>();
    }

    public void setRenderFashion(boolean renderFashion) {

        this.renderFashion = renderFashion;
    }

    /** Inverts the shouldRenderFashion boolean */
    public void toggleRenderFashion() {

        this.renderFashion = !renderFashion;
    }

    /**
     * Returns the index at which the player renders fashion at the given moment for
     * the given fashion slot enum
     */
    public int getPartIndex(EnumFashionSlot slot) {

        switch (slot) {
        case HEAD:
            return hatIndex;
        case CHEST:
            return bodyIndex;
        case LEGS:
            return legsIndex;
        case BOOTS:
            return bootsIndex;
        case WEAPON:
            return weaponIndex;
        case SHIELD:
            return shieldIndex;

        default:
            return 0;
        }
    }

    /**
     * Returns the index for all fashion parts currently rendered on the player.
     * This is mainly used in saving data and passing data trough packets
     */
    public int[] getAllParts() {

        return new int[] { hatIndex, bodyIndex, legsIndex, bootsIndex, weaponIndex, shieldIndex };
    }

    /** Change the index for the given fashion slot to a new index */
    public void updatePartIndex(int id, EnumFashionSlot slot) {

        switch (slot) {
        case HEAD:
            hatIndex = id;
            break;
        case CHEST:
            bodyIndex = id;
            break;
        case LEGS:
            legsIndex = id;
            break;
        case BOOTS:
            bootsIndex = id;
            break;
        case WEAPON:
            weaponIndex = id;
            break;
        case SHIELD:
            shieldIndex = id;
            break;

        default:
            break;
        }
    }

    /** Set wether or not the player should be rendered in the wardrobe */
    public void setInWardrobe(boolean inWardrobe) {

        this.inWardrobe = inWardrobe;
    }

    /**
     * Wether or not the player should be rendered with the wardrobe layer or not
     */
    public boolean isInWardrobe() {

        return inWardrobe;
    }
}
