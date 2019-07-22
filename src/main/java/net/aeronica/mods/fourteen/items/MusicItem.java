package net.aeronica.mods.fourteen.items;

import net.aeronica.libs.mml.core.TestData;
import net.aeronica.mods.fourteen.Fourteen;
import net.aeronica.mods.fourteen.audio.ClientAudio;
import net.aeronica.mods.fourteen.caps.ILivingEntityModCap;
import net.aeronica.mods.fourteen.caps.LivingEntityModCapProvider;
import net.aeronica.mods.fourteen.managers.PlayIdSupplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class MusicItem extends Item
{
    private static final Logger LOGGER = LogManager.getLogger();
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
            if (!playerIn.isSneaking())
                LivingEntityModCapProvider.getLivingEntityModCap(playerIn).ifPresent(livingCap -> {
                    livingCap.setPlayId((int) worldIn.getDayTime());
                });
            else
                LivingEntityModCapProvider.getLivingEntityModCap(playerIn).ifPresent(ILivingEntityModCap::synchronise);

        } else if (!playerIn.isSneaking())
        {
            ClientAudio.playLocal(PlayIdSupplier.PlayType.BACKGROUND.getAsInt(), TestData.MML14.getMML(), null);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
