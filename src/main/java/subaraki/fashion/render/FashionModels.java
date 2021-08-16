package subaraki.fashion.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.fashion.mod.Fashion;

@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FashionModels {

    public static ModelLayerLocation HEAD_MODEL_LOCATION = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "fashion_head");
    public static ModelLayerLocation NORML_MODEL_LOCATION = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "fashion_norm");
    public static ModelLayerLocation SMALL_MODEL_LOCATION = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "fashion_small");
    public static ModelLayerLocation BOOTS_MODEL_LOCATION = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "fashion_boots");
    public static ModelLayerLocation LEGS_MODEL_LOCATION = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "fashion_legs");

    @SubscribeEvent
    public static void clientSetup(EntityRenderersEvent.RegisterLayerDefinitions event) {

        LayerDefinition head = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.51f), false), 64, 16);
        LayerDefinition norml = LayerDefinition.create(createBody(new CubeDeformation(0.27f), false), 56, 32);
        LayerDefinition small = LayerDefinition.create(createBody(new CubeDeformation(0.27f), true), 56, 32);
        LayerDefinition legs = LayerDefinition.create(createLegs(new CubeDeformation(0.26f)), 56, 32);
        LayerDefinition boots = LayerDefinition.create(createBoots(new CubeDeformation(0.27f)), 32, 32);

        event.registerLayerDefinition(HEAD_MODEL_LOCATION, () -> head);
        event.registerLayerDefinition(NORML_MODEL_LOCATION, () -> norml);
        event.registerLayerDefinition(SMALL_MODEL_LOCATION, () -> small);
        event.registerLayerDefinition(BOOTS_MODEL_LOCATION, () -> boots);
        event.registerLayerDefinition(LEGS_MODEL_LOCATION, () -> legs);

    }

    public static MeshDefinition createBody(CubeDeformation cubeDeformation, boolean smallArms) {
        MeshDefinition var2 = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition var3 = var2.getRoot();
        if (smallArms) {
            var3.addOrReplaceChild("left_arm", CubeListBuilder.create().mirror().texOffs(24, 0).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(5.0F, 2.5F, 0.0F));
            var3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(-5.0F, 2.5F, 0.0F));
            var3.addOrReplaceChild("left_sleeve", CubeListBuilder.create().mirror().texOffs(24, 16).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(5.0F, 2.5F, 0.0F));
            var3.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
        } else {
            var3.addOrReplaceChild("left_arm", CubeListBuilder.create().mirror().texOffs(24, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(5.0F, 2.0F, 0.0F));
            var3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(-5.0F, 2.0F, 0.0F));
            var3.addOrReplaceChild("left_sleeve", CubeListBuilder.create().mirror().texOffs(24, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));
            var3.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        }
        var3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        var3.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO);

        //keep in so player model can find these and not crash the game
        var3.addOrReplaceChild("ear", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("cloak", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        return var2;
    }

    public static MeshDefinition createLegs(CubeDeformation cubeDeformation) {
        MeshDefinition var2 = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition var3 = var2.getRoot();
        var3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 00).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));

        var3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(00, 00).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(1.9F, 12.0F, 0.0F));
        var3.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 00).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(-1.9F, 12.0F, 0.0F));
        var3.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        var3.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(00, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        //keep in so player model can find these and not crash the game
        var3.addOrReplaceChild("ear", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("cloak", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation, 1.0F, 0.5F), PartPose.ZERO);
        var3.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        return var2;
    }

    public static MeshDefinition createBoots(CubeDeformation cubeDeformation) {
        MeshDefinition var2 = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition var3 = var2.getRoot();
        var3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 00).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(1.9F, 12.0F, 0.0F));
        var3.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(00, 00).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(-1.9F, 12.0F, 0.0F));
        var3.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        var3.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        //keep in so player model can find these and not crash the game
        var3.addOrReplaceChild("ear", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("cloak", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation, 1.0F, 0.5F), PartPose.ZERO);
        var3.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        var3.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0, cubeDeformation), PartPose.ZERO);
        return var2;
    }
}


