package net.aeronica.mods.fourteen.items;

import net.aeronica.mods.fourteen.Fourteen;
import net.aeronica.mods.fourteen.audio.MovingMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class MusicItem extends Item
{

    public MusicItem()
    {
        super(new Item.Properties()
             .maxStackSize(1)
             .group(Fourteen.setup.itemGroup));
        setRegistryName("musicitem");
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn)
    {
        if (!worldIn.isRemote)
        {
            //worldIn.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, SoundCategory.PLAYERS, 1F, 1F);
        } else
        {
            Minecraft.getInstance().getSoundHandler().play(new MovingMusic());
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
