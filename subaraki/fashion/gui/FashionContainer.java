package subaraki.fashion.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class FashionContainer extends Container {

	public EntityPlayer player;
	
	public FashionContainer(EntityPlayer player) {
		this.player = player;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
