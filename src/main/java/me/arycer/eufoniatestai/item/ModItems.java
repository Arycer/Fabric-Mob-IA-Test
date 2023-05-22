package me.arycer.eufoniatestai.item;

import me.arycer.eufoniatestai.Main;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, name), item);
    }
}