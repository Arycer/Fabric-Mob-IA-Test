package me.arycer.eufoniatestai.entity;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.client.entity.BirdRenderer;
import me.arycer.eufoniatestai.entity.custom.AngelEntity;
import me.arycer.eufoniatestai.client.entity.AngelRenderer;
import me.arycer.eufoniatestai.entity.custom.BirdEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    private static final EntityType<BirdEntity> BIRD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Main.MOD_ID, "bird"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BirdEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    private static final EntityType<AngelEntity> ANGEL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Main.MOD_ID, "angel"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AngelEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 0.75f))
                    .build()
    );

    public static void registerEntities() {
        FabricDefaultAttributeRegistry.register(ModEntities.BIRD, BirdEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.ANGEL, AngelEntity.setAttributes());
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(ModEntities.BIRD, BirdRenderer::new);
        EntityRendererRegistry.register(ModEntities.ANGEL, AngelRenderer::new);
    }
}
