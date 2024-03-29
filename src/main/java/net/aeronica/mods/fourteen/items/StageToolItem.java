package net.aeronica.mods.fourteen.items;

import net.aeronica.mods.fourteen.caps.LivingEntityModCapProvider;
import net.aeronica.mods.fourteen.caps.stages.ServerStageAreaProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class StageToolItem extends Item
{
    private static final Logger LOGGER = LogManager.getLogger();
    public StageToolItem(Properties properties)
    {
        super(properties);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn)
    {
        if (!worldIn.isClientSide)
        {
            if (!playerIn.isShiftKeyDown())
            {
                //PacketDispatcher.sendTo(new OpenScreenMessage(OpenScreenMessage.SM.TEST_ONE), (ServerPlayerEntity) playerIn);
                ServerStageAreaProvider.getServerStageAreas(worldIn).ifPresent(p->{
                    p.test();
                    p.setInt(worldIn.getRandom().nextInt(10));
                });
                LivingEntityModCapProvider.getLivingEntityModCap(playerIn).ifPresent(p->{
                    p.setPlayId(worldIn.getRandom().nextInt(10));
                });
            }
            else
            {
                //PacketDispatcher.sendTo(new OpenScreenMessage(OpenScreenMessage.SM.TEST_TWO), (ServerPlayerEntity) playerIn);
            }

        } else if (!playerIn.isShiftKeyDown())
        {
            // nop
        } else
        {
            LivingEntityModCapProvider.getLivingEntityModCap(playerIn).ifPresent(p->{
                ServerStageAreaProvider.getServerStageAreas(worldIn).ifPresent(n->{
                    LOGGER.debug("playId: {}, someInt: {}", p.getPlayId(), n.getInt());
                });
            });
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        return super.useOn(context);
    }
}
