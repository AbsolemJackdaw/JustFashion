package subaraki.fashion.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockDirt;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.handler.ClientEventHandler;
import subaraki.fashion.handler.GuiHandler;
import subaraki.fashion.handler.PlayerTracker;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.proxy.ServerProxy;

@Mod(name = Fashion.NAME, modid = Fashion.MODID, version = Fashion.VERSION, dependencies = Fashion.DEPENDENCY)
public class Fashion {

	public static final String MODID = "fashion";
	public static final String NAME = "fashion mod";
	public static final String VERSION = "1.10.2 1.0.0.1";
	public static final String DEPENDENCY = "required-after:subcommonlib";

	public static final String FASHIONPACK = "fashion packs";

	public static Logger log = LogManager.getLogger(MODID);

	@SidedProxy(clientSide = "subaraki.fashion.proxy.ClientProxy", serverSide = "subaraki.fashion.proxy.ServerProxy")
	public static ServerProxy proxy;

	public Fashion() {
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);
		proxy.init();
		
		new NetworkHandler();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		new FashionCapability().register();
		
		new PlayerTracker();
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.registerClientEvents();
	}
	
	@SubscribeEvent
	public void onclickstuff(PlayerInteractEvent event){
		if(event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockDirt){
			FMLNetworkHandler.openGui(event.getEntityPlayer(), this, 0, event.getWorld(), 0, 0, 0);
		}
	}
}
