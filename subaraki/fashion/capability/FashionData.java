package subaraki.fashion.capability;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.render.layer.LayerAestheticHeldItem;
import subaraki.fashion.render.layer.LayerWardrobe;

public class FashionData {

	private EntityPlayer player;

	private boolean renderFashion;
	private boolean inWardrobe;

	private int hatIndex;
	private int bodyIndex;
	private int legsIndex;
	private int bootsIndex; 
	private int weaponIndex;
	private int shieldIndex;

	public List<LayerRenderer> cachedOriginalRenderList = null;
	public List<LayerRenderer> fashionLayers = Lists.<LayerRenderer>newArrayList();

	private List<LayerRenderer> savedOriginalList = Lists.<LayerRenderer>newArrayList();
	public List<LayerRenderer> keepLayers = Lists.<LayerRenderer>newArrayList();

	public List<LayerRenderer> getSavedOriginalList() {
		List<LayerRenderer> copy = new ArrayList<>();
		copy = savedOriginalList;
		return copy;
	}
	
	public void resetSavedOriginalList(){
		savedOriginalList.clear();
	}

	public void saveVanillaList(List<LayerRenderer> original){
		List<LayerRenderer> copy = Lists.<LayerRenderer>newArrayList();

		//Remove unneeded layers
		for (LayerRenderer layer: original) 
		{
			if((layer instanceof LayerBipedArmor) || 
					(layer instanceof LayerWardrobe) ||
					(layer instanceof LayerCustomHead) ||
					(layer instanceof LayerDeadmau5Head)||
					(layer instanceof LayerHeldItem)||
					(layer instanceof LayerArrow)||
					(layer instanceof LayerCape)||
					(layer instanceof LayerElytra)||
					(layer instanceof LayerWardrobe))
				continue;

			copy.add(layer);
		}

		savedOriginalList.clear();

		for(LayerRenderer layer : copy)
			savedOriginalList.add(layer);
	}

	public FashionData(){

	}

	public EntityPlayer getPlayer() { 
		return player; 
	}

	public void setPlayer(EntityPlayer newPlayer){
		this.player = newPlayer;
	}

	public static FashionData get(EntityPlayer player){
		return player.getCapability(FashionCapability.CAPABILITY, null);
	}

	public NBTBase writeData(){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("renderFashion", renderFashion);
		tag.setInteger("hat", hatIndex);
		tag.setInteger("body", bodyIndex);
		tag.setInteger("legs", legsIndex);
		tag.setInteger("boots", bootsIndex);
		tag.setInteger("weapon", weaponIndex);
		tag.setInteger("shield", shieldIndex);
		return tag;
	}

	public void readData(NBTBase nbt){
		renderFashion = ((NBTTagCompound)nbt).getBoolean("renderFashion");
		hatIndex = ((NBTTagCompound)nbt).getInteger("hat");
		bodyIndex = ((NBTTagCompound)nbt).getInteger("body");
		legsIndex = ((NBTTagCompound)nbt).getInteger("legs");
		bootsIndex = ((NBTTagCompound)nbt).getInteger("boots");
		weaponIndex = ((NBTTagCompound)nbt).getInteger("weapon");
		shieldIndex = ((NBTTagCompound)nbt).getInteger("shield");

	}

	public boolean shouldRenderFashion() {
		return renderFashion;
	}
	public void setRenderFashion(boolean renderFashion) {
		this.renderFashion = renderFashion;
	}

	public int getPartIndex(EnumFashionSlot slot){
		switch(slot){
		case HEAD : return hatIndex;
		case CHEST : return bodyIndex;
		case LEGS : return legsIndex;
		case BOOTS : return bootsIndex;
		case WEAPON : return weaponIndex;
		case SHIELD : return shieldIndex;

		default : return 0;
		}
	}

	public int[] getAllParts(){
		return new int[]{hatIndex, bodyIndex, legsIndex, bootsIndex, weaponIndex, shieldIndex};
	}

	public void updatePartIndex(int id, EnumFashionSlot slot) {
		switch(slot){
		case HEAD : hatIndex = id; break;
		case CHEST : bodyIndex = id; break;
		case LEGS : legsIndex = id; break;
		case BOOTS : bootsIndex = id; break;
		case WEAPON : weaponIndex = id; break;
		case SHIELD : shieldIndex = id; break;

		default:
			break;
		}
	}

	public void setInWardrobe(boolean inWardrobe) {
		this.inWardrobe = inWardrobe;
	}

	public boolean isInWardrobe() {
		return inWardrobe;
	}
}
