package subaraki.fashion.handler.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lib.modelloader.ModelHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;

public class ResourcePackReader {

    public void registerReloadListener() {

        Fashion.log.debug("Loading up the Resource Pack Reader");
        IResourceManager rm = Minecraft.getInstance().getResourceManager();
        if (rm instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) rm).addReloadListener((ISelectiveResourceReloadListener) (resourceManager, resourcePredicate) -> {
                if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
                    loadFashionPacks();
                }
            });
        }
    }

    private static List<ResourceLocation> hats = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> body = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> legs = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> boots = Lists.<ResourceLocation>newArrayList();

    private static List<ResourceLocation> weapon = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> weaponTextures = Lists.<ResourceLocation>newArrayList();

    private static List<ResourceLocation> shield = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> shieldBlocking = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> shieldTexture = Lists.<ResourceLocation>newArrayList();

    private static List<ModelHandle> aestheticWeapons = Lists.<ModelHandle>newArrayList();
    private static List<ModelHandle> aestheticShield = Lists.<ModelHandle>newArrayList();
    private static List<ModelHandle> aestheticShieldBlocking = Lists.<ModelHandle>newArrayList();

    private static final ResourceLocation MISSING_FASHION = new ResourceLocation(Fashion.MODID, "/textures/fashion/missing_fasion.png");

    public void loadFashionPacks() {

        hats.clear();
        body.clear();
        legs.clear();
        boots.clear();

        weapon.clear();
        weaponTextures.clear();
        aestheticWeapons.clear();

        shield.clear();
        shieldBlocking.clear();
        shieldTexture.clear();
        aestheticShield.clear();
        aestheticShieldBlocking.clear();

        addHats(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_hat.png"));
        addBody(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_body.png"));
        addLegs(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_pants.png"));
        addBoots(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_boots.png"));

        addWeaponModel(new ResourceLocation(Fashion.MODID, "blank_weapon"));
        addWeaponTextures(null);

        addShieldModel(null, null); // placeholder for empty spot
        addShieldTextures(null);

        loadFromJson();
    }

    public static void addHats(ResourceLocation resLoc) {

        hats.add(resLoc);
    }

    public static void addBody(ResourceLocation resLoc) {

        body.add(resLoc);
    }

    public static void addLegs(ResourceLocation resLoc) {

        legs.add(resLoc);
    }

    public static void addBoots(ResourceLocation resLoc) {

        boots.add(resLoc);
    }

    public static void addWeaponModel(ResourceLocation model) {

        weapon.add(model);
        aestheticWeapons.add(ModelHandle.of(model));
    }

    public static void addWeaponTextures(ResourceLocation resLoc) {

        weaponTextures.add(resLoc);
    }

    public static void addShieldModel(ResourceLocation model, ResourceLocation modelBlocking) {

        shield.add(model);
        shieldBlocking.add(modelBlocking);
        if (model != null)
            aestheticShield.add(ModelHandle.of(model));
        if (modelBlocking != null)
            aestheticShieldBlocking.add(ModelHandle.of(modelBlocking));
    }

    public static void addShieldTextures(ResourceLocation resLoc) {

        shieldTexture.add(resLoc);
    }

    public static ModelHandle getAestheticWeapon(int index) {

        if (index < aestheticWeapons.size())
            return aestheticWeapons.get(index);

        return ModelHandle.of("item/blaze_rod");
    }

    public static ModelHandle getAestheticShield(int index, boolean isBlocking) {

        index--; // offset with empty first slot
        if (!isBlocking) {
            if (index < aestheticShield.size())
                return aestheticShield.get(index);
        } else {
            if (index < aestheticShieldBlocking.size())
                return aestheticShieldBlocking.get(index);
        }

        return ModelHandle.of("item/blaze_rod");
    }

    private void loadFromJson() {

        try {
            List<IResource> jsons = Minecraft.getInstance().getResourceManager().getAllResources(new ResourceLocation(Fashion.MODID, "fashionpack.json"));
            Gson gson = new GsonBuilder().create();

            for (IResource res : jsons) {
                InputStream stream = res.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                JsonElement je = gson.fromJson(reader, JsonElement.class);
                JsonObject json = je.getAsJsonObject();

                if (!json.has("pack")) {
                    Fashion.log.warn(res.getPackName() + "'s fashion pack did not contain a pack id !");
                    continue;
                }
                String pack = json.get("pack").getAsString();

                if (json.has("hats")) {
                    JsonArray array = json.getAsJsonArray("hats");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "textures/fashionpack/" + pack + "/hats/" + array.get(i).getAsString();
                        addHats(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("body")) {
                    JsonArray array = json.getAsJsonArray("body");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "textures/fashionpack/" + pack + "/body/" + array.get(i).getAsString();
                        addBody(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("pants")) {
                    JsonArray array = json.getAsJsonArray("pants");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "textures/fashionpack/" + pack + "/pants/" + array.get(i).getAsString();
                        addLegs(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("boots")) {
                    JsonArray array = json.getAsJsonArray("boots");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "textures/fashionpack/" + pack + "/boots/" + array.get(i).getAsString();
                        addBoots(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                // prefixes 'models' and 'textures' aren't needed here. the stitch event and
                // modelloader automatically add that
                if (json.has("weapon_models")) {
                    JsonArray array = json.getAsJsonArray("weapon_models");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "fashionpack/" + pack + "/weapons/" + array.get(i).getAsString();
                        addWeaponModel(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("weapon_textures")) {
                    JsonArray array = json.getAsJsonArray("weapon_textures");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "fashionpack/" + pack + "/weapons/" + array.get(i).getAsString();
                        addWeaponTextures(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("shield_models")) {
                    JsonArray array = json.getAsJsonArray("shield_models");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "fashionpack/" + pack + "/shields/" + array.get(i).getAsString();
                        String pathBlock = path + "_blocking";
                        addShieldModel(new ResourceLocation(Fashion.MODID, path), new ResourceLocation(Fashion.MODID, pathBlock));
                    }
                }

                if (json.has("shield_textures")) {
                    JsonArray array = json.getAsJsonArray("shield_textures");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "fashionpack/" + pack + "/shields/" + array.get(i).getAsString();
                        addShieldTextures(new ResourceLocation(Fashion.MODID, path));
                    }
                }
            }
        } catch (IOException e) {
            Fashion.log.warn("************************************");
            Fashion.log.warn("!*!*!*!*!");
            Fashion.log.warn("No FashionPacks Detected. You will not be able to equip any fashion.");
            Fashion.log.warn("Make sure to select or set some in the resourcepack gui !");
            Fashion.log.warn("!*!*!*!*!");
            Fashion.log.warn("************************************");

            e.printStackTrace();
        }
    }

    public static ResourceLocation getResourceForPart(EnumFashionSlot slot, int partIndex) {

        switch (slot) {
        case HEAD:
            return partIndex >= hats.size() ? MISSING_FASHION : hats.get(partIndex);
        case CHEST:
            return partIndex >= body.size() ? MISSING_FASHION : body.get(partIndex);
        case LEGS:
            return partIndex >= legs.size() ? MISSING_FASHION : legs.get(partIndex);
        case BOOTS:
            return partIndex >= boots.size() ? MISSING_FASHION : boots.get(partIndex);
        case WEAPON:
            return partIndex >= weapon.size() ? MISSING_FASHION : weapon.get(partIndex);
        case SHIELD:
            return partIndex >= shield.size() ? MISSING_FASHION : shield.get(partIndex);
        default:
            return MISSING_FASHION;
        }
    }

    public static ResourceLocation getTextureForStitcher(EnumFashionSlot slot, int partIndex) {

        switch (slot) {
        case WEAPON:
            return partIndex >= weaponTextures.size() ? new ResourceLocation("minecraft:items/blaze_rod") : weaponTextures.get(partIndex);
        case SHIELD:
            return partIndex >= shieldTexture.size() ? new ResourceLocation("minecraft:items/blaze_rod") : shieldTexture.get(partIndex);

        default:
            return MISSING_FASHION;
        }
    }

    public static int partsSize(EnumFashionSlot slot) {

        switch (slot) {
        case HEAD:
            return hats.size();
        case CHEST:
            return body.size();
        case LEGS:
            return legs.size();
        case BOOTS:
            return boots.size();
        case WEAPON:
            return weapon.size();
        case SHIELD:
            return shield.size();
        default:
            return 0;
        }
    }
}
