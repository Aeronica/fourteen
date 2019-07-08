package net.aeronica.mods.fourteen.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

import java.util.List;

public class MusicBlock extends Block
{
    public MusicBlock()
    {
        super(Properties.create(Material.IRON)
             .sound(SoundType.METAL)
             .hardnessAndResistance(2.0F)
             .lightValue(14));
        setRegistryName("musicblock");
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isRemote)
        {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, SoundCategory.BLOCKS, 1F, 2F);
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }


}
