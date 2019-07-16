package net.aeronica.mods.fourteen.blocks;

import net.aeronica.libs.mml.core.TestData;
import net.aeronica.mods.fourteen.audio.ClientAudio;
import net.aeronica.mods.fourteen.managers.PlayIdSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

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
            // worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, SoundCategory.BLOCKS, 1F, 2F);
        } else
        {
            ClientAudio.play(PlayIdSupplier.PlayType.PLAYERS.getAsInt(), pos, TestData.MML10.getMML());
        }
        return true;
    }


}
