package net.aeronica.mods.fourteen.blocks;

import net.aeronica.mods.fourteen.Fourteen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class InvTestContainer extends Container
{
    private TileEntity tileEntity;
    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;
    private int guiX = 10;
    private int guiY = 70;

    public InvTestContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory ,PlayerEntity playerEntity)
    {
        super(Fourteen.ObjectHolders.INV_TEST_CONTAINER, windowId);
        this.playerEntity = playerEntity;
        this.playerInventory = new InvWrapper(playerInventory);
        tileEntity = world.getTileEntity(pos);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 64, 24));
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, j * 18 + guiX, i * 18 + guiY));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, i * 18 + guiX, guiY + 58));
        }
    }

    public ITextComponent getName()
    {
        if (tileEntity != null && tileEntity.getWorld().isRemote)
            return ((InvTestTile)tileEntity).getName();
        return new StringTextComponent("");
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerEntity, Fourteen.ObjectHolders.INV_TEST_BLOCK);
    }

    // Transfer the item in test slot to-from any other open slot
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 0)
            {
                if (!this.mergeItemStack(stack, 1, 37, false))
                    return ItemStack.EMPTY;
                slot.onSlotChange(stack, itemstack);
            }
            else if (!this.mergeItemStack(stack, 0, 1, false))
            {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
}
