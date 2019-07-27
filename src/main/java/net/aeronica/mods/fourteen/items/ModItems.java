package net.aeronica.mods.fourteen.items;

import net.aeronica.mods.fourteen.Reference;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Reference.MOD_ID)
public class ModItems
{
    private ModItems() { /* NOP */ }

    @ObjectHolder("musicitem")
    public static MusicItem MUSICITEM;

    @ObjectHolder("guitestitem")
    public static MusicItem GUITESTITEM;

}
