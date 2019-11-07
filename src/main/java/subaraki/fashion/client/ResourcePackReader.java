package subaraki.fashion.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
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

    private static ModelHandle rod = ModelHandle.of("item/blaze_rod");

    public ResourcePackReader() {

        registerReloadListener();
        loadFashionPacks();
    }

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

    private static List<ResourceLocation> weaponTextures = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> shieldTextures = Lists.<ResourceLocation>newArrayList();

    private static LinkedHashMap<ModelHandle, Boolean> weapons = new LinkedHashMap<ModelHandle, Boolean>();
    private static List<ModelHandle> shields = Lists.<ModelHandle>newArrayList();
    private static List<ModelHandle> shieldsBlocking = Lists.<ModelHandle>newArrayList();

    public void loadFashionPacks() {

        hats.clear();
        body.clear();
        legs.clear();
        boots.clear();

        weaponTextures.clear();
        weapons.clear();

        shields.clear();
        shieldsBlocking.clear();

        shieldTextures.clear();

        addHats(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_hat.png"));
        addBody(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_body.png"));
        addLegs(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_pants.png"));
        addBoots(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_boots.png"));

        // shield placeholders are coded to always pair with a resloc. weapons however
        // can be items and not have a resloc associated.
        // the base logic for getting shields and waepons is seperated by the fact that
        // shields are get by resloc and weapons by model
        addWeaponModel(null, false); // placeholder for empty spot

        addShieldModel(null, null); // placeholder for empty spot
        addShieldTextures(null);// placeholder for empty spot. for shields this is requiered.

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

    public static void addWeaponModel(ResourceLocation model, boolean isItem) {

        if (model == null) {
            weapons.put((ModelHandle) null, isItem);
            Fashion.log.warn(String.format(
                    "%n TRIED REGISTERING A NULL WEAPON MODEL %n This is normal the first time for empty placeholders. %n If this happens more then once, check your Resource Pack json for any errors!"));
            return;
        }

        weapons.put(ModelHandle.of(model), isItem);
    }

    public static void addWeaponTextures(ResourceLocation resLoc) {

        weaponTextures.add(resLoc);
    }

    public static void addShieldModel(ResourceLocation model, ResourceLocation modelBlocking) {

        if (model == null || modelBlocking == null) {
            shields.add((ModelHandle) null);
            shieldsBlocking.add((ModelHandle) null);

            Fashion.log.warn(String.format(
                    "%n TRIED REGISTERING A NULL SHIELD MODEL %n This is normal the first time for empty placeholders. %n If this happens more then once, check your Resource Pack json for any errors!"));
            return;
        }
        shields.add(ModelHandle.of(model));
        shieldsBlocking.add(ModelHandle.of(modelBlocking));

    }

    public static void addShieldTextures(ResourceLocation resLoc) {

        shieldTextures.add(resLoc);
    }

    public static ModelHandle getAestheticWeapon(ResourceLocation resloc) {

        for (ModelHandle handle : weapons.keySet()) {
            if (handle != null)
                if (handle.getModel().equals(resloc))
                    return handle;
        }

        return rod;
    }

    public static boolean isItem(ResourceLocation resloc) {

        for (ModelHandle handle : weapons.keySet()) {
            if (handle != null)
                if (handle.getModel().equals(resloc))
                    return weapons.get(handle);
        }

        return false;
    }

    public static ModelHandle getAestheticShield(ResourceLocation resloc, boolean isBlocking) {

        if (!isBlocking) {
            for (ModelHandle handle : shields) {
                if (handle != null)
                    if (handle.getModel().equals(resloc))
                        return handle;
            }
        } else {
            for (ModelHandle handle : shieldsBlocking) {
                if (handle != null)
                    if (handle.getModel().equals(resloc))
                        return handle;
            }
        }

        return rod;
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
                        String path = "textures/" + pack + "/hats/" + array.get(i).getAsString();
                        addHats(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("body")) {
                    JsonArray array = json.getAsJsonArray("body");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "textures/" + pack + "/body/" + array.get(i).getAsString();
                        addBody(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("pants")) {
                    JsonArray array = json.getAsJsonArray("pants");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "textures/" + pack + "/pants/" + array.get(i).getAsString();
                        addLegs(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("boots")) {
                    JsonArray array = json.getAsJsonArray("boots");
                    for (int i = 0; i < array.size(); i++) {
                        String path = "textures/" + pack + "/boots/" + array.get(i).getAsString();
                        addBoots(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                // prefixes 'models' and 'textures' aren't needed here. the stitch event and
                // modelloader automatically add that
                if (json.has("weapon_models")) {

                    JsonArray array = json.getAsJsonArray("weapon_models");

                    boolean flag = false;
                    String path = pack + "/weapons/";
                    for (JsonElement el : array) {
                        String name = el.getAsString();
                        flag = name.contains("item/");
                        String base = name.replace("item/", "");
                        String fullpath = path + base;
                        addWeaponModel(new ResourceLocation(Fashion.MODID, fullpath), flag);
                    }
                }

                if (json.has("weapon_textures")) {
                    JsonArray array = json.getAsJsonArray("weapon_textures");
                    for (int i = 0; i < array.size(); i++) {
                        String name = array.get(i).getAsString();
                        String path = pack + "/weapons/" + name.replace("item/", "");
                        addWeaponTextures(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("shield_models")) {
                    JsonArray array = json.getAsJsonArray("shield_models");
                    for (int i = 0; i < array.size(); i++) {
                        String path = pack + "/shields/" + array.get(i).getAsString();
                        String pathBlock = path + "_blocking";
                        addShieldModel(new ResourceLocation(Fashion.MODID, path), new ResourceLocation(Fashion.MODID, pathBlock));
                    }
                }

                if (json.has("shield_textures")) {
                    JsonArray array = json.getAsJsonArray("shield_textures");
                    for (int i = 0; i < array.size(); i++) {
                        String path = pack + "/shields/" + array.get(i).getAsString();
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

    public static ResourceLocation getNextClothes(EnumFashionSlot slot, ResourceLocation part_name) {

        List<ResourceLocation> resLocs = getListForSlot(slot);
        int index = 0;

        for (ResourceLocation name : resLocs) {
            if (name.equals(part_name))
                break; // break out of loop. index saved is the next one.
            index++;
        }

        if (index < resLocs.size() - 1) // if the id is smaller then the size of the array, we can get the next item
            return resLocs.get(++index);
        else
            return resLocs.get(0); // if the id is equal or bigger then the size (equal is more anyway) we need to
                                   // loop back to the first item
    }

    public static ResourceLocation getPreviousClothes(EnumFashionSlot slot, ResourceLocation part_name) {

        List<ResourceLocation> resLocs = getListForSlot(slot);
        int index = 0;

        for (ResourceLocation name : resLocs) {
            if (name.equals(part_name))
                break; // break out of loop. index saved is the next one.
            index++;
        }
        if (index > 0) // if the id is smaller then the size of the array, we can get the next item
            return resLocs.get(--index);
        else
            return resLocs.get(resLocs.size() - 1); // if the id is equal or bigger then the size (equal is more anyway) we need to
        // loop back to the first item
    }

    public static List<ResourceLocation> getListForSlot(EnumFashionSlot slot) {

        switch (slot) {
        case HEAD:
            return hats;
        case CHEST:
            return body;
        case LEGS:
            return legs;
        case BOOTS:
            return boots;
        case WEAPON:
            return toArrayList(slot);
        case SHIELD:
            return toArrayList(slot);
        default:
            return Lists.newArrayList();
        }
    }

    private static List<ResourceLocation> toArrayList(EnumFashionSlot slot) {

        List<ResourceLocation> resLoc = Lists.newArrayList();

        switch (slot) {
        case WEAPON: {

            for (ModelHandle handle : weapons.keySet())
                if (handle != null)
                    resLoc.add(handle.getModel());
                else
                    resLoc.add(new ResourceLocation("missing"));
            break;
        }
        case SHIELD: {
            for (ModelHandle handle : shields)
                if (handle != null)
                    resLoc.add(handle.getModel());
                else
                    resLoc.add(new ResourceLocation("missing"));
            break;
        }
        default:
            break;
        }

        return resLoc;
    }

    public static List<ResourceLocation> getTexturesForStitcher(EnumFashionSlot slot) {

        switch (slot) {
        case WEAPON:
            return weaponTextures;
        case SHIELD:
            return shieldTextures;
        default: {
            Fashion.log.error("Tried stitching textures for something else then weapons or shields. this is not necesairy ! skipping process...");
            return Lists.newArrayList();
        }
        }
    }

    public static int getWeaponSize() {

        return weapons.size();
    }
}
