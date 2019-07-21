package net.aeronica.mods.fourteen.setup;

import net.aeronica.mods.fourteen.Reference;
import net.aeronica.mods.fourteen.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class Setup
{
    public Setup() { /* NOP */ }

    public ItemGroup itemGroup = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.MUSICBLOCK);
        }
    };
}
