package subaraki.fashion.client;

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

    private static ResourceLocation rod = new ResourceLocation("item/blaze_rod");

    public ResourcePackReader() {

        // cannot reload textures and models anymore due to model loading changing in
        // 1.16
        // registerReloadListener();
        loadFashionPacks();
    }

    public void registerReloadListener()
    {

        Fashion.log.debug("Loading up the Resource Pack Reader");
        IResourceManager rm = Minecraft.getInstance().getResourceManager();
        if (rm instanceof IReloadableResourceManager)
        {
            ((IReloadableResourceManager) rm).addReloadListener((ISelectiveResourceReloadListener) (resourceManager, resourcePredicate) -> {
                if (resourcePredicate.test(VanillaResourceType.TEXTURES))
                {
                    loadFashionPacks();
                }
            });
        }
    }

    private static List<ResourceLocation> hats = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> body = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> legs = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> boots = Lists.<ResourceLocation>newArrayList();

    private static List<ResourceLocation> weapons = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> items = Lists.<ResourceLocation>newArrayList();

    private static List<ResourceLocation> shields = Lists.<ResourceLocation>newArrayList();
    private static List<ResourceLocation> shieldsBlocking = Lists.<ResourceLocation>newArrayList();

    public void loadFashionPacks()
    {

        Fashion.log.debug("Loading up the Resource Pack Reader");

        hats.clear();
        body.clear();
        legs.clear();
        boots.clear();

        weapons.clear();
        items.clear();

        shields.clear();
        shieldsBlocking.clear();

        Fashion.log.debug("Cleared all lists");

        addHats(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_hat.png"));
        addBody(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_body.png"));
        addLegs(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_pants.png"));
        addBoots(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_boots.png"));

        addWeaponModel(null); // placeholder for empty spot
        addShieldModel(null, null); // placeholder for empty spot

        Fashion.log.debug("Added fail safe empty models");

        loadFromJson();
    }

    public static void addHats(ResourceLocation resLoc)
    {

        hats.add(resLoc);
    }

    public static void addBody(ResourceLocation resLoc)
    {

        body.add(resLoc);
    }

    public static void addLegs(ResourceLocation resLoc)
    {

        legs.add(resLoc);
    }

    public static void addBoots(ResourceLocation resLoc)
    {

        boots.add(resLoc);
    }

    public static void addWeaponItem(ResourceLocation model)
    {

        if (model != null)
        {
            items.add(model);
            Fashion.log.debug("added " + model.toString() + " for item rendering");
        }
    }

    public static void addWeaponModel(ResourceLocation model)
    {

        if (model == null)
        {
            weapons.add((ResourceLocation) null);
            Fashion.log.warn(String.format(
                    "%n TRIED REGISTERING A NULL WEAPON MODEL %n This is normal the first time for empty placeholders. %n If this happens more then once, check your Resource Pack json for any errors!"));
            return;
        }

        weapons.add(model);

        Fashion.log.debug("added " + model.toString());
    }

    public static void addShieldModel(ResourceLocation model, ResourceLocation modelBlocking)
    {

        if (model == null || modelBlocking == null)
        {
            shields.add((ResourceLocation) null);
            shieldsBlocking.add((ResourceLocation) null);

            Fashion.log.warn(String.format(
                    "%n TRIED REGISTERING A NULL SHIELD MODEL %n This is normal the first time for empty placeholders. %n If this happens more then once, check your Resource Pack json for any errors!"));
            return;
        }
        shields.add(model);
        shieldsBlocking.add(modelBlocking);

    }
    
    public static boolean isItem(ResourceLocation resLoc)
    {
        return items.contains(resLoc);
    }

    public static ResourceLocation getAestheticWeapon(ResourceLocation resloc)
    {

        for (ResourceLocation resLoc : weapons)
        {
            if (resLoc != null)
                if (resLoc.getPath().equals(resloc.getPath()))
                    return resLoc;
        }

        return rod;
    }

    public static ResourceLocation getAestheticShield(ResourceLocation resloc, boolean isBlocking)
    {

        ResourceLocation resloc_blocking = new ResourceLocation(resloc.getNamespace(), resloc.getPath() + "_blocking");

        for (ResourceLocation resource : isBlocking ? shieldsBlocking : shields)
        {
            if (resource != null)
                if (resource.getPath().equals(isBlocking ? resloc_blocking.getPath() : resloc.getPath()))
                    return resource;
        }

        return rod;
    }

    private void loadFromJson()
    {

        Fashion.log.debug("Reading out json files ...");

        try
        {
            List<IResource> jsons = Minecraft.getInstance().getResourceManager().getAllResources(new ResourceLocation(Fashion.MODID, "fashionpack.json"));
            Gson gson = new GsonBuilder().create();

            for (IResource res : jsons)
            {
                InputStream stream = res.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                JsonElement je = gson.fromJson(reader, JsonElement.class);
                JsonObject json = je.getAsJsonObject();

                if (!json.has("pack"))
                {
                    Fashion.log.warn(res.getPackName() + "'s fashion pack did not contain a pack id !");
                    continue;
                }
                String pack = json.get("pack").getAsString();

                Fashion.log.debug("Reading out " + pack + " ...");

                if (json.has("hats"))
                {
                    JsonArray array = json.getAsJsonArray("hats");
                    for (int i = 0; i < array.size(); i++)
                    {
                        String path = "textures/" + pack + "/hats/" + array.get(i).getAsString();
                        addHats(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("body"))
                {
                    JsonArray array = json.getAsJsonArray("body");
                    for (int i = 0; i < array.size(); i++)
                    {
                        String path = "textures/" + pack + "/body/" + array.get(i).getAsString();
                        addBody(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("pants"))
                {
                    JsonArray array = json.getAsJsonArray("pants");
                    for (int i = 0; i < array.size(); i++)
                    {
                        String path = "textures/" + pack + "/pants/" + array.get(i).getAsString();
                        addLegs(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("boots"))
                {
                    JsonArray array = json.getAsJsonArray("boots");
                    for (int i = 0; i < array.size(); i++)
                    {
                        String path = "textures/" + pack + "/boots/" + array.get(i).getAsString();
                        addBoots(new ResourceLocation(Fashion.MODID, path));
                    }
                }

                if (json.has("weapon_models"))
                {

                    JsonArray array = json.getAsJsonArray("weapon_models");

                    String path = pack + "/weapons/";
                    for (JsonElement el : array)
                    {
                        String name = el.getAsString();

                        boolean isItem = name.contains("item/");
                        name = name.replace("item/", "");

                        String fullpath = path + name;
                        ResourceLocation resLoc = new ResourceLocation(Fashion.MODID, fullpath);

                        addWeaponModel(resLoc);
                        if (isItem)
                            addWeaponItem(resLoc);
                    }
                }

                if (json.has("shield_models"))
                {
                    JsonArray array = json.getAsJsonArray("shield_models");
                    for (int i = 0; i < array.size(); i++)
                    {
                        String path = pack + "/shields/" + array.get(i).getAsString();
                        String pathBlock = path + "_blocking";
                        addShieldModel(new ResourceLocation(Fashion.MODID, path), new ResourceLocation(Fashion.MODID, pathBlock));
                    }
                }

            }
        }
        catch (IOException e)
        {
            Fashion.log.warn("************************************");
            Fashion.log.warn("!*!*!*!*!");
            Fashion.log.warn("No FashionPacks Detected. You will not be able to equip any fashion.");
            Fashion.log.warn("Make sure to select or set some in the resourcepack gui !");
            Fashion.log.warn("!*!*!*!*!");
            Fashion.log.warn("************************************");

            e.printStackTrace();
        }
    }

    public static ResourceLocation getNextClothes(EnumFashionSlot slot, ResourceLocation part_name)
    {

        List<ResourceLocation> resLocs = getListForSlot(slot);
        int index = 0;

        for (ResourceLocation name : resLocs)
        {
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

    public static ResourceLocation getPreviousClothes(EnumFashionSlot slot, ResourceLocation part_name)
    {

        List<ResourceLocation> resLocs = getListForSlot(slot);
        int index = 0;

        for (ResourceLocation name : resLocs)
        {
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

    public static List<ResourceLocation> getListForSlot(EnumFashionSlot slot)
    {

        switch (slot)
        {
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

    private static List<ResourceLocation> toArrayList(EnumFashionSlot slot)
    {

        List<ResourceLocation> resLoc = Lists.newArrayList();

        switch (slot)
        {
        case WEAPON:
            {

                for (ResourceLocation resource : weapons)
                    if (resource != null)
                        resLoc.add(resource);
                    else
                        resLoc.add(new ResourceLocation("missing"));
                break;
            }
        case SHIELD:
            {
                for (ResourceLocation resource : shields)
                    if (resource != null)
                        resLoc.add(resource);
                    else
                        resLoc.add(new ResourceLocation("missing"));
                break;
            }
        default:
            break;
        }

        return resLoc;
    }

    public static int getWeaponSize()
    {

        return weapons.size();
    }

    public static int getShieldSize()
    {

        return shields.size();
    }
}
