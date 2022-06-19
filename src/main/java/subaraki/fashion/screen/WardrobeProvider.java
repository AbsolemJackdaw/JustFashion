//package subaraki.fashion.screen;
//
//import javax.annotation.Nullable;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.inventory.container.Container;
//import net.minecraft.inventory.container.INamedContainerProvider;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.TranslationTextComponent;
//import net.minecraftforge.fml.network.NetworkHooks;
//import subaraki.fashion.mod.Fashion.ObjectHolders;
//
//public class WardrobeProvider implements INamedContainerProvider {
//
//    /**
//     * Used to open a new container with attached screen with
//     * {@link NetworkHooks#openGui(net.minecraft.entity.player.ServerPlayerEntity, INamedContainerProvider)
//     * openGui}
//     */
//    public WardrobeProvider() {
//
//    }
//
//    @Override
//    public ITextComponent getDisplayName() {
//
//        return new TranslationTextComponent("test");
//    }
//
//    @Nullable
//    @Override
//    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
//
//        return new WardrobeContainer(ObjectHolders.WARDROBECONTAINER, i);
//    }
//}
