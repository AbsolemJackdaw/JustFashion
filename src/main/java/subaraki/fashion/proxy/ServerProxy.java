package subaraki.fashion.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ServerProxy{

	public void init(){};
	
	public EntityPlayer getClientPlayer() {
		return null;
	}
	public World getClientWorld() {
		return null;
	}
	public void loadFashionPacks() {}

	public void registerClientEvents() {};
	
	public void registerKey(){};
}