package me.arycer.eufoniatestai.poi;

import me.arycer.eufoniatestai.Main;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.Predicate;

public class PointsOfInterest {
    public static void register() {
        registerPOI("wood", Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.DARK_OAK_LOG, Blocks.ACACIA_LOG, Blocks.JUNGLE_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG);
    }

    private static void registerPOI(String id, Block... blocks) {
        final Identifier identifier = new Identifier(Main.MOD_ID, id);
        PointOfInterestHelper.register(identifier, 1, 1, blocks);
    }

    public static RegistryKey<PointOfInterestType> getPointOfInterest(String id) {
        boolean isPresent = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(Main.MOD_ID, id)) != null;
        if (isPresent) {
            return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(Main.MOD_ID, id));
        } else {
            throw new RuntimeException(String.format("POI %s not found", id));
        }
    }

    private static RegistryEntry<PointOfInterestType> getRegistryEntry(RegistryKey<PointOfInterestType> poi) {
        boolean isPresent = Registries.POINT_OF_INTEREST_TYPE.getEntry(poi).isPresent();
        if (isPresent) {
            return Registries.POINT_OF_INTEREST_TYPE.getEntry(poi).get();
        } else {
            throw new RuntimeException(String.format("POI %s not found", poi.getValue()));
        }
    }

    private static Predicate<RegistryEntry<PointOfInterestType>> getPredicate(RegistryKey<PointOfInterestType> poi) {
        return (poiEntry) -> poiEntry.getKey().equals(getRegistryEntry(poi).getKey());
    }

    public static BlockPos getNearestPOI(MobEntity entity, RegistryKey<PointOfInterestType> poi, int radius) {
        if (!(entity.getWorld() instanceof ServerWorld world)) return null;
        PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
        return poiStorage.getNearestPosition(getPredicate(poi), entity.getBlockPos(), radius, PointOfInterestStorage.OccupationStatus.ANY).orElse(null);
    }
}
