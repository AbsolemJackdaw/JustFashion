package subaraki.fashion.util;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.EnumFashionSlot;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ResourcePackReader extends SimplePreparableReloadListener<ArrayList<JsonObject>> {

    private static final ResourceLocation rod = new ResourceLocation("item/blaze_rod");
    private static final List<ResourceLocation> hats = Lists.newArrayList();
    private static final List<ResourceLocation> body = Lists.newArrayList();
    private static final List<ResourceLocation> legs = Lists.newArrayList();
    private static final List<ResourceLocation> boots = Lists.newArrayList();
    private static final List<ResourceLocation> weapons = Lists.newArrayList();
    private static final List<ResourceLocation> items = Lists.newArrayList();
    private static final List<ResourceLocation> shields = Lists.newArrayList();
    private static final List<ResourceLocation> shieldsBlocking = Lists.newArrayList();
    private static final Gson GSON = new GsonBuilder().create();

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        Fashion.log.info("Registering Resource pack Reader for Reload");
        event.registerReloadListener(new ResourcePackReader());
    }

    public static boolean isItem(ResourceLocation resLoc) {
        return items.contains(resLoc);
    }

    public static ResourceLocation getAestheticShield(ResourceLocation resloc, boolean isBlocking) {

        ResourceLocation resloc_blocking = new ResourceLocation(resloc.getNamespace(), resloc.getPath() + "_blocking");

        for (ResourceLocation resource : isBlocking ? shieldsBlocking : shields) {
            if (resource != null)
                if (resource.getPath().equals(isBlocking ? resloc_blocking.getPath() : resloc.getPath()))
                    return resource;
        }

        return rod;
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

        return switch (slot) {
            case HEAD -> hats;
            case CHEST -> body;
            case LEGS -> legs;
            case BOOTS -> boots;
            case WEAPON -> weapons;
            case SHIELD -> shields;
        };
    }

    private static List<ResourceLocation> toArrayList(EnumFashionSlot slot) {

        List<ResourceLocation> resLoc = Lists.newArrayList();

        switch (slot) {
            case WEAPON -> resLoc.addAll(weapons);
            case SHIELD -> resLoc.addAll(shields);
        }

        return resLoc;
    }

    public static int getWeaponSize() {

        return weapons.size();
    }

    public static int getShieldSize() {

        return shields.size();
    }

    public void addHats(ResourceLocation resLoc) {
        hats.add(resLoc);
    }

    public void addHats(Collection<ResourceLocation> all) {
        hats.addAll(all);
    }

    public void addBody(ResourceLocation resLoc) {
        body.add(resLoc);
    }

    public void addBody(Collection<ResourceLocation> all) {
        body.addAll(all);
    }

    public void addLegs(ResourceLocation resLoc) {
        legs.add(resLoc);
    }

    public void addLegs(Collection<ResourceLocation> all) {
        legs.addAll(all);
    }

    public void addBoots(ResourceLocation resLoc) {
        boots.add(resLoc);
    }

    public void addBoots(Collection<ResourceLocation> all) {
        boots.addAll(all);
    }

    public void addWeaponItem(ResourceLocation model) {

        if (model != null) {
            items.add(model);
            Fashion.log.info("added " + model + " for item rendering");
        }
    }

    public void addWeaponModel(ResourceLocation model) {

        if (model == null) {
            weapons.add(null);
            Fashion.log.warn(String.format(
                    "%n TRIED REGISTERING A NULL WEAPON MODEL %n This is normal the first time for empty placeholders. %n If this happens more then once, check your Resource Pack json for any errors!"));
            return;
        }

        weapons.add(model);

        Fashion.log.info("added " + model);
    }

    public void addShieldModel(ResourceLocation model, ResourceLocation modelBlocking) {

        if (model == null || modelBlocking == null) {
            shields.add(null);
            shieldsBlocking.add(null);

            Fashion.log.warn(String.format(
                    "%n TRIED REGISTERING A NULL SHIELD MODEL %n This is normal the first time for empty placeholders. %n If this happens more then once, check your Resource Pack json for any errors!"));
            return;
        }
        shields.add(model);
        shieldsBlocking.add(modelBlocking);

    }

    public void initFashion() {

        Fashion.log.info("Loading up the Resource Pack Reader");

        hats.clear();
        body.clear();
        legs.clear();
        boots.clear();

        Fashion.log.info("Cleared all Fashion lists");

        addHats(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_hat.png"));
        addBody(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_body.png"));
        addLegs(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_pants.png"));
        addBoots(new ResourceLocation(Fashion.MODID, "textures/fashion/blank_boots.png"));

        Fashion.log.info("Added fail safe empty Fashion Clothes");
    }

    public void initModels() {
        weapons.clear();
        items.clear();

        shields.clear();
        shieldsBlocking.clear();

        Fashion.log.info("Cleared all Model lists");
        addWeaponModel(new ResourceLocation("missing")); // placeholder for empty spot
        addShieldModel(new ResourceLocation("missing"), new ResourceLocation("missing")); // placeholder for empty spot

        Fashion.log.info("Added fail safe empty Fashion Weapons");
    }

    public ArrayList<JsonObject> prepare(ResourceManager resourceManager) {
        ArrayList<JsonObject> theJsonFiles = Lists.newArrayList();
        Set<String> packnames = new TreeSet<>();
        for (Resource res : resourceManager.getResourceStack(new ResourceLocation(Fashion.MODID, "fashionpack.json"))) {
            if (!packnames.contains(res.sourcePackId())) {
                packnames.add(res.sourcePackId());
                try (BufferedReader stream = res.openAsReader()) {
                    JsonElement je = GSON.fromJson(stream, JsonElement.class);
                    JsonObject json = je.getAsJsonObject();
                    if (json.has("pack")) {
                        theJsonFiles.add(json);
                    } else {
                        Fashion.log.warn(res.sourcePackId() + "'s fashion pack did not contain a pack id !");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return theJsonFiles;
    }

    public void fillFashionLists(ArrayList<JsonObject> jsonObjects) {
        if (!jsonObjects.isEmpty()) {
            //TODO check if runnable is needed
            Runnable run = () -> {
                for (JsonObject json : jsonObjects) {
                    if (json.has("pack")) {

                        String pack = json.get("pack").getAsString();

                        Fashion.log.info("Reading out Fashion " + pack + " ...");

                        addHats(get("hats", json, pack));
                        addBody(get("body", json, pack));
                        addLegs(get("pants", json, pack));
                        addBoots(get("boots", json, pack));

                    }
                }
            };
            run.run();
        }
    }

    public void fillWeaponAndShieldLists(ArrayList<JsonObject> jsonObjects) {
        if (!jsonObjects.isEmpty()) {
            for (JsonObject json : jsonObjects) {
                if (json.has("pack")) {

                    String pack = json.get("pack").getAsString();

                    Fashion.log.info("Reading out Weapons and Shields from " + pack + " ...");

                    if (json.has("weapon_models")) {

                        JsonArray array = json.getAsJsonArray("weapon_models");

                        String path = pack + "/weapons/";
                        for (JsonElement el : array) {
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

                    if (json.has("shield_models")) {
                        JsonArray array = json.getAsJsonArray("shield_models");
                        for (int i = 0; i < array.size(); i++) {
                            String path = pack + "/shields/" + array.get(i).getAsString();
                            String pathBlock = path + "_blocking";
                            addShieldModel(new ResourceLocation(Fashion.MODID, path), new ResourceLocation(Fashion.MODID, pathBlock));
                        }
                    }
                }
            }
        }
    }

    public void registerSpecialModels() {

        Fashion.log.info("Firing Special Model Registry Event");
        int size_weapons = ResourcePackReader.getWeaponSize();
        int size_shields = ResourcePackReader.getShieldSize();

        Fashion.log.info("Weapons to load : " + size_weapons);
        Fashion.log.info("Shields to load : " + size_shields);

        List<ResourceLocation> theList;

        theList = ResourcePackReader.getListForSlot(EnumFashionSlot.WEAPON);
        for (ResourceLocation resLoc : theList) {

            Fashion.log.info(String.format("Weapon Registry %s", resLoc));

            ForgeModelBakery.addSpecialModel(resLoc);
        }

        theList = ResourcePackReader.getListForSlot(EnumFashionSlot.SHIELD);
        for (ResourceLocation resLoc : theList) {
            Fashion.log.info(String.format("ShieldRegistry %s", resLoc));

            for (int i = 0; i < 2; i++) {
                boolean blocking = i == 0;
                ForgeModelBakery.addSpecialModel(ResourcePackReader.getAestheticShield(resLoc, blocking));
            }
        }
    }

    public Collection<ResourceLocation> get(String element, JsonObject json, String pack) {

        ArrayList<ResourceLocation> collection = Lists.newArrayList();
        if (json.has(element)) {
            JsonArray array = json.getAsJsonArray(element);
            for (int i = 0; i < array.size(); i++) {
                String path = "textures/" + pack + "/" + element + "/" + array.get(i).getAsString();
                collection.add(new ResourceLocation(Fashion.MODID, path));
            }
        }
        return collection;
    }

    /**
     * Normally we're supposed to prepare a list of all objects in 'prepare' and apply them in 'apply' to win on time and resources,
     * problem being that the special model loader is fired _before_ apply, so we're just better off doing everything in prepare sadly.
     * no optimization really possible here.
     */
    @Nonnull
    @Override
    protected ArrayList<JsonObject> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        initFashion();
        initModels();
        ArrayList<JsonObject> prepareLists = prepare(resourceManager);
        fillFashionLists(prepareLists);
        fillWeaponAndShieldLists(prepareLists);
        //model lists have to be prepared for special models to register
        registerSpecialModels();
        return prepareLists;
    }

    @Override
    protected void apply(ArrayList<JsonObject> jsonObjects, ResourceManager resourceManager, ProfilerFiller profilerFiller) {

    }
}
