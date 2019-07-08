package net.aeronica.mods.fourteen.blocks;

import net.aeronica.mods.fourteen.Fourteen;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Fourteen.MODID)
public class ModBlocks
{
    private ModBlocks() { /* NOP */ }

    @ObjectHolder("musicblock")
    public static MusicBlock MUSICBLOCK;
}
