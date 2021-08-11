package subaraki.fashion.mod;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigData {

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static
    {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static boolean bigger_model = false;
    public static boolean face_mirror = true;

    public static class ServerConfig {

        ServerConfig(ForgeConfigSpec.Builder builder) {

            builder.push("general");
            builder.pop();
        }
    }

    public static class ClientConfig {

        public final ForgeConfigSpec.BooleanValue bigger_model;
        public final ForgeConfigSpec.BooleanValue face_mirror;

        ClientConfig(ForgeConfigSpec.Builder builder) {

            builder.push("general");
            bigger_model = builder.comment("Pick true to show a larger model in the wardrobe screen").translation("translate.show.size").define("bigger_model",
                    true);
            face_mirror = builder.comment("Pick true to have the player look at the mirror, false for looking at the player")
                    .translation("translate.face.mirror").define("face_mirror", true);
            builder.pop();

        }
    }

    public static void refreshClient()
    {

        bigger_model = CLIENT.bigger_model.get();
        face_mirror = CLIENT.face_mirror.get();

    }

    public static void refreshServer()
    {

    }
}
