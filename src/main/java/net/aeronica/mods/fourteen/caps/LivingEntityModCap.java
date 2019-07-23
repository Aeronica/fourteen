package net.aeronica.mods.fourteen.caps;

import net.aeronica.mods.fourteen.managers.PlayIdSupplier;
import net.aeronica.mods.fourteen.network.LivingEntityModCapSync;
import net.aeronica.mods.fourteen.network.PacketDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;

import javax.annotation.Nullable;

public class LivingEntityModCap implements ILivingEntityModCap
{
    private int playId = PlayIdSupplier.INVALID;
    private final LivingEntity entity;

    LivingEntityModCap(@Nullable final LivingEntity entity)
    {
        this.entity = entity;
    }

    @Override
    public void setPlayId(int playId)
    {
        this.playId = playId;
        synchronise();
    }

    @Override
    public int getPlayId()
    {
        return playId;
    }


    @Override
    public void synchronise()
    {
        if (entity == null) return;
        World world = entity.world;
        if (world.isRemote) return;
        Dimension dimension = world.dimension;
        PacketDispatcher.sendToDimension(new LivingEntityModCapSync(playId), dimension);
    }
}
