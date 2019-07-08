package net.aeronica.mods.fourteen.items;

import net.aeronica.mods.fourteen.Fourteen;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Fourteen.MODID)
public class ModItems
{
    private ModItems() { /* NOP */ }

    @ObjectHolder("musicitem")
    public static MusicItem MUSICITEM;

}
