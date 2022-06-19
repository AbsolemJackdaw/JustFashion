//package subaraki.fashion.network.server;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Supplier;
//
//import subaraki.fashion.network.IPacketBase;
//import net.minecraft.network.PacketBuffer;
//import net.minecraftforge.fml.network.NetworkEvent.Context;
//import subaraki.fashion.capability.FashionData;
//import subaraki.fashion.network.NetworkHandler;
//
//public class PacketSyncSavedListToServer implements IPacketBase {
//
//    private List<String> names = new ArrayList<String>();
//
//    public PacketSyncSavedListToServer() {
//
//    }
//
//    public PacketSyncSavedListToServer(PacketBuffer buf) {
//
//        decode(buf);
//    }
//
//    public PacketSyncSavedListToServer(List<String> names) {
//
//        this.names = names;
//    }
//
//    @Override
//    public void encode(PacketBuffer buf) {
//
//        buf.writeInt(names.size());
//
//        if (!names.isEmpty())
//            for (String name : names)
//                buf.writeUtf(name);
//
//    }
//
//    @Override
//    public void decode(PacketBuffer buf) {
//
//        int size = buf.readInt();
//
//        if (size > 0)
//            for (int i = 0; i < size; i++)
//                names.add(buf.readUtf(128));
//    }
//
//    @Override
//    public void handle(Supplier<Context> context) {
//
//        context.get().enqueueWork(() -> {
//            FashionData.get(context.get().getSender()).ifPresent(data -> {
//                data.saveVanillaListServer(names);
//            });
//        });
//
//        context.get().setPacketHandled(true);
//    }
//
//    @Override
//    public void register(int id) {
//
//        NetworkHandler.NETWORK.registerMessage(id, PacketSyncSavedListToServer.class, PacketSyncSavedListToServer::encode, PacketSyncSavedListToServer::new,
//                PacketSyncSavedListToServer::handle);
//    }
//
//}
