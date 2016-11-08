package subaraki.fashion.capability;

import java.util.List;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.PacketSyncFashionToTrackedPlayers;

public class FashionData {

	private EntityPlayer player;

	private boolean renderFashion;

	private int hatIndex;
	private int bodyIndex;
	private int legsIndex;
	private int bootsIndex; 

	public List<LayerRenderer> cachedOriginalRenderList = null;
	
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
		return tag;
	}

	public void readData(NBTBase nbt){
		renderFashion = ((NBTTagCompound)nbt).getBoolean("renderFashion");
		hatIndex = ((NBTTagCompound)nbt).getInteger("hat");
		bodyIndex = ((NBTTagCompound)nbt).getInteger("body");
		legsIndex = ((NBTTagCompound)nbt).getInteger("legs");
		bootsIndex = ((NBTTagCompound)nbt).getInteger("boots");
	}

	public boolean shouldRenderFashion() {
		return renderFashion;
	}
	public void setRenderFashion(boolean renderFashion) {
		this.renderFashion = renderFashion;
	}

	public int getPartIndex(int slot){
		switch(slot){
		case 0 : return hatIndex;
		case 1 : return bodyIndex;
		case 2 : return legsIndex;
		case 3 : return bootsIndex;
		default : return 0;
		}
	}
	
	public int[] getAllParts(){
		return new int[]{hatIndex, bodyIndex, legsIndex, bootsIndex};
	}

	public void updatePartIndex(int id, int slot) {
		switch(slot){
		case 0 : hatIndex = id; break;
		case 1 : bodyIndex = id; break;
		case 2 : legsIndex = id; break;
		case 3 : bootsIndex = id; break;
		}
	}
}
