package net.aeronica.mods.fourteen.items;

import net.aeronica.libs.mml.core.TestData;
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
import java.util.Random;

public class MusicItem extends Item
{
    private static final Random rand = new Random();
    private static final Logger LOGGER = LogManager.getLogger();
    private static int lastPlayID;
    public MusicItem(Item.Properties properties)
    {
        super(properties);
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
            {
                LivingEntityModCapProvider.getLivingEntityModCap(playerIn).ifPresent(ILivingEntityModCap::synchronise);
            }

        } else if (!playerIn.isSneaking())
        {
            int newPlayId = PlayIdSupplier.PlayType.BACKGROUND.getAsInt();
            lastPlayID = newPlayId;
            ClientAudio.playLocal(newPlayId, getRandomMML(), null);
        } else
        {
            ClientAudio.stop(lastPlayID);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    private String getRandomMML()
    {
        int index = rand.nextInt(TestData.values().length);
        LOGGER.debug("MusicItem: song: {}", TestData.getMML(index).getTitle());
        return TestData.getMML(index).getMML();
    }
}
