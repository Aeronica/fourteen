package net.aeronica.mods.fourteen.blocks;

import net.aeronica.mods.fourteen.Reference;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Reference.MOD_ID)
public class ModBlocks
{
    private ModBlocks() { /* NOP */ }

    @ObjectHolder("musicblock")
    public static MusicBlock MUSICBLOCK;
}
