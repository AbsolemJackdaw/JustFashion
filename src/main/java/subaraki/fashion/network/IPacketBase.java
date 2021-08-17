package subaraki.fashion.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IPacketBase {

    void encode(PacketBuffer buf);

    void decode(PacketBuffer buf);

    void handle(Supplier<NetworkEvent.Context> context);

    void register(int id);

}