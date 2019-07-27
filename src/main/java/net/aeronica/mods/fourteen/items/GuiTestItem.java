package net.aeronica.mods.fourteen.items;

import net.aeronica.mods.fourteen.Fourteen;
import net.aeronica.mods.fourteen.gui.TestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class GuiTestItem extends Item
{
    private static final Logger LOGGER = LogManager.getLogger();
    public GuiTestItem()
    {
        super(new Properties()
             .maxStackSize(1)
             .group(Fourteen.setup.itemGroup));
        setRegistryName("guitestitem");
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn)
    {
        if (!worldIn.isRemote)
        {
            if (!playerIn.isSneaking())
            {
                // nop
            }
            else
            {
                // nop
            }

        } else if (!playerIn.isSneaking())
        {
            Minecraft mc = Minecraft.getInstance();
            //mc.enqueue(()->mc.displayGuiScreen(new OptionsScreen(null, Minecraft.getInstance().gameSettings)));
            mc.enqueue(()->mc.displayGuiScreen(new TestScreen(null)));
        } else
        {
            // nop
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

}
