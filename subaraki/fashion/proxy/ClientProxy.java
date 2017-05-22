package subaraki.fashion.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import subaraki.fashion.handler.ClientEventHandler;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.layer.LayerWardrobe;

public class ClientProxy extends ServerProxy implements IResourceManagerReloadListener{

	public static KeyBinding keyWardrobe;
	
	@Override
	public void init() {
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		if(manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)manager).registerReloadListener(this);
		}
	}
	@Override
	public void registerClientEvents() {
		new ClientEventHandler();
		
		String types[] = new String[]{"default","slim"};

		for(String type : types){
			RenderPlayer renderer = ((RenderPlayer)Minecraft.getMinecraft().getRenderManager().getSkinMap().get(type));
			renderer.addLayer(new LayerWardrobe(renderer));
		}
	}
	
	@Override
	public void registerKey() {
		keyWardrobe = new KeyBinding("Wardrobe", Keyboard.KEY_W, "Wardrobe");
		ClientRegistry.registerKeyBinding(keyWardrobe);
	}
	
	@Override
	public EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().player;
	}
	@Override
	public World getClientWorld(){
		return Minecraft.getMinecraft().world;
	}

	private static List<ResourceLocation> hats = Lists.<ResourceLocation>newArrayList();
	private static List<ResourceLocation> body = Lists.<ResourceLocation>newArrayList();
	private static List<ResourceLocation> legs = Lists.<ResourceLocation>newArrayList();
	private static List<ResourceLocation> boots = Lists.<ResourceLocation>newArrayList();
	private static List<ResourceLocation> weapon = Lists.<ResourceLocation>newArrayList();
	private static List<ResourceLocation> weaponTextures = Lists.<ResourceLocation>newArrayList();

	private static final ResourceLocation MISSING_FASHION = new ResourceLocation(Fashion.MODID,"fashion/missing_fasion.png");

	public static ResourceLocation getResourceForPart(EnumFashionSlot slot, int partIndex){
		switch (slot) {
		case HEAD : return partIndex >= hats.size() ? MISSING_FASHION : hats.get(partIndex);
		case CHEST : return partIndex >= body.size() ? MISSING_FASHION : body.get(partIndex);
		case LEGS : return partIndex >= legs.size() ? MISSING_FASHION : legs.get(partIndex);
		case BOOTS : return partIndex >= boots.size() ? MISSING_FASHION : boots.get(partIndex);
		case WEAPON : return partIndex >= weapon.size() ? MISSING_FASHION : weapon.get(partIndex);

		default: return MISSING_FASHION;
		}
	}
	
	public static ResourceLocation getTextureForStitcher(EnumFashionSlot slot, int partIndex){
		switch (slot) {
		case WEAPON : return partIndex >= weaponTextures.size() ? MISSING_FASHION : weaponTextures.get(partIndex);

		default: return MISSING_FASHION;
		}
	}

	public static void addHats(ResourceLocation resLoc){
		hats.add(resLoc);
	}
	public static void addBody(ResourceLocation resLoc){
		body.add(resLoc);
	}
	public static void addLegs(ResourceLocation resLoc){
		legs.add(resLoc);
	}
	public static void addBoots(ResourceLocation resLoc){
		boots.add(resLoc);
	}
	public static void addWeapon(ResourceLocation model, ResourceLocation texture){
		weapon.add(model);
		weaponTextures.add(texture);
	}

	public static int partsSize(EnumFashionSlot slot){
		switch (slot) {
		case HEAD : return hats.size();
		case CHEST : return body.size();
		case LEGS : return legs.size();
		case BOOTS : return boots.size();
		case WEAPON : return weapon.size();
		default: return 0;
		}
	}

	@Override
	public void loadFashionPacks() {
		hats.clear();
		body.clear();
		legs.clear();
		boots.clear();
		weapon.clear();

		ClientProxy.addHats(new ResourceLocation(Fashion.MODID,"textures/fashion/blank_hat.png"));
		ClientProxy.addBody(new ResourceLocation(Fashion.MODID,"textures/fashion/blank_body.png"));
		ClientProxy.addLegs(new ResourceLocation(Fashion.MODID,"textures/fashion/blank_pants.png"));
		ClientProxy.addBoots(new ResourceLocation(Fashion.MODID,"textures/fashion/blank_boots.png"));
		ClientProxy.addWeapon(new ResourceLocation(Fashion.MODID,"blank_weapon"), null);

		loadFromJson();
	}

	private void loadFromJson(){
		try {
			List<IResource> jsons = Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation(Fashion.MODID,"fashionpack.json"));
			Gson gson = new GsonBuilder().create();

			for(IResource res : jsons){
				InputStream stream = res.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				JsonElement je = gson.fromJson(reader, JsonElement.class);
				JsonObject json = je.getAsJsonObject();

				if(!json.has("pack")){
					Fashion.log.warn(res.getResourcePackName()+"'s fashion pack did not contain a pack id !");
					continue;
				}
				String pack = json.get("pack").getAsString();

				if(json.has("hats")){
					JsonArray array = json.getAsJsonArray("hats");
					for(int i = 0; i < array.size(); i++){
						String path = "textures/fashionpack/"+pack+"/hats/"+array.get(i).getAsString();
						addHats(new ResourceLocation(Fashion.MODID,path));
					}
				}

				if(json.has("body")){
					JsonArray array = json.getAsJsonArray("body");
					for(int i = 0; i < array.size(); i++){
						String path = "textures/fashionpack/"+pack+"/body/"+array.get(i).getAsString();
						addBody(new ResourceLocation(Fashion.MODID, path));
					}
				}

				if(json.has("pants")){
					JsonArray array = json.getAsJsonArray("pants");
					for(int i = 0; i < array.size(); i++){
						String path = "textures/fashionpack/"+pack+"/pants/"+array.get(i).getAsString();
						addLegs(new ResourceLocation(Fashion.MODID, path));
					}
				}

				if(json.has("boots")){
					JsonArray array = json.getAsJsonArray("boots");
					for(int i = 0; i < array.size(); i++){
						String path = "textures/fashionpack/"+pack+"/boots/"+array.get(i).getAsString();
						addBoots(new ResourceLocation(Fashion.MODID, path));
					}
				}
				
				//prefixes 'models' and 'textures' aren't needed here. the stitch event and modelloader automatically add that
				if(json.has("weapons")){
					JsonArray array = json.getAsJsonArray("weapons");
					for(int i = 0; i < array.size(); i++){
						String path = "fashionpack/"+pack+"/weapons/"+array.get(i).getAsString();
						String texturePath = "fashionpack/"+pack+"/weapons/"+array.get(i).getAsString();
						addWeapon(new ResourceLocation(Fashion.MODID, path), new ResourceLocation(Fashion.MODID, texturePath));
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
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		loadFashionPacks();
	}
}
