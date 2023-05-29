package me.arycer.eufoniatestai.entity;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.entity.custom.BirdEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BirdEntity> BIRD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Main.MOD_ID, "bird"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BirdEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 2f))
                    .build()
    );
}
